package br.com.tonspersonalizados.service.notificacoes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WhatsAppService {

    private final RestClient restClient;
    private final String phoneNumberId;

    public WhatsAppService(
            @Value("${whatsapp.api.url}") String apiUrl,
            @Value("${whatsapp.api.phone-number-id}") String phoneNumberId,
            @Value("${whatsapp.api.access-token}") String accessToken
    ) {
        this.phoneNumberId = phoneNumberId;
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();
    }

    public String enviarTemplate(String telefone, String template) {
        String body = """
                {
                  "messaging_product": "whatsapp",
                  "to": "%s",
                  "type": "template",
                  "template": { "name": "%s", "language": { "code": "en_US" } }
                }
                """.formatted(limparTelefone(telefone), template);

        return enviar(body);
    }

    public String enviarTemplate(String telefone, String template, String... parametros) {
        if (parametros == null || parametros.length == 0) {
            return enviarTemplate(telefone, template);
        }

        StringBuilder parametersJson = new StringBuilder();
        for (int i = 0; i < parametros.length; i++) {
            if (i > 0) parametersJson.append(",");
            parametersJson.append("{\"type\":\"text\",\"text\":\"")
                          .append(escapar(parametros[i]))
                          .append("\"}");
        }

        String body = """
                {
                  "messaging_product": "whatsapp",
                  "to": "%s",
                  "type": "template",
                  "template": {
                    "name": "%s",
                    "language": { "code": "pt_BR" },
                    "components": [
                      { "type": "body", "parameters": [%s] }
                    ]
                  }
                }
                """.formatted(limparTelefone(telefone), template, parametersJson);

        return enviar(body);
    }

    public String enviarMensagem(String telefone, String mensagem) {
        String body = """
                {
                  "messaging_product": "whatsapp",
                  "to": "%s",
                  "type": "text",
                  "text": { "body": "%s" }
                }
                """.formatted(limparTelefone(telefone), mensagem);

        return enviar(body);
    }

    private String enviar(String body) {
        return restClient.post()
                .uri("/{phoneNumberId}/messages", phoneNumberId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    private String limparTelefone(String telefone) {
        return telefone.replaceAll("[^0-9]", "");
    }

    private String escapar(String valor) {
        if (valor == null) return "";
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
