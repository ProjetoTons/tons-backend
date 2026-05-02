package br.com.tonspersonalizados.controller.notificacoes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.tonspersonalizados.dto.notificacoes.WhatsAppRequestDto;
import br.com.tonspersonalizados.service.notificacoes.WhatsAppService;

@RestController
@RequestMapping("/whatsapp")
@CrossOrigin(origins = "*")
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    public WhatsAppController(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    @PostMapping("/enviar-mensagem")
    public ResponseEntity<String> enviarMensagem(@RequestBody WhatsAppRequestDto dto) {
        try {
            String resposta = whatsAppService.enviarMensagem(dto.getTelefone(), dto.getMensagem());
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao enviar WhatsApp: " + e.getMessage());
        }
    }

    @PostMapping("/confirmar-cadastro/{telefone}")
    public ResponseEntity<String> confirmarCadastro(
            @PathVariable String telefone,
            @RequestParam(required = false) String nome) {
        try {
            String resposta = whatsAppService.enviarTemplate(telefone, "hello_world");
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao enviar WhatsApp: " + e.getMessage());
        }
    }
}
