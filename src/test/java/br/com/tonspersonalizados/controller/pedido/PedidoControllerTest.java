package br.com.tonspersonalizados.controller.pedido;

import br.com.tonspersonalizados.config.AutenticacaoFilter;
import br.com.tonspersonalizados.config.SecurityConfiguracao;
import br.com.tonspersonalizados.dto.pedidos.PedidoResponseDto;
import br.com.tonspersonalizados.exception.pedido.PedidoNaoEncontradoException;
import br.com.tonspersonalizados.service.pedido.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
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
@DisplayName("PedidoController (web)")
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    @Test
    @DisplayName("GET /pedidos deve retornar 200")
    void deveListarTodos() throws Exception {
        // Arrange
        when(pedidoService.listarTodos()).thenReturn(List.of());
        // Act + Assert
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk());
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
}
