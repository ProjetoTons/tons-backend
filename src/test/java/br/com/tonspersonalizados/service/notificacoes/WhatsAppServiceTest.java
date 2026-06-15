package br.com.tonspersonalizados.service.notificacoes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class WhatsAppServiceTest {

    private final WhatsAppService whatsAppService = new WhatsAppService(
            "http://localhost:9999",
            "123456",
            "token-teste"
    );

    @Nested
    @DisplayName("enviarTemplate")
    class EnviarTemplateTest {

        @Test
        @DisplayName("Deve tentar enviar template sem parâmetros")
        void deveTentarEnviarTemplateSemParametros() {
            assertThrows(
                    Exception.class,
                    () -> whatsAppService.enviarTemplate(
                            "(11) 99999-8888",
                            "hello_world"
                    )
            );
        }

        @Test
        @DisplayName("Deve tentar enviar template com parâmetros")
        void deveTentarEnviarTemplateComParametros() {
            assertThrows(
                    Exception.class,
                    () -> whatsAppService.enviarTemplate(
                            "(11) 99999-8888",
                            "confirmacao_cadastro",
                            "Gustavo"
                    )
            );
        }

        @Test
        @DisplayName("Deve tentar enviar template com parâmetros nulos e cair no template simples")
        void deveTentarEnviarTemplateComParametrosNulos() {
            assertThrows(
                    Exception.class,
                    () -> whatsAppService.enviarTemplate(
                            "(11) 99999-8888",
                            "hello_world",
                            (String[]) null
                    )
            );
        }

        @Test
        @DisplayName("Deve escapar aspas e barras nos parâmetros")
        void deveEscaparAspasEBarrasNosParametros() {
            assertThrows(
                    Exception.class,
                    () -> whatsAppService.enviarTemplate(
                            "(11) 99999-8888",
                            "confirmacao",
                            "Nome \"Teste\" \\ Gustavo"
                    )
            );
        }
    }

    @Nested
    @DisplayName("enviarMensagem")
    class EnviarMensagemTest {

        @Test
        @DisplayName("Deve tentar enviar mensagem de texto")
        void deveTentarEnviarMensagemDeTexto() {
            assertThrows(
                    Exception.class,
                    () -> whatsAppService.enviarMensagem(
                            "(11) 99999-8888",
                            "Olá, Gustavo"
                    )
            );
        }
    }
}