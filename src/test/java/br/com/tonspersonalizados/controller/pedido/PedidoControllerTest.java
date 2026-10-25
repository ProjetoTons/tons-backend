package br.com.tonspersonalizados.controller.pedido;

import br.com.tonspersonalizados.config.AutenticacaoFilter;
import br.com.tonspersonalizados.config.SecurityConfiguracao;
import br.com.tonspersonalizados.dto.pedidos.PedidoRequestDto;
import br.com.tonspersonalizados.dto.pedidos.PedidoResponseDto;
import br.com.tonspersonalizados.exception.pedido.PedidoNaoEncontradoException;
import br.com.tonspersonalizados.service.pedido.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de camada web do PedidoController (@WebMvcTest + MockMvc).
 * Endpoints principais sem dependência de SecurityContext: listar e buscar por id.
 */
@WebMvcTest(controllers = PedidoController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguracao.class, AutenticacaoFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
@Import(PedidoControllerTest.MethodSecurityTestConfiguration.class)
@DisplayName("PedidoController (web)")
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

        @Autowired
        private PedidoController pedidoController;

        @AfterEach
        void limparContextoSeguranca() {
                SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("POST /pedidos deve negar criação para cargo diferente de Adm")
        void deveNegarCriacaoSemAdm() {
                autenticarComo("Vendedor");

                assertThrows(AccessDeniedException.class,
                                () -> pedidoController.criarPedido(new PedidoRequestDto()));
                verifyNoInteractions(pedidoService);
        }

        @Test
        @DisplayName("POST /pedidos deve permitir criação para cargo Adm")
        void devePermitirCriacaoParaAdm() {
                autenticarComo("Adm");
                PedidoResponseDto respostaEsperada = new PedidoResponseDto();
                when(pedidoService.criarPedido(any(PedidoRequestDto.class))).thenReturn(respostaEsperada);

                var resposta = pedidoController.criarPedido(new PedidoRequestDto());

                assertEquals(201, resposta.getStatusCode().value());
                assertSame(respostaEsperada, resposta.getBody());
                verify(pedidoService).criarPedido(any(PedidoRequestDto.class));
        }

        private void autenticarComo(String autoridade) {
                SecurityContext contexto = SecurityContextHolder.createEmptyContext();
                contexto.setAuthentication(new UsernamePasswordAuthenticationToken(
                                "teste@tons.com", null, List.of(new SimpleGrantedAuthority(autoridade))));
                SecurityContextHolder.setContext(contexto);
        }

    @Test
    @DisplayName("GET /pedidos deve retornar página e aceitar filtros")
    void deveListarTodos() throws Exception {
        when(pedidoService.listarTodos(any(Pageable.class), nullable(String.class), nullable(String.class),
                nullable(String.class), nullable(Long.class), eq(false)))
                .thenReturn(Page.empty(org.springframework.data.domain.PageRequest.of(1, 5)));

        mockMvc.perform(get("/pedidos")
                        .param("etapa", "Finalizados")
                        .param("status", "Concluído")
                        .param("busca", "pedido")
                        .param("responsavelId", "12")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "dataFinalizacao,desc"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.number").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.size").value(5))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.totalElements").value(0))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.totalPages").value(0));

        verify(pedidoService).listarTodos(any(Pageable.class), eq("Finalizados"), eq("Concluído"),
                eq("pedido"), eq(12L), eq(false));
    }

    @Test
    @DisplayName("GET /pedidos/meus/historico/paginado deve retornar a página do cliente autenticado")
    void deveListarHistoricoPaginado() throws Exception {
        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(
                "cliente@tons.com", null, List.of());
        autenticacao.setDetails(42L);
        contexto.setAuthentication(autenticacao);
        SecurityContextHolder.setContext(contexto);
        when(pedidoService.listarMeuHistoricoPaginado(eq(42), eq("CANCELADOS"), any(Pageable.class)))
                .thenReturn(Page.empty(org.springframework.data.domain.PageRequest.of(1, 3)));

        mockMvc.perform(get("/pedidos/meus/historico/paginado")
                        .param("filtro", "CANCELADOS")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.number").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.size").value(3))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.totalElements").value(0))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.totalPages").value(0));

        verify(pedidoService).listarMeuHistoricoPaginado(eq(42), eq("CANCELADOS"), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /pedidos/{id} deve retornar 200 quando existir")
    void deveBuscarPorId() throws Exception {
        // Arrange
        when(pedidoService.buscarPorId(1)).thenReturn(new PedidoResponseDto());
        // Act + Assert
        mockMvc.perform(get("/pedidos/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /pedidos/{id} deve retornar 404 quando não existir")
    void deveRetornar404() throws Exception {
        // Arrange
        when(pedidoService.buscarPorId(99))
                .thenThrow(new PedidoNaoEncontradoException("Pedido não encontrado"));
        // Act + Assert
        mockMvc.perform(get("/pedidos/{id}", 99))
                .andExpect(status().isNotFound());
    }

        @TestConfiguration
        @EnableMethodSecurity
        static class MethodSecurityTestConfiguration {}
}
