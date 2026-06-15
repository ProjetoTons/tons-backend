package br.com.tonspersonalizados.service.notificacoes;

import br.com.tonspersonalizados.dto.notificacoes.NotificacaoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificacaoService notificacaoService;

    private NotificacaoDto criarDto() {
        NotificacaoDto dto = new NotificacaoDto();
        dto.setDestinatario("teste@email.com");
        dto.setAssunto("Assunto Teste");
        dto.setCorpo("Corpo Teste");
        return dto;
    }

    @Nested
    @DisplayName("enviarEmail")
    class EnviarEmailTest {

        @Test
        @DisplayName("Deve enviar e-mail com sucesso")
        void deveEnviarEmailComSucesso() {

            // Arrange
            NotificacaoDto dto = criarDto();

            // Act
            notificacaoService.enviarEmail(dto);

            // Assert
            ArgumentCaptor<SimpleMailMessage> captor =
                    ArgumentCaptor.forClass(SimpleMailMessage.class);

            verify(mailSender).send(captor.capture());

            SimpleMailMessage mensagem = captor.getValue();

            assertEquals("tonspersonalizadosdev@gmail.com", mensagem.getFrom());
            assertArrayEquals(
                    new String[]{"teste@email.com"},
                    mensagem.getTo()
            );
            assertEquals("Assunto Teste", mensagem.getSubject());
            assertEquals("Corpo Teste", mensagem.getText());
        }

        @Test
        @DisplayName("Deve lançar RuntimeException quando ocorrer erro ao enviar e-mail")
        void deveLancarRuntimeExceptionQuandoOcorrerErroAoEnviarEmail() {

            // Arrange
            NotificacaoDto dto = criarDto();

            doThrow(new RuntimeException("SMTP indisponível"))
                    .when(mailSender)
                    .send(any(SimpleMailMessage.class));

            // Act + Assert
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> notificacaoService.enviarEmail(dto)
            );

            assertTrue(
                    exception.getMessage()
                            .contains("Erro ao enviar email")
            );

            verify(mailSender).send(any(SimpleMailMessage.class));
        }
    }
}
