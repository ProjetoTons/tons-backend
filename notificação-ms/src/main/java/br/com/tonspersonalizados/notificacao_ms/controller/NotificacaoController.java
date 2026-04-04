package br.com.tonspersonalizados.notificacao_ms.controller;

import br.com.tonspersonalizados.notificacao_ms.dto.NotificacaoDto;
import br.com.tonspersonalizados.notificacao_ms.service.NotificacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificacao")
@CrossOrigin(origins = "*")
public class NotificacaoController {

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
            return ResponseEntity.internalServerError()
                    .body("Erro ao enviar e-mail: " + e.getMessage());
        }
    }


}
