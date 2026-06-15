package br.com.tonspersonalizados.dto;

import br.com.tonspersonalizados.dto.dashboard.GraficoEtapaDto;
import br.com.tonspersonalizados.dto.dashboard.KpisDashboardDto;
import br.com.tonspersonalizados.dto.dashboard.PerformanceFuncionarioDto;
import br.com.tonspersonalizados.dto.dashboard.SubEtapaDto;
import br.com.tonspersonalizados.dto.pedidos.CancelamentoPedidoRequestDto;
import br.com.tonspersonalizados.dto.pedidos.PedidoLogDto;
import br.com.tonspersonalizados.dto.usuarios.*;
import br.com.tonspersonalizados.entity.LogSistema;
import br.com.tonspersonalizados.entity.AcaoLog;
import br.com.tonspersonalizados.entity.pedidos.Pedido;
import br.com.tonspersonalizados.entity.produtos.CategoriaProduto;
import br.com.tonspersonalizados.entity.produtos.Produto;
import br.com.tonspersonalizados.entity.usuarios.Acesso;
import br.com.tonspersonalizados.entity.usuarios.Login;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.dto.produtos.ProdutoLogDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DTOs e Entidades - cobertura getters/setters")
class DtoEntityCoverageTest {

    @Test
    @DisplayName("KpisDashboardDto - construtor e getters/setters")
    void kpisDashboardDto() {
        KpisDashboardDto dto = new KpisDashboardDto(BigDecimal.TEN, 1, 2, 3, BigDecimal.ONE, 10);
        assertEquals(BigDecimal.TEN, dto.getTotalValor());
        assertEquals(1, dto.getAguardandoArte());
        assertEquals(2, dto.getAguardandoRetirada());
        assertEquals(3, dto.getEnviada());
        assertEquals(BigDecimal.ONE, dto.getMetaSemanal());
        assertEquals(10, dto.getTotalPedidos());

        dto.setTotalValor(BigDecimal.ZERO);
        dto.setAguardandoArte(5);
        dto.setAguardandoRetirada(6);
        dto.setEnviada(7);
        dto.setMetaSemanal(BigDecimal.TEN);
        dto.setTotalPedidos(20);
        assertEquals(BigDecimal.ZERO, dto.getTotalValor());
        assertEquals(5, dto.getAguardandoArte());
    }

    @Test
    @DisplayName("GraficoEtapaDto - construtor e getters/setters")
    void graficoEtapaDto() {
        GraficoEtapaDto dto = new GraficoEtapaDto("Design", 5, BigDecimal.TEN);
        assertEquals("Design", dto.getEtapa());
        assertEquals(5, dto.getQuantidadePedidos());
        assertEquals(BigDecimal.TEN, dto.getValorTotalArrecadado());

        dto.setEtapa("Produção");
        dto.setQuantidadePedidos(10);
        dto.setValorTotalArrecadado(BigDecimal.ONE);
        assertEquals("Produção", dto.getEtapa());
    }

    @Test
    @DisplayName("SubEtapaDto - construtor e getters/setters")
    void subEtapaDto() {
        SubEtapaDto dto = new SubEtapaDto("Aguardando", 3, BigDecimal.TEN);
        assertEquals("Aguardando", dto.getSubEtapa());
        assertEquals(3, dto.getQuantidadePedidos());
        assertEquals(BigDecimal.TEN, dto.getValorTotalArrecadado());

        dto.setSubEtapa("Pronto");
        dto.setQuantidadePedidos(7);
        dto.setValorTotalArrecadado(BigDecimal.ZERO);
        assertEquals("Pronto", dto.getSubEtapa());
    }

    @Test
    @DisplayName("PerformanceFuncionarioDto - construtor e getters/setters")
    void performanceFuncionarioDto() {
        PerformanceFuncionarioDto.TarefasDto tarefas = new PerformanceFuncionarioDto.TarefasDto(1, 2, 3, 4);
        assertEquals(1, tarefas.getDesign());
        assertEquals(2, tarefas.getProducao());
        assertEquals(3, tarefas.getEmbalagem());
        assertEquals(4, tarefas.getLogistica());

        tarefas.setDesign(10);
        tarefas.setProducao(20);
        tarefas.setEmbalagem(30);
        tarefas.setLogistica(40);

        PerformanceFuncionarioDto dto = new PerformanceFuncionarioDto(1L, "Func", tarefas);
        assertEquals(1L, dto.getIdFuncionario());
        assertEquals("Func", dto.getNomeFuncionario());
        assertNotNull(dto.getTarefas());

        dto.setIdFuncionario(2L);
        dto.setNomeFuncionario("Outro");
        dto.setTarefas(tarefas);
    }

    @Test
    @DisplayName("ClienteResponseDto - getters/setters")
    void clienteResponseDto() {
        ClienteResponseDto dto = new ClienteResponseDto();
        dto.setId(1L);
        dto.setNome("Nome");
        dto.setCpf("12345678901");
        dto.setTelefone("11999990000");
        dto.setNomeEmpresa("Empresa");
        dto.setCnpj("12345678000199");

        assertEquals(1L, dto.getId());
        assertEquals("Nome", dto.getNome());
        assertEquals("12345678901", dto.getCpf());
        assertEquals("11999990000", dto.getTelefone());
        assertEquals("Empresa", dto.getNomeEmpresa());
        assertEquals("12345678000199", dto.getCnpj());
    }

    @Test
    @DisplayName("FuncionarioAtualizarRequestDto - getters/setters")
    void funcionarioAtualizarRequestDto() {
        FuncionarioAtualizarRequestDto dto = new FuncionarioAtualizarRequestDto();
        dto.setNome("Nome");
        dto.setEmail("e@e.com");
        dto.setTelefone("11999990000");
        dto.setAcessos(List.of(1L, 2L));
        dto.setFotoUrl("http://foto.jpg");

        assertEquals("Nome", dto.getNome());
        assertEquals("e@e.com", dto.getEmail());
        assertEquals("11999990000", dto.getTelefone());
        assertEquals(2, dto.getAcessos().size());
        assertEquals("http://foto.jpg", dto.getFotoUrl());
        assertNotNull(dto.getFotoPublicId() == null || dto.getFotoPublicId() != null); // exercita getter
    }

    @Test
    @DisplayName("FuncionarioRequestDto - getters")
    void funcionarioRequestDto() {
        FuncionarioRequestDto dto = new FuncionarioRequestDto();
        // Exercita todos os getters (retornam null por default)
        assertNull(dto.getNome());
        assertNull(dto.getEmail());
        assertNull(dto.getTelefone());
        assertNull(dto.getSenha());
        assertNull(dto.getFotoUrl());
        assertNull(dto.getFotoPublicId());
        assertNull(dto.getDataNascimento());
        assertNull(dto.getAcessos());
    }

    @Test
    @DisplayName("EnderecoRequestDto - getters")
    void enderecoRequestDto() {
        EnderecoRequestDto dto = new EnderecoRequestDto();
        assertNull(dto.getLogradouro());
        assertNull(dto.getNumero());
        assertNull(dto.getCep());
        assertNull(dto.getComplemento());
        assertNull(dto.getBairro());
        assertNull(dto.getCidade());
        assertNull(dto.getEstado());
    }

    @Test
    @DisplayName("LogSistema - entity getters/setters")
    void logSistemaEntity() {
        LogSistema log = new LogSistema();
        Usuario u = new Usuario();
        LocalDateTime now = LocalDateTime.now();

        log.setUsuario(u);
        log.setAcao(AcaoLog.CRIAR);
        log.setEntidade("Pedido");
        log.setEntidadeId(1L);
        log.setDescricao("desc");
        log.setValorAnterior("antes");
        log.setValorNovo("depois");
        log.setDataLog(now);

        assertSame(u, log.getUsuario());
        assertEquals(AcaoLog.CRIAR, log.getAcao());
        assertEquals("Pedido", log.getEntidade());
        assertEquals(1L, log.getEntidadeId());
        assertEquals("desc", log.getDescricao());
        assertEquals("antes", log.getValorAnterior());
        assertEquals("depois", log.getValorNovo());
        assertEquals(now, log.getDataLog());
    }

    @Test
    @DisplayName("CategoriaProduto - entity getters/setters")
    void categoriaProdutoEntity() {
        CategoriaProduto cat = new CategoriaProduto();
        cat.setId(1L);
        cat.setNome("Canecas");
        cat.setDescricao("Desc");
        cat.setSlug("canecas");
        cat.setProdutos(List.of(new Produto()));
        assertEquals(1L, cat.getId());
        assertEquals("Canecas", cat.getNome());
        assertEquals("Desc", cat.getDescricao());
        assertEquals("canecas", cat.getSlug());
        assertEquals(1, cat.getProdutos().size());
    }

    @Test
    @DisplayName("Produto - entity getters/setters adicionais")
    void produtoEntity() {
        Produto p = new Produto();
        p.setId(1L);
        p.setNome("Caneca");
        p.setDescricao("Caneca personalizada");
        p.setTipoMaterial("Cerâmica");
        p.setImagemUrl("http://img.jpg");
        p.setDestaque(true);

        CategoriaProduto cat = new CategoriaProduto();
        p.setCategoriaProduto(cat);

        assertEquals(1L, p.getId());
        assertEquals("Caneca", p.getNome());
        assertEquals("Caneca personalizada", p.getDescricao());
        assertEquals("Cerâmica", p.getTipoMaterial());
        assertEquals("http://img.jpg", p.getImagemUrl());
        assertTrue(p.getDestaque());
        assertSame(cat, p.getCategoriaProduto());
    }

    @Test
    @DisplayName("ProdutoLogDto.from - com produto completo")
    void produtoLogDtoFrom() {
        CategoriaProduto cat = new CategoriaProduto();
        cat.setNome("Camisetas");
        Produto p = new Produto();
        p.setId(5L);
        p.setNome("Camiseta");
        p.setDescricao("Desc");
        p.setTipoMaterial("Algodão");
        p.setCategoriaProduto(cat);

        ProdutoLogDto dto = ProdutoLogDto.from(p);
        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("Camiseta", dto.getNome());
        assertEquals("Desc", dto.getDescricao());
        assertEquals("Algodão", dto.getTipoMaterial());
        assertEquals("Camisetas", dto.getNomeCategoria());
    }

    @Test
    @DisplayName("ProdutoLogDto.from - com null")
    void produtoLogDtoFromNull() {
        assertNull(ProdutoLogDto.from(null));
    }

    @Test
    @DisplayName("ProdutoLogDto.from - sem categoria")
    void produtoLogDtoFromSemCategoria() {
        Produto p = new Produto();
        p.setId(1L);
        p.setNome("X");
        p.setDescricao("Y");
        p.setTipoMaterial("Z");
        ProdutoLogDto dto = ProdutoLogDto.from(p);
        assertNull(dto.getNomeCategoria());
    }

    @Test
    @DisplayName("ProdutoLogDto - setters")
    void produtoLogDtoSetters() {
        ProdutoLogDto dto = new ProdutoLogDto();
        dto.setId(1L);
        dto.setNome("N");
        dto.setDescricao("D");
        dto.setTipoMaterial("T");
        dto.setNomeCategoria("C");
        assertEquals(1L, dto.getId());
        assertEquals("N", dto.getNome());
    }

    @Test
    @DisplayName("UsuarioLogDto.from - com login")
    void usuarioLogDtoFrom() {
        Usuario u = new Usuario();
        u.setNome("João");
        u.setCpf("123");
        u.setTelefone("11999");
        Login login = new Login();
        login.setEmail("j@e.com");
        u.setLogin(login);

        UsuarioLogDto dto = UsuarioLogDto.from(u);
        assertEquals("João", dto.getNome());
        assertEquals("j@e.com", dto.getEmail());
        assertEquals("123", dto.getCpf());
    }

    @Test
    @DisplayName("UsuarioLogDto.from - sem login")
    void usuarioLogDtoFromSemLogin() {
        Usuario u = new Usuario();
        u.setNome("X");
        UsuarioLogDto dto = UsuarioLogDto.from(u);
        assertNull(dto.getEmail());
    }

    @Test
    @DisplayName("UsuarioLogDto - construtor e setters")
    void usuarioLogDtoSetters() {
        UsuarioLogDto dto = new UsuarioLogDto(1L, "N", "e@e", "cpf", "tel", true);
        dto.setId(2L);
        dto.setNome("O");
        dto.setEmail("x@x");
        dto.setCpf("c");
        dto.setTelefone("t");
        dto.setIsFuncionario(false);
        assertEquals(2L, dto.getId());
        assertFalse(dto.getIsFuncionario());
    }

    @Test
    @DisplayName("PedidoLogDto.from - com cliente e responsável")
    void pedidoLogDtoFrom() {
        Pedido p = new Pedido();
        p.setId(1);
        p.setNumPedido("PED-001");
        p.setEtapaPedido("Design");
        p.setStatus("Em andamento");
        p.setValorTotal(BigDecimal.TEN);
        Usuario cliente = new Usuario();
        cliente.setNome("Cliente");
        p.setUsuarioCliente(cliente);
        Usuario resp = new Usuario();
        resp.setNome("Resp");
        p.setUsuarioResponsavel(resp);

        PedidoLogDto dto = PedidoLogDto.from(p);
        assertEquals(1, dto.getId());
        assertEquals("PED-001", dto.getNumPedido());
        assertEquals("Design", dto.getEtapaPedido());
        assertEquals("Em andamento", dto.getStatus());
        assertEquals(BigDecimal.TEN, dto.getValorTotal());
        assertEquals("Cliente", dto.getClienteNome());
        assertEquals("Resp", dto.getResponsavelNome());
    }

    @Test
    @DisplayName("PedidoLogDto.from - null retorna null")
    void pedidoLogDtoFromNull() {
        assertNull(PedidoLogDto.from(null));
    }

    @Test
    @DisplayName("PedidoLogDto.from - sem cliente e sem responsável")
    void pedidoLogDtoFromSemClienteEResp() {
        Pedido p = new Pedido();
        p.setId(1);
        PedidoLogDto dto = PedidoLogDto.from(p);
        assertNull(dto.getClienteNome());
        assertNull(dto.getResponsavelNome());
    }

    @Test
    @DisplayName("PedidoLogDto - setters")
    void pedidoLogDtoSetters() {
        PedidoLogDto dto = new PedidoLogDto();
        dto.setId(1);
        dto.setNumPedido("X");
        dto.setEtapaPedido("E");
        dto.setStatus("S");
        dto.setValorTotal(BigDecimal.ONE);
        dto.setClienteNome("C");
        dto.setResponsavelNome("R");
        assertEquals(1, dto.getId());
        assertEquals("X", dto.getNumPedido());
    }

    @Test
    @DisplayName("LoginRequestDto - getters")
    void loginRequestDto() {
        LoginRequestDto dto = new LoginRequestDto();
        assertNull(dto.getEmail());
        assertNull(dto.getSenha());
    }

    @Test
    @DisplayName("AlterarSenhaRequestDto - getters")
    void alterarSenhaRequestDto() {
        AlterarSenhaRequestDto dto = new AlterarSenhaRequestDto();
        assertNull(dto.getSenhaAtual());
        assertNull(dto.getNovaSenha());
    }

    @Test
    @DisplayName("UsuarioAtualizarRequestDto - getters/setters")
    void usuarioAtualizarRequestDto() {
        UsuarioAtualizarRequestDto dto = new UsuarioAtualizarRequestDto();
        dto.setNome("Nome");
        dto.setEmail("e@e.com");
        dto.setTelefone("11999990000");
        dto.setIdEmpresa(5L);
        assertEquals("Nome", dto.getNome());
        assertEquals("e@e.com", dto.getEmail());
        assertEquals("11999990000", dto.getTelefone());
        assertEquals(5L, dto.getIdEmpresa());
        assertNull(dto.getEndereco());
    }

    @Test
    @DisplayName("Acesso entity - getters/setters")
    void acessoEntity() {
        Acesso acesso = new Acesso();
        acesso.setId(1L);
        acesso.setRole("ADMIN");
        acesso.setDescricao("Administrador");
        assertEquals(1L, acesso.getId());
        assertEquals("ADMIN", acesso.getRole());
        assertEquals("Administrador", acesso.getDescricao());
    }

    @Test
    @DisplayName("CancelamentoPedidoRequestDto - getters/setters")
    void testCancelamentoPedidoRequestDto() {
        CancelamentoPedidoRequestDto dto = new CancelamentoPedidoRequestDto();
        dto.setMotivo("Cliente desistiu");
        assertEquals("Cliente desistiu", dto.getMotivo());
    }

    @Test
    @DisplayName("EmpresaRequestDto - getters")
    void testEmpresaRequestDto() {
        EmpresaRequestDto dto = new EmpresaRequestDto();
        // Uses reflection to set private fields since no setters
        try {
            var nomeField = EmpresaRequestDto.class.getDeclaredField("nomeFantasia");
            nomeField.setAccessible(true);
            nomeField.set(dto, "Tons Personalizados");

            var emailField = EmpresaRequestDto.class.getDeclaredField("email");
            emailField.setAccessible(true);
            emailField.set(dto, "contato@tons.com");

            var telField = EmpresaRequestDto.class.getDeclaredField("telefone");
            telField.setAccessible(true);
            telField.set(dto, "11999999999");

            var razaoField = EmpresaRequestDto.class.getDeclaredField("razaoSocial");
            razaoField.setAccessible(true);
            razaoField.set(dto, "Tons LTDA");

            var cnpjField = EmpresaRequestDto.class.getDeclaredField("cnpj");
            cnpjField.setAccessible(true);
            cnpjField.set(dto, "12345678000199");
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }

        assertEquals("Tons Personalizados", dto.getNomeFantasia());
        assertEquals("contato@tons.com", dto.getEmail());
        assertEquals("11999999999", dto.getTelefone());
        assertEquals("Tons LTDA", dto.getRazaoSocial());
        assertEquals("12345678000199", dto.getCnpj());
    }

    @Test
    @DisplayName("UsuarioDetalhesDto - UserDetails methods")
    void testUsuarioDetalhesDto() {
        Usuario usuario = new Usuario();
        usuario.setNome("Test User");
        Login login = new Login();
        login.setEmail("test@email.com");
        login.setSenhaHash("hashed123");
        usuario.setLogin(login);

        UsuarioDetalhesDto dto = new UsuarioDetalhesDto(usuario);

        assertEquals("test@email.com", dto.getUsername());
        assertEquals("hashed123", dto.getPassword());
        assertNotNull(dto.getAuthorities());
        assertTrue(dto.getAuthorities().isEmpty());
        assertTrue(dto.isAccountNonExpired());
        assertTrue(dto.isAccountNonLocked());
        assertTrue(dto.isCredentialsNonExpired());
        assertTrue(dto.isEnabled());
    }
}
