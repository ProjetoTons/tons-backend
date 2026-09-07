package br.com.tonspersonalizados.controller.notificacoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tonspersonalizados.dto.notificacoes.NotificacaoDto;
import br.com.tonspersonalizados.service.notificacoes.NotificacaoService;

@RestController
@RequestMapping("/notificacao")
@CrossOrigin(origins = "*")
public class NotificacaoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoController.class);
    private final NotificacaoService service;

    public NotificacaoController(NotificacaoService service) {
        this.service = service;
    }

    @PostMapping("/enviar-email")
    public ResponseEntity<String> enviarEmail(@RequestBody NotificacaoDto dto) {
        try {
            service.enviarEmail(dto);
            return ResponseEntity.ok("E-mail enviado com sucesso!");
        } catch (Exception e) {
            LOGGER.error("Falha ao enviar e-mail de notificacao", e);
            return ResponseEntity.internalServerError()
                .body("Nao foi possivel enviar o e-mail");
        }
    }
}
