package br.com.tonspersonalizados.repository.pedido;

import br.com.tonspersonalizados.entity.pedidos.Pedido;
import br.com.tonspersonalizados.entity.usuarios.Endereco;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Usuario vendedor;
    private Usuario cliente;
    private Usuario responsavel;
    private Endereco endereco;
    private LocalDateTime dataPedido;

    @BeforeEach
    void prepararDados() {
        vendedor = criarUsuario("Vendedor Alfa");
        cliente = criarUsuario("Cliente Busca");
        responsavel = criarUsuario("Responsável Design");
        endereco = new Endereco();
        endereco.setCep("00000000");
        endereco.setLogradouro("Rua Teste");
        endereco.setNumero("1");
        entityManager.persist(endereco);
        entityManager.flush();

        dataPedido = LocalDateTime.of(2026, 1, 1, 12, 0);
        pedidoRepository.saveAll(List.of(
                criarPedido("NUM-001", "Design", "Em andamento", responsavel, dataPedido),
                criarPedido("NUM-002", "Design", "Em andamento", responsavel, dataPedido),
                criarPedido("NUM-003", "Finalizados", "Concluído", responsavel, dataPedido),
                criarPedido("NUM-004", "Finalizado", "Concluído", responsavel, dataPedido),
                criarPedido("NUM-005", "Cancelado", "Cancelado", responsavel, dataPedido)));
        entityManager.flush();
    }

    @Test
    void deveFiltrarBuscaSemDiferenciarMaiusculasEtapaStatusEResponsavel() {
        Page<Pedido> resultado = pedidoRepository.buscarPedidos(
                "Finalizados", "concluído", "CLIENTE BUSCA", responsavel.getId(), true,
                PageRequest.of(0, 10, Sort.by(Sort.Order.desc("dataPedido"), Sort.Order.asc("id"))));

        assertEquals(1, resultado.getTotalElements());
        assertEquals("NUM-003", resultado.getContent().getFirst().getNumPedido());
    }

    @Test
    void deveBuscarPorVendedorResponsavelENumeroEPaginarAntesDeCalcularTotais() {
        Page<Pedido> porVendedor = pedidoRepository.buscarPedidos(
                null, null, "VENDEDOR ALFA", null, false, PageRequest.of(0, 10));
        Page<Pedido> porResponsavel = pedidoRepository.buscarPedidos(
                null, null, "responsável design", null, false, PageRequest.of(0, 10));
        Page<Pedido> porNumero = pedidoRepository.buscarPedidos(
                null, null, "num-001", null, false, PageRequest.of(0, 10));
        Page<Pedido> primeiraPagina = pedidoRepository.buscarPedidos(
                null, null, null, null, true,
                PageRequest.of(0, 1, Sort.by(Sort.Order.asc("id"))));

        assertEquals(5, porVendedor.getTotalElements());
        assertEquals(5, porResponsavel.getTotalElements());
        assertEquals(1, porNumero.getTotalElements());
        assertEquals(2, primeiraPagina.getTotalElements());
        assertEquals(2, primeiraPagina.getTotalPages());
        assertEquals(1, primeiraPagina.getSize());
        assertEquals(0, primeiraPagina.getNumber());
    }

    @Test
    void deveCombinarHistoricoFiltrarClienteEPaginarComOrdemEstavel() {
        Usuario outroCliente = criarUsuario("Outro cliente");
        Pedido pedidoDeOutroCliente = criarPedido("NUM-006", "Cancelado", "Cancelado", responsavel, dataPedido);
        pedidoDeOutroCliente.setUsuarioCliente(outroCliente);
        pedidoRepository.save(pedidoDeOutroCliente);
        entityManager.flush();

        List<String> todasEtapas = List.of("finalizado", "finalizados", "cancelado");
        var ordenacao = Sort.by(Sort.Order.desc("dataPedido"), Sort.Order.desc("id"));
        Page<Pedido> primeiraPagina = pedidoRepository.buscarHistoricoClientePaginado(
                cliente.getId().intValue(), todasEtapas, PageRequest.of(0, 2, ordenacao));
        Page<Pedido> segundaPagina = pedidoRepository.buscarHistoricoClientePaginado(
                cliente.getId().intValue(), todasEtapas, PageRequest.of(1, 2, ordenacao));
        Page<Pedido> finalizados = pedidoRepository.buscarHistoricoClientePaginado(
                cliente.getId().intValue(), List.of("finalizado", "finalizados"), PageRequest.of(0, 10, ordenacao));
        Page<Pedido> cancelados = pedidoRepository.buscarHistoricoClientePaginado(
                cliente.getId().intValue(), List.of("cancelado"), PageRequest.of(0, 10, ordenacao));

        assertEquals(3, primeiraPagina.getTotalElements());
        assertEquals(2, primeiraPagina.getTotalPages());
        assertEquals(2, primeiraPagina.getContent().size());
        assertEquals(1, segundaPagina.getContent().size());
        assertEquals(3, java.util.stream.Stream.concat(
                primeiraPagina.getContent().stream(), segundaPagina.getContent().stream())
                .map(Pedido::getId).distinct().count());
        assertEquals(2, finalizados.getTotalElements());
        assertEquals(1, cancelados.getTotalElements());
        assertTrue(primeiraPagina.getContent().get(0).getId()
                > primeiraPagina.getContent().get(1).getId());
        assertTrue(primeiraPagina.getContent().get(1).getId()
                > segundaPagina.getContent().getFirst().getId());
    }

    private Usuario criarUsuario(String nome) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setTelefone("11999990000");
        usuario.setFuncionario(true);
        entityManager.persist(usuario);
        return usuario;
    }

    private Pedido criarPedido(
            String numero,
            String etapa,
            String status,
            Usuario responsavel,
            LocalDateTime dataPedido) {
        Pedido pedido = new Pedido();
        pedido.setNumPedido(numero);
        pedido.setEtapaPedido(etapa);
        pedido.setStatus(status);
        pedido.setValorTotal(BigDecimal.TEN);
        pedido.setDataPedido(dataPedido);
        pedido.setUsuario(vendedor);
        pedido.setUsuarioCliente(cliente);
        pedido.setUsuarioResponsavel(responsavel);
        pedido.setEndereco(endereco);
        return pedido;
    }
}
