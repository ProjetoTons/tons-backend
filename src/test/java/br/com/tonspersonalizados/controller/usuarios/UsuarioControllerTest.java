package br.com.tonspersonalizados.controller.usuarios;

import br.com.tonspersonalizados.config.AutenticacaoFilter;
import br.com.tonspersonalizados.config.SecurityConfiguracao;
import br.com.tonspersonalizados.dto.usuarios.UsuarioResponseDto;
import br.com.tonspersonalizados.exception.usuarios.UsuarioNaoEncontradoException;
import br.com.tonspersonalizados.service.usuarios.AutenticacaoService;
import br.com.tonspersonalizados.service.usuarios.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de camada web do UsuarioController (@WebMvcTest + MockMvc).
 * Foco em status HTTP: 201 (criado), 400 (corpo inválido), 200/404 (busca).
 * UsuarioService e AutenticacaoService são mocks; a segurança é desligada.
 */
@WebMvcTest(controllers = UsuarioController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguracao.class, AutenticacaoFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UsuarioController (web)")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;
    @MockitoBean
    private AutenticacaoService autenticacaoService;

    @Test
    @DisplayName("POST /usuarios deve retornar 201 com corpo válido")
    void deveCadastrar() throws Exception {
        // Arrange — CPF precisa ser válido (@CPF), telefone com 11 dígitos (@Size)
        String body = """
                {"nome":"João","cpf":"11144477735","email":"joao@email.com",
                 "telefone":"11999998888","senha":"senha123"}
                """;

        // Act + Assert
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /usuarios deve retornar 400 com corpo inválido")
    void deveRetornar400ComCorpoInvalido() throws Exception {
        // Arrange — corpo vazio viola @NotBlank/@NotNull/@CPF
        // Act + Assert
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /usuarios/{id} deve retornar 200 quando existir")
    void deveBuscarPorId() throws Exception {
        // Arrange
        when(usuarioService.buscarPorIdDto(1L)).thenReturn(new UsuarioResponseDto());
        // Act + Assert
        mockMvc.perform(get("/usuarios/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /usuarios/{id} deve retornar 404 quando não existir")
    void deveRetornar404() throws Exception {
        // Arrange
        when(usuarioService.buscarPorIdDto(99L))
                .thenThrow(new UsuarioNaoEncontradoException("Usuário não encontrado"));
        // Act + Assert
        mockMvc.perform(get("/usuarios/{id}", 99))
                .andExpect(status().isNotFound());
    }
}
