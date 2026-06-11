package br.com.tonspersonalizados.service.usuarios;

import br.com.tonspersonalizados.config.GerenciadorTokenJwt;
import br.com.tonspersonalizados.dto.usuarios.LoginRequestDto;
import br.com.tonspersonalizados.dto.usuarios.UsuarioTokenDto;
import br.com.tonspersonalizados.entity.usuarios.Empresa;
import br.com.tonspersonalizados.entity.usuarios.Login;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.exception.usuarios.LoginInvalidoException;
import br.com.tonspersonalizados.exception.usuarios.UsuarioNaoEncontradoException;
import br.com.tonspersonalizados.service.notificacoes.NotificacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes do AutenticacaoService — cobertura máxima com o mínimo de testes.
 * UsuarioService, GerenciadorTokenJwt, NotificacaoService, PasswordEncoder e
 * AuthenticationManager são dependências mockadas. Cada método tem 2 caminhos
 * (sucesso x exceção/validação) — exatamente os testes criados.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AutenticacaoService")
class AutenticacaoServiceTest {

    @Mock private UsuarioService usuarioService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private NotificacaoService notificacaoService;
    @Mock private GerenciadorTokenJwt gerenciadorTokenJwt;
    @Mock private AuthenticationManager authenticationManager;
    @InjectMocks private AutenticacaoService autenticacaoService;

    private Usuario usuarioCompleto() {
        Usuario u = new Usuario();
        u.setNome("João");
        Login login = new Login();
        login.setEmail("joao@email.com");
        login.setSenhaHash("hashAntigo");
        login.setUsuario(u);
        u.setLogin(login);
        return u;
    }

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserTest {

        @Test
        @DisplayName("Deve retornar UserDetails quando o usuário existir")
        void deveCarregarUsuario() {
            // Arrange
            when(usuarioService.buscarPorEmail("joao@email.com")).thenReturn(usuarioCompleto());
            // Act
            UserDetails details = autenticacaoService.loadUserByUsername("joao@email.com");
            // Assert
            assertNotNull(details);
        }

        @Test
        @DisplayName("Deve lançar UsernameNotFoundException quando o usuário não existir")
        void deveLancarQuandoNaoExiste() {
            // Arrange
            when(usuarioService.buscarPorEmail("x@x.com")).thenReturn(null);
            // Act + Assert
            assertThrows(UsernameNotFoundException.class,
                    () -> autenticacaoService.loadUserByUsername("x@x.com"));
        }
    }

    @Nested
    @DisplayName("login")
    class LoginTest {

        @Test
        @DisplayName("Deve autenticar, gerar token e registrar último login")
        void deveLogar() {
            // Arrange
            LoginRequestDto loginDto = mock(LoginRequestDto.class);
            when(loginDto.getEmail()).thenReturn("joao@email.com");
            Authentication auth = mock(Authentication.class);
            when(authenticationManager.authenticate(any())).thenReturn(auth);
            Usuario usuario = usuarioCompleto();
            Empresa empresa = new Empresa();
            empresa.setCnpj("99999999000100");
            usuario.setEmpresa(empresa); // cobre o ramo getEmpresa() != null do cnpj
            when(usuarioService.buscarPorEmail("joao@email.com")).thenReturn(usuario);
            when(gerenciadorTokenJwt.generateToken(any(), any())).thenReturn("token-jwt");

            // Act
            UsuarioTokenDto resultado = autenticacaoService.login(loginDto);

            // Assert
            assertEquals("token-jwt", resultado.getToken());
            assertEquals("99999999000100", resultado.getCnpj());
            assertNotNull(usuario.getLogin().getUltimoLogin());
            verify(usuarioService).atualizar(usuario);
        }

        @Test
        @DisplayName("Deve lançar LoginInvalidoException quando o usuário não for encontrado após autenticar")
        void deveLancarLoginInvalido() {
            // Arrange
            LoginRequestDto loginDto = mock(LoginRequestDto.class);
            when(loginDto.getEmail()).thenReturn("x@x.com");
            when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
            when(usuarioService.buscarPorEmail("x@x.com")).thenReturn(null);

            // Act + Assert
            assertThrows(LoginInvalidoException.class, () -> autenticacaoService.login(loginDto));
        }
    }

    @Nested
    @DisplayName("enviarEmailResetSenha")
    class ResetEmailTest {

        @Test
        @DisplayName("Deve gerar token e enviar e-mail de recuperação quando o usuário existir")
        void deveEnviarEmail() {
            // Arrange
            ReflectionTestUtils.setField(autenticacaoService, "resetUrl", "http://front/reset?token=");
            when(usuarioService.buscarPorEmail("joao@email.com")).thenReturn(usuarioCompleto());
            when(gerenciadorTokenJwt.generateResetToken(anyString(), anyString(), anyString()))
                    .thenReturn("reset-token");

            // Act
            autenticacaoService.enviarEmailResetSenha("joao@email.com");

            // Assert
            verify(notificacaoService).enviarEmail(any());
        }

        @Test
        @DisplayName("Deve lançar UsuarioNaoEncontradoException quando o e-mail não existir")
        void deveLancarQuandoNaoExiste() {
            // Arrange
            when(usuarioService.buscarPorEmail("x@x.com")).thenReturn(null);
            // Act + Assert
            assertThrows(UsuarioNaoEncontradoException.class,
                    () -> autenticacaoService.enviarEmailResetSenha("x@x.com"));
        }
    }

    @Nested
    @DisplayName("resetarSenha")
    class ResetarSenhaTest {

        @Test
        @DisplayName("Deve regravar a senha quando o token for válido")
        void deveResetar() {
            // Arrange
            Usuario usuario = usuarioCompleto();
            when(gerenciadorTokenJwt.getUserIdFromResetToken("tok")).thenReturn("1");
            when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
            when(gerenciadorTokenJwt.validateResetToken("tok", "hashAntigo")).thenReturn(true);
            when(passwordEncoder.encode("novaSenha")).thenReturn("hashNovo");

            // Act
            autenticacaoService.resetarSenha("tok", "novaSenha");

            // Assert
            assertEquals("hashNovo", usuario.getLogin().getSenhaHash());
            verify(usuarioService).atualizar(usuario);
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando o token for inválido")
        void deveLancarTokenInvalido() {
            // Arrange
            Usuario usuario = usuarioCompleto();
            when(gerenciadorTokenJwt.getUserIdFromResetToken("tok")).thenReturn("1");
            when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
            when(gerenciadorTokenJwt.validateResetToken("tok", "hashAntigo")).thenReturn(false);

            // Act + Assert
            assertThrows(ResponseStatusException.class,
                    () -> autenticacaoService.resetarSenha("tok", "novaSenha"));
            verify(usuarioService, never()).atualizar(any());
        }
    }

    @Nested
    @DisplayName("alterarSenha")
    class AlterarSenhaTest {

        @Test
        @DisplayName("Deve alterar a senha quando a senha atual estiver correta")
        void deveAlterar() {
            // Arrange
            Usuario usuario = usuarioCompleto();
            when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
            when(passwordEncoder.matches("atual", "hashAntigo")).thenReturn(true);
            when(passwordEncoder.encode("nova")).thenReturn("hashNovo");

            // Act
            autenticacaoService.alterarSenha(1L, "atual", "nova");

            // Assert
            assertEquals("hashNovo", usuario.getLogin().getSenhaHash());
            verify(usuarioService).atualizar(usuario);
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando a senha atual estiver incorreta")
        void deveLancarSenhaIncorreta() {
            // Arrange
            Usuario usuario = usuarioCompleto();
            when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
            when(passwordEncoder.matches("errada", "hashAntigo")).thenReturn(false);

            // Act + Assert
            assertThrows(ResponseStatusException.class,
                    () -> autenticacaoService.alterarSenha(1L, "errada", "nova"));
            verify(usuarioService, never()).atualizar(any());
        }
    }
}
