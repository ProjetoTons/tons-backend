package br.com.tonspersonalizados.service.notificacoes;

import br.com.tonspersonalizados.dto.notificacoes.NotificacaoDto;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {
    private final EmailServiceClient emailServiceClient;

    public NotificacaoService(EmailServiceClient emailServiceClient) {
        this.emailServiceClient = emailServiceClient;
    }

    public void enviarEmail(NotificacaoDto dto){
        emailServiceClient.enviarEmail(dto);
    }
}
