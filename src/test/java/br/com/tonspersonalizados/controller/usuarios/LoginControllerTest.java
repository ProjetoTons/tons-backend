package br.com.tonspersonalizados.controller.usuarios;

import br.com.tonspersonalizados.config.AutenticacaoFilter;
import br.com.tonspersonalizados.config.SecurityConfiguracao;
import br.com.tonspersonalizados.dto.usuarios.UsuarioTokenDto;
import br.com.tonspersonalizados.exception.usuarios.UsuarioNaoEncontradoException;
import br.com.tonspersonalizados.service.usuarios.AutenticacaoService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de camada web do LoginController (@WebMvcTest + MockMvc).
 * 200 (login ok), 400 (e-mail inválido) e 404 (reset para usuário inexistente).
 */
@WebMvcTest(controllers = LoginController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguracao.class, AutenticacaoFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("LoginController (web)")
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutenticacaoService autenticacaoService;

    @Test
    @DisplayName("POST /login deve retornar 200 com credenciais válidas")
    void deveLogar() throws Exception {
        // Arrange
        when(autenticacaoService.login(any())).thenReturn(new UsuarioTokenDto());
        String body = "{\"email\":\"joao@email.com\",\"senha\":\"senha123\"}";

        // Act + Assert
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /login deve retornar 400 quando o e-mail for inválido")
    void deveRetornar400() throws Exception {
        // Arrange — e-mail malformado viola @Email
        String body = "{\"email\":\"naoEhEmail\",\"senha\":\"senha123\"}";

        // Act + Assert
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /login/reset-senha deve retornar 404 quando o usuário não existir")
    void deveRetornar404NoReset() throws Exception {
        // Arrange
        doThrow(new UsuarioNaoEncontradoException("Usuário não encontrado."))
                .when(autenticacaoService).resetarSenha(anyString(), anyString());
        String body = "{\"token\":\"abc\",\"novaSenha\":\"novaSenha123\"}";

        // Act + Assert
        mockMvc.perform(post("/login/reset-senha").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }
}
