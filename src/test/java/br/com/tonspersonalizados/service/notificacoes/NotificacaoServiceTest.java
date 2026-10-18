package br.com.tonspersonalizados.service.notificacoes;

import br.com.tonspersonalizados.dto.notificacoes.NotificacaoDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

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
            verify(rabbitTemplate).convertAndSend(dto);
        }

        @Test
        @DisplayName("Deve propagar erro quando RabbitMQ falhar")
        void devePropagarErroQuandoRabbitMqFalhar() {

            // Arrange
            NotificacaoDto dto = criarDto();
            RuntimeException erro = new RuntimeException("RabbitMQ indisponível");

            doThrow(erro).when(rabbitTemplate).convertAndSend(dto);

            // Act + Assert
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> notificacaoService.enviarEmail(dto)
            );

            assertSame(erro, exception);

            verify(rabbitTemplate).convertAndSend(dto);
        }
    }
}
