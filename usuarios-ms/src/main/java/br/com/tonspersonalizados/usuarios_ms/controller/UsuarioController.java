package br.com.tonspersonalizados.usuarios_ms.controller;


import br.com.tonspersonalizados.usuarios_ms.dto.EnderecoRequestDto;
import br.com.tonspersonalizados.usuarios_ms.dto.UsuarioRequestDto;
import br.com.tonspersonalizados.usuarios_ms.model.Empresa;
import br.com.tonspersonalizados.usuarios_ms.model.Endereco;
import br.com.tonspersonalizados.usuarios_ms.model.Login;
import br.com.tonspersonalizados.usuarios_ms.model.Usuario;
import br.com.tonspersonalizados.usuarios_ms.service.EmpresaService;
import br.com.tonspersonalizados.usuarios_ms.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;

    public UsuarioController(EmpresaService empresaService, UsuarioService usuarioService) {
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody @Valid UsuarioRequestDto dto) {

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setCpf(dto.getCpf());
        usuario.setTelefone(dto.getTelefone());

        Login login = new Login();
        login.setEmail(dto.getEmail());
        login.setSenhaHash(dto.getSenha());
        login.setUsuario(usuario);
        usuario.setLogin(login);

        Endereco endereco = new Endereco();
        endereco.setNumero(dto.getEndereco().getNumero());
        endereco.setLogradouro(dto.getEndereco().getLogadouro());
        endereco.setCep(dto.getEndereco().getCep());
        endereco.setComplemento(dto.getEndereco().getComplemento());
        endereco.setUsuario(usuario);
        usuario.setEndereco(endereco);


        if (dto.getCnpj() != null) {

            Empresa empresa = empresaService.buscarPorCnpj(dto.getCnpj());
            if (empresa == null) {
              return   ResponseEntity.status(404).body("Empresa não encontrada.");
            }

            usuario.setEmpresa(empresa);
        }


        usuarioService.cadastrar(usuario);

        return ResponseEntity.status(201).body("Usuário cadastrado com sucesso!");
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
