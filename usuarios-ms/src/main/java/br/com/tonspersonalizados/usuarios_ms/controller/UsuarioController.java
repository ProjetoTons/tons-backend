package br.com.tonspersonalizados.usuarios_ms.controller;


import br.com.tonspersonalizados.usuarios_ms.model.Usuario;
import br.com.tonspersonalizados.usuarios_ms.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody Usuario usuario) {
        Boolean resultado = usuarioService.cadastrar(usuario);
        if (resultado) {
            return ResponseEntity.status(201).body("Usuário cadastrado com sucesso!");
        }
        return ResponseEntity.status(400).body("Erro ao cadastrar usuário.");
    }

    @GetMapping("/{nome}")
    public ResponseEntity<Usuario> buscarPorNome(@PathVariable String nome) {
        Usuario usuario = usuarioService.buscarPorNome(nome);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(404).build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        usuario.setId(id);

        Boolean resultado = usuarioService.atualizar(usuario);
        if (resultado) {
            return ResponseEntity.ok("Usuário atualizado com sucesso!");
        }
        return ResponseEntity.status(400).body("Erro ao atualizar usuário.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        Boolean resultado = usuarioService.deletar(id);
        if (resultado) {
            return ResponseEntity.ok("Usuário deletado com sucesso!");
        }
        return ResponseEntity.status(400).body("Erro ao deletar usuário.");
    }
}
