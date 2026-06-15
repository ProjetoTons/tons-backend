package br.com.tonspersonalizados.controller.notificacoes;

import br.com.tonspersonalizados.dto.notificacoes.WhatsAppRequestDto;
import br.com.tonspersonalizados.service.notificacoes.WhatsAppService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WhatsAppControllerTest {

    @Mock
    private WhatsAppService whatsAppService;

    @InjectMocks
    private WhatsAppController controller;

    @Nested
    @DisplayName("enviarMensagem")
    class EnviarMensagemTest {

        @Test
        @DisplayName("Deve retornar 200 quando mensagem for enviada")
        void deveRetornarOkQuandoMensagemForEnviada() {
            WhatsAppRequestDto dto = new WhatsAppRequestDto();
            dto.setTelefone("11999998888");
            dto.setMensagem("Olá");

            when(whatsAppService.enviarMensagem("11999998888", "Olá"))
                    .thenReturn("Mensagem enviada");

            ResponseEntity<String> resposta = controller.enviarMensagem(dto);

            assertEquals(200, resposta.getStatusCode().value());
            assertEquals("Mensagem enviada", resposta.getBody());
            verify(whatsAppService).enviarMensagem("11999998888", "Olá");
        }

        @Test
        @DisplayName("Deve retornar 500 quando falhar ao enviar mensagem")
        void deveRetornarErroQuandoFalharAoEnviarMensagem() {
            WhatsAppRequestDto dto = new WhatsAppRequestDto();
            dto.setTelefone("11999998888");
            dto.setMensagem("Olá");

            when(whatsAppService.enviarMensagem("11999998888", "Olá"))
                    .thenThrow(new RuntimeException("API fora do ar"));

            ResponseEntity<String> resposta = controller.enviarMensagem(dto);

            assertEquals(500, resposta.getStatusCode().value());
            assertEquals("Erro ao enviar WhatsApp: API fora do ar", resposta.getBody());
            verify(whatsAppService).enviarMensagem("11999998888", "Olá");
        }
    }

    @Nested
    @DisplayName("confirmarCadastro")
    class ConfirmarCadastroTest {

        @Test
        @DisplayName("Deve retornar 200 quando template for enviado")
        void deveRetornarOkQuandoTemplateForEnviado() {
            when(whatsAppService.enviarTemplate("11999998888", "hello_world"))
                    .thenReturn("Template enviado");

            ResponseEntity<String> resposta =
                    controller.confirmarCadastro("11999998888", "Gustavo");

            assertEquals(200, resposta.getStatusCode().value());
            assertEquals("Template enviado", resposta.getBody());
            verify(whatsAppService).enviarTemplate("11999998888", "hello_world");
        }

        @Test
        @DisplayName("Deve retornar 500 quando falhar ao enviar template")
        void deveRetornarErroQuandoFalharAoEnviarTemplate() {
            when(whatsAppService.enviarTemplate("11999998888", "hello_world"))
                    .thenThrow(new RuntimeException("Token inválido"));

            ResponseEntity<String> resposta =
                    controller.confirmarCadastro("11999998888", "Gustavo");

            assertEquals(500, resposta.getStatusCode().value());
            assertEquals("Erro ao enviar WhatsApp: Token inválido", resposta.getBody());
            verify(whatsAppService).enviarTemplate("11999998888", "hello_world");
        }
    }
}