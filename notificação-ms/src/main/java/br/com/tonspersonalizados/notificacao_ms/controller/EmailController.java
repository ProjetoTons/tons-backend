package br.com.tonspersonalizados.notificacao_ms.controller;

import br.com.tonspersonalizados.notificacao_ms.dto.EmailDto;
import br.com.tonspersonalizados.notificacao_ms.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificacao")
@CrossOrigin(origins = "*")
public class EmailController {

    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    @PostMapping("/enviar-email")
    public ResponseEntity<String> enviarEmail(@RequestBody EmailDto dto) {
        try {
            service.enviarEmail(dto);
            return ResponseEntity.ok("E-mail enviado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao enviar e-mail: " + e.getMessage());
        }
    }


}
