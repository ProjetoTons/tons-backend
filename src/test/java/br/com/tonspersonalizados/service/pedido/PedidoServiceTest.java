package br.com.tonspersonalizados.service.pedido;

import br.com.tonspersonalizados.dto.pedidos.*;
import br.com.tonspersonalizados.entity.pedidos.HistoricoEtapaPedido;
import br.com.tonspersonalizados.entity.pedidos.Pedido;
import br.com.tonspersonalizados.entity.produtos.Produto;
import br.com.tonspersonalizados.entity.usuarios.Empresa;
import br.com.tonspersonalizados.entity.usuarios.Endereco;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.event.EtapaAvancadaEvent;
import br.com.tonspersonalizados.exception.pedido.PedidoNaoEncontradoException;
import br.com.tonspersonalizados.exception.usuarios.UsuarioNaoEncontradoException;
import br.com.tonspersonalizados.repository.pedido.*;
import br.com.tonspersonalizados.repository.produto.ProdutoRepository;
import br.com.tonspersonalizados.repository.usuarios.EnderecoRepository;
import br.com.tonspersonalizados.repository.usuarios.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes do PedidoService — cobertura máxima com o mínimo de testes.
 * Estratégia: UM teste de "criar pedido completo" (cliente+empresa, responsável,
 * endereço, item com características) exercita criarPedido + os métodos privados
 * criarItemPedido e montarPedidoResponse pelos ramos "verdadeiros".
 * Já o listarTodos usa um pedido "pelado", cobrindo os ramos "falsos" de
 * montarPedidoResponse. Os padrões de exceção repetidos têm um representante cada.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService")
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private ItemPedidoRepository itemPedidoRepository;
    @Mock private CaracteristicasItemPedidoRepository caracteristicasRepository;
    @Mock private HistoricoEtapaPedidoRepository historicoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private EnderecoRepository enderecoRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private br.com.tonspersonalizados.service.LogSistemaService logSistemaService;
    @InjectMocks private PedidoService pedidoService;

    private Usuario cliente() {
        Usuario u = new Usuario();
        u.setNome("Cliente");
        u.setTelefone("11999990000");
        Empresa empresa = new Empresa();
        empresa.setNomeFantasia("Empresa X");
        u.setEmpresa(empresa);
        return u;
    }

    @Nested
    @DisplayName("criarPedido")
    class CriarPedidoTest {

        @Test
        @DisplayName("Deve criar o pedido com itens e montar a resposta completa")
        void deveCriarPedidoCompleto() {
            // Arrange — request com 1 item e características
            CaracteristicasRequestDto caract = new CaracteristicasRequestDto();
            caract.setDescricaoArte("Arte");
            ItemPedidoRequestDto itemDto = new ItemPedidoRequestDto();
            itemDto.setIdProduto(5L);
            itemDto.setQuantidade(2);
            itemDto.setValorUnitario(BigDecimal.TEN);
            itemDto.setCaracteristicas(caract);

            PedidoRequestDto request = new PedidoRequestDto();
            request.setIdUsuarioCliente(1L);
            request.setIdUsuarioResponsavel(2L); // cobre o ramo responsavel != null
            request.setIdEndereco(3L);
            request.setEtapaPedido("Design");
            request.setStatus("Não Iniciado");
            request.setItens(List.of(itemDto));

            // Produto.getId() é primitivo (long) sobre campo Long -> mockamos para evitar NPE de unboxing
            Produto produto = mock(Produto.class);
            when(produto.getId()).thenReturn(5L);

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente()));
            when(usuarioRepository.findById(2L)).thenReturn(Optional.of(new Usuario()));
            when(enderecoRepository.findById(3L)).thenReturn(Optional.of(new Endereco()));
            when(produtoRepository.findById(5L)).thenReturn(Optional.of(produto));
            // Devolve o próprio objeto recebido para que o montarResponse veja os dados setados
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> {
                Pedido p = inv.getArgument(0);
                p.setId(1);
                return p;
            });
            when(caracteristicasRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(itemPedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            PedidoResponseDto resposta = pedidoService.criarPedido(request);

            // Assert
            assertNotNull(resposta);
            assertEquals(1, resposta.getItens().size());
            assertNotNull(resposta.getCliente());
            assertNotNull(resposta.getResponsavel());
            verify(pedidoRepository).save(any(Pedido.class));
        }

        // Representante do padrão UsuarioNaoEncontradoException.
        @Test
        @DisplayName("Deve lançar UsuarioNaoEncontradoException quando o cliente não existir")
        void deveLancarQuandoClienteNaoExiste() {
            // Arrange
            PedidoRequestDto request = new PedidoRequestDto();
            request.setIdUsuarioCliente(99L);
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            // Act + Assert
            assertThrows(UsuarioNaoEncontradoException.class, () -> pedidoService.criarPedido(request));
        }
    }

    @Nested
    @DisplayName("consultas")
    class ConsultasTest {

        // Pedido "pelado" cobre os ramos FALSOS de montarPedidoResponse (cliente/responsável/endereço nulos).
        @Test
        @DisplayName("Deve retornar página de pedidos e acrescentar desempate por ID")
        void deveListarTodos() {
            Pedido pedido = new Pedido();
            pedido.setId(1);
            when(pedidoRepository.buscarPedidos(
                    eq("Finalizados"), eq("Concluído"), eq("cliente"), eq(7L), eq(true), any(Pageable.class)))
                    .thenAnswer(invocation -> {
                        Pageable pageable = invocation.getArgument(5);
                        return new PageImpl<>(List.of(pedido), pageable, 1);
                    });
            when(itemPedidoRepository.findByPedidoId(1)).thenReturn(List.of());

            var resultado = pedidoService.listarTodos(
                    PageRequest.of(0, 10), "Finalizados", "Concluído", " cliente ", 7L, true);

            assertEquals(1, resultado.getTotalElements());
            assertEquals(1, resultado.getContent().size());
            var ordem = org.mockito.ArgumentCaptor.forClass(Pageable.class);
            verify(pedidoRepository).buscarPedidos(
                    eq("Finalizados"), eq("Concluído"), eq("cliente"), eq(7L), eq(true), ordem.capture());
            assertEquals(List.of("dataPedido", "id"), ordem.getValue().getSort().stream()
                    .map(order -> order.getProperty()).toList());
            assertEquals(List.of(org.springframework.data.domain.Sort.Direction.DESC,
                            org.springframework.data.domain.Sort.Direction.ASC),
                    ordem.getValue().getSort().stream().map(order -> order.getDirection()).toList());
        }

        @Test
        @DisplayName("Deve mapear ordenação por data de finalização e preservar a página")
        void deveMapearOrdenacaoEPaginacao() {
            when(pedidoRepository.buscarPedidos(
                    eq(null), eq(null), eq(null), eq(null), eq(false), any(Pageable.class)))
                    .thenAnswer(invocation ->
                            new PageImpl<>(List.of(), invocation.getArgument(5), 0));

            Pageable solicitada = PageRequest.of(2, 5, Sort.by(Sort.Order.desc("dataFinalizacao")));
            pedidoService.listarTodos(solicitada, null, null, null, null, false);

            var pageable = org.mockito.ArgumentCaptor.forClass(Pageable.class);
            verify(pedidoRepository).buscarPedidos(
                    eq(null), eq(null), eq(null), eq(null), eq(false), pageable.capture());
            assertEquals(2, pageable.getValue().getPageNumber());
            assertEquals(5, pageable.getValue().getPageSize());
            assertEquals(List.of("dataFinalizacao", "id"), pageable.getValue().getSort().stream()
                    .map(order -> order.getProperty()).toList());
            assertEquals(Sort.Direction.DESC, pageable.getValue().getSort().iterator().next().getDirection());
        }

        @Test
        @DisplayName("Deve buscar o pedido por id e anexar seus itens")
        void deveBuscarPorId() {
            // Arrange
            when(pedidoRepository.findById(1)).thenReturn(Optional.of(new Pedido()));
            when(itemPedidoRepository.findByPedidoId(1)).thenReturn(List.of());
            // Act
            PedidoResponseDto resposta = pedidoService.buscarPorId(1);
            // Assert
            assertNotNull(resposta);
        }

        // Representante do padrão PedidoNaoEncontradoException.
        @Test
        @DisplayName("Deve lançar PedidoNaoEncontradoException quando o id não existir")
        void deveLancarQuandoPedidoNaoExiste() {
            // Arrange
            when(pedidoRepository.findById(99)).thenReturn(Optional.empty());
            // Act + Assert
            assertThrows(PedidoNaoEncontradoException.class, () -> pedidoService.buscarPorId(99));
        }

        @Test
        @DisplayName("Deve listar o histórico incluindo o nome do responsável")
        void deveListarHistorico() {
            // Arrange
            Usuario responsavel = new Usuario();
            responsavel.setNome("Func");
            HistoricoEtapaPedido h = new HistoricoEtapaPedido();
            h.setEtapa("Design");
            h.setResponsavelEtapa(responsavel); // cobre o ramo responsavelEtapa != null
            when(historicoRepository.findByPedidoIdOrderByDataEntradaAsc(1)).thenReturn(List.of(h));

            // Act
            List<HistoricoEtapaResponseDto> resultado = pedidoService.listarHistorico(1);

            // Assert
            assertEquals(1, resultado.size());
            assertEquals("Func", resultado.get(0).getNomeResponsavel());
        }

        @Test
        @DisplayName("Deve listar meus pedidos em andamento")
        void deveListarEmAndamento() {
            // Arrange
            when(pedidoRepository.findByUsuarioClienteIdAndEtapaPedidoNotInOrderByDataPedidoDesc(
                    1, List.of("Finalizado", "Finalizados")))
                    .thenReturn(List.of(new Pedido()));
            // Act + Assert
            assertEquals(1, pedidoService.listarMeusPedidosEmAndamento(1).size());
        }

        @Test
        @DisplayName("Deve listar meus pedidos finalizados")
        void deveListarFinalizados() {
            // Arrange
            when(pedidoRepository.findByUsuarioClienteIdAndEtapaPedidoInOrderByDataFinalizacaoDesc(
                    1, List.of("Finalizado", "Finalizados")))
                    .thenReturn(List.of(new Pedido()));
            // Act + Assert
            assertEquals(1, pedidoService.listarMeusPedidosFinalizados(1).size());
        }

        @Test
        @DisplayName("Deve paginar histórico do cliente e combinar finalizados e cancelados")
        void devePaginarHistoricoDoCliente() {
            when(pedidoRepository.buscarHistoricoClientePaginado(
                    eq(1), eq(List.of("finalizado", "finalizados", "cancelado")), any(Pageable.class)))
                    .thenAnswer(invocation -> new PageImpl<>(
                            List.of(), invocation.getArgument(2), 0));

            var resultado = pedidoService.listarMeuHistoricoPaginado(
                    1, "todos", PageRequest.of(1, 5));

            assertEquals(1, resultado.getNumber());
            assertEquals(5, resultado.getSize());
            var pageable = org.mockito.ArgumentCaptor.forClass(Pageable.class);
            verify(pedidoRepository).buscarHistoricoClientePaginado(
                    eq(1), eq(List.of("finalizado", "finalizados", "cancelado")), pageable.capture());
            assertEquals(List.of("dataPedido", "id"), pageable.getValue().getSort().stream()
                    .map(Sort.Order::getProperty).toList());
            assertEquals(List.of(Sort.Direction.DESC, Sort.Direction.DESC), pageable.getValue().getSort().stream()
                    .map(Sort.Order::getDirection).toList());
        }

        @Test
        @DisplayName("Deve rejeitar filtro de histórico desconhecido")
        void deveRejeitarFiltroHistoricoDesconhecido() {
            assertThrows(org.springframework.web.server.ResponseStatusException.class,
                    () -> pedidoService.listarMeuHistoricoPaginado(
                            1, "EM_ANDAMENTO", PageRequest.of(0, 10)));
            verifyNoInteractions(pedidoRepository);
        }
    }

    @Nested
    @DisplayName("avancarEtapa")
    class AvancarEtapaTest {

        private EtapaRequestDto etapaRequest(String etapa, String status) {
            EtapaRequestDto req = new EtapaRequestDto();
            req.setEtapa(etapa);
            req.setStatus(status);
            req.setIdResponsavelEtapa(2L);
            return req;
        }

        @Test
        @DisplayName("Deve resetar o responsável e publicar evento quando a etapa mudar")
        void deveAvancarComMudancaDeEtapa() {
            // Arrange — pedido em "Design", request muda para "Produção"
            Pedido pedido = new Pedido();
            pedido.setId(1);
            pedido.setEtapaPedido("Design");
            when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
            when(usuarioRepository.findById(2L)).thenReturn(Optional.of(new Usuario()));

            // Act
            pedidoService.avancarEtapa(1, etapaRequest("Produção", "Não Iniciado"));

            // Assert
            assertNull(pedido.getUsuarioResponsavel());                  // etapa mudou -> reseta
            verify(eventPublisher).publishEvent(any(EtapaAvancadaEvent.class));
            verify(historicoRepository).save(any(HistoricoEtapaPedido.class));
        }

        @Test
        @DisplayName("Deve manter o responsável e NÃO publicar evento quando só o status mudar")
        void deveAvancarSemMudancaDeEtapa() {
            // Arrange — etapa continua "Design"
            Pedido pedido = new Pedido();
            pedido.setId(1);
            pedido.setEtapaPedido("Design");
            Usuario responsavel = new Usuario();
            when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
            when(usuarioRepository.findById(2L)).thenReturn(Optional.of(responsavel));

            // Act
            pedidoService.avancarEtapa(1, etapaRequest("Design", "Aguardando arte"));

            // Assert
            assertSame(responsavel, pedido.getUsuarioResponsavel());      // etapa não mudou -> mantém
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Deve aceitar e persistir etapa Finalizados")
        void deveAceitarEtapaFinalizados() {
            Pedido pedido = new Pedido();
            pedido.setId(1);
            pedido.setEtapaPedido("Design");
            when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
            when(usuarioRepository.findById(2L)).thenReturn(Optional.of(new Usuario()));

            pedidoService.avancarEtapa(1, etapaRequest("Finalizados", "Concluído"));

            assertEquals("Finalizados", pedido.getEtapaPedido());
            assertEquals("Concluído", pedido.getStatus());
            verify(pedidoRepository).save(pedido);
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando o status não for válido para a etapa")
        void deveLancarStatusInvalido() {
            // Arrange — "Concluído" não é status válido de "Design"
            // Act + Assert (falha antes de qualquer acesso a repositório)
            assertThrows(IllegalArgumentException.class,
                    () -> pedidoService.avancarEtapa(1, etapaRequest("Design", "Concluído")));
        }
    }

    @Nested
    @DisplayName("atribuirResponsavel")
    class AtribuirResponsavelTest {

        @Test
        @DisplayName("Deve atribuir o responsável e salvar o pedido")
        void deveAtribuir() {
            // Arrange
            Pedido pedido = new Pedido();
            pedido.setId(1);
            Usuario responsavel = new Usuario();
            when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
            when(usuarioRepository.findById(2L)).thenReturn(Optional.of(responsavel));

            // Act
            pedidoService.atribuirResponsavel(1, 2L);

            // Assert
            assertSame(responsavel, pedido.getUsuarioResponsavel());
            verify(pedidoRepository).save(pedido);
        }
    }
}
