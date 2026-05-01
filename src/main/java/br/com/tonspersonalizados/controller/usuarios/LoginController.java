package br.com.tonspersonalizados.controller.usuarios;

import br.com.tonspersonalizados.dto.usuarios.LoginRequestDto;
import br.com.tonspersonalizados.dto.usuarios.ResetSenhaRequestDto;
import br.com.tonspersonalizados.dto.usuarios.UsuarioTokenDto;
import br.com.tonspersonalizados.service.usuarios.AutenticacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@Tag(name = "Login")
public class LoginController {

    private final AutenticacaoService autenticacaoService;

    public LoginController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @Operation(summary = "Autenticação (Login)", description = "Realiza a autenticação de um usuário ou funcionário e retorna um token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    })
    @PostMapping
    //usando o post por ser mais seguro já que ele possui uma critografia própria, melhor para transitar com a senha do usuário
    public ResponseEntity<UsuarioTokenDto> login(@RequestBody @Valid LoginRequestDto loginDto) {

        UsuarioTokenDto loginValidado = autenticacaoService.login(loginDto);

        return ResponseEntity.ok(loginValidado);
    }

    @Operation(summary = "Enviar email para reset de senha", description = "Realiza o envio do email com o token de reset de senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Email não encontrado")
    })
    @PostMapping("/esqueci-senha")
    public ResponseEntity esqueceuSenha(@RequestParam @Valid String email) {
        autenticacaoService.enviarEmailResetSenha(email);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Troca da senha", description = "Realiza a troca da senha validando o token anteriormente enviado por email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "422", description = "Token informado inválido")
    })
    @PostMapping("/reset-senha")
    public ResponseEntity<Void> resetarSenha(@RequestBody @Valid ResetSenhaRequestDto request) {
        autenticacaoService.resetarSenha(request.getToken(), request.getNovaSenha());
        return ResponseEntity.noContent().build();
    }
}
