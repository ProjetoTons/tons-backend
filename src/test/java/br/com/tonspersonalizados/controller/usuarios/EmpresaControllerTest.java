package br.com.tonspersonalizados.controller.usuarios;

import br.com.tonspersonalizados.config.AutenticacaoFilter;
import br.com.tonspersonalizados.config.SecurityConfiguracao;
import br.com.tonspersonalizados.entity.usuarios.Empresa;
import br.com.tonspersonalizados.exception.usuarios.EmpresaNaoEncontradoException;
import br.com.tonspersonalizados.service.usuarios.EmpresaService;
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
 * Testes de camada web do EmpresaController (@WebMvcTest + MockMvc).
 * Endpoints principais (sem corpo): listagem e busca por CNPJ (200 e 404).
 */
@WebMvcTest(controllers = EmpresaController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguracao.class, AutenticacaoFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("EmpresaController (web)")
class EmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmpresaService empresaService;

    @Test
    @DisplayName("GET /empresas deve retornar 200")
    void deveListarEmpresas() throws Exception {
        // Arrange
        when(empresaService.listarTodos()).thenReturn(List.of());
        // Act + Assert
        mockMvc.perform(get("/empresas"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /empresas/cnpj/{cnpj} deve retornar 200 quando existir")
    void deveBuscarPorCnpj() throws Exception {
        // Arrange
        when(empresaService.buscarPorCnpj("11222333000181")).thenReturn(new Empresa());
        // Act + Assert
        mockMvc.perform(get("/empresas/cnpj/{cnpj}", "11222333000181"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /empresas/cnpj/{cnpj} deve retornar 404 quando não existir")
    void deveRetornar404() throws Exception {
        // Arrange
        when(empresaService.buscarPorCnpj("000"))
                .thenThrow(new EmpresaNaoEncontradoException("Empresa não encontrada."));
        // Act + Assert
        mockMvc.perform(get("/empresas/cnpj/{cnpj}", "000"))
                .andExpect(status().isNotFound());
    }
}
