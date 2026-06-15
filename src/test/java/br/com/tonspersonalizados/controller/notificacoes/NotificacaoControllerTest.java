package br.com.tonspersonalizados.controller.notificacoes;

import br.com.tonspersonalizados.dto.notificacoes.NotificacaoDto;
import br.com.tonspersonalizados.service.notificacoes.NotificacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoControllerTest {

    @Mock
    private NotificacaoService service;

    @InjectMocks
    private NotificacaoController controller;

    @Nested
    @DisplayName("enviarEmail")
    class EnviarEmailTest {

        @Test
        @DisplayName("Deve retornar 200 quando e-mail for enviado com sucesso")
        void deveRetornarOkQuandoEmailForEnviadoComSucesso() {
            NotificacaoDto dto = new NotificacaoDto();

            doNothing().when(service).enviarEmail(dto);

            ResponseEntity<String> resposta = controller.enviarEmail(dto);

            assertEquals(200, resposta.getStatusCode().value());
            assertEquals("E-mail enviado com sucesso!", resposta.getBody());

            verify(service).enviarEmail(dto);
        }

        @Test
        @DisplayName("Deve retornar 500 quando ocorrer erro ao enviar e-mail")
        void deveRetornarErroQuandoFalharAoEnviarEmail() {
            NotificacaoDto dto = new NotificacaoDto();

            doThrow(new RuntimeException("SMTP indisponível"))
                    .when(service)
                    .enviarEmail(dto);

            ResponseEntity<String> resposta = controller.enviarEmail(dto);

            assertEquals(500, resposta.getStatusCode().value());
            assertEquals("Erro ao enviar e-mail: SMTP indisponível", resposta.getBody());

            verify(service).enviarEmail(dto);
        }
    }
}