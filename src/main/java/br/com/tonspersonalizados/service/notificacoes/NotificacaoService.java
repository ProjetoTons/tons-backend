package br.com.tonspersonalizados.service.notificacoes;

import br.com.tonspersonalizados.dto.notificacoes.NotificacaoDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {
    private final RabbitTemplate rabbitTemplate;

    public NotificacaoService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarEmail(NotificacaoDto dto) {
        rabbitTemplate.convertAndSend(dto);
    }
}
