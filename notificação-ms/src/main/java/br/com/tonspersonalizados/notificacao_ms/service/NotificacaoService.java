package br.com.tonspersonalizados.notificacao_ms.service;

import br.com.tonspersonalizados.notificacao_ms.dto.NotificacaoDto;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {
    private final JavaMailSender mailSender;

    public NotificacaoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(NotificacaoDto dto){
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom("tonspersonalizadosdev@gmail.com");
            mensagem.setTo(dto.getDestinatario());
            mensagem.setSubject(dto.getAssunto());
            mensagem.setText(dto.getCorpo());

            mailSender.send(mensagem);
        }catch (Exception e){
            throw new RuntimeException("Erro ao enviar email: " + e.getMessage());
        }
    }
}
