package br.com.tonspersonalizados.usuarios_ms.controller;


import br.com.tonspersonalizados.usuarios_ms.dto.*;
import br.com.tonspersonalizados.usuarios_ms.entity.Empresa;
import br.com.tonspersonalizados.usuarios_ms.entity.Endereco;
import br.com.tonspersonalizados.usuarios_ms.entity.Login;
import br.com.tonspersonalizados.usuarios_ms.entity.Usuario;
import br.com.tonspersonalizados.usuarios_ms.service.EmpresaService;
import br.com.tonspersonalizados.usuarios_ms.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios")
//@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody @Valid UsuarioRequestDto dto) {
        usuarioService.cadastrar(dto);

        return ResponseEntity.status(201).body("Usuário cadastrado com sucesso!");
    }

    @PostMapping("/login") //usando o post por mais seguro já que ele possui uma critografia própria, melhor para transitar com a senha do usuário
    public ResponseEntity<UsuarioTokenDto> login(@RequestBody @Valid LoginRequestDto loginDto){

        UsuarioTokenDto loginValidado = usuarioService.login(loginDto);

        // loginValidado.().setUltimoLogin(LocalDateTime.now());

        // usuarioService.atualizar(loginValidado);

        return ResponseEntity.ok(loginValidado);
    }

    @PostMapping("/funcionario")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<String> cadastrarFuncionario(@RequestBody @Valid FuncionarioRequestDto dto) {
        usuarioService.cadastrarFuncionario(dto);

        return ResponseEntity.status(201).body("Funcionário cadastrado com sucesso!");
    }

    @GetMapping("/{nome}")
    public ResponseEntity<Usuario> buscarPorNome(@PathVariable String nome) {
        Usuario usuario = usuarioService.buscarPorNome(nome);

        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioRequestDto usuario) {
        usuarioService.atualizar(id, usuario);

        return ResponseEntity.ok("Usuário atualizado com sucesso!");
    }


    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<String> deletar(@PathVariable Long id) {

        usuarioService.deletar(id);

        return ResponseEntity.ok("Usuário deletado com sucesso!");
    }
}
