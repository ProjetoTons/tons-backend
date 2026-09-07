package br.com.tonspersonalizados.service.notificacoes;

import br.com.tonspersonalizados.dto.notificacoes.NotificacaoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class EmailServiceClient {
    private final RestClient restClient;
    private final String serviceToken;

    public EmailServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${email.service-url}") String serviceUrl,
            @Value("${email.service-token}") String serviceToken) {
        this.restClient = restClientBuilder.baseUrl(serviceUrl).build();
        this.serviceToken = serviceToken;
    }

    public void enviarEmail(NotificacaoDto notificacao) {
        try {
            restClient.post()
                    .uri("/api/v1/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(notificacao)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            throw new RuntimeException(
                    "Erro ao enviar email pelo microservico: " + exception.getMessage(), exception);
        }
    }
}