package br.com.tonspersonalizados.usuarios_ms.controller;


import br.com.tonspersonalizados.usuarios_ms.dto.LoginRequestDto;
import br.com.tonspersonalizados.usuarios_ms.dto.UsuarioRequestDto;
import br.com.tonspersonalizados.usuarios_ms.dto.UsuarioResponseDto;
import br.com.tonspersonalizados.usuarios_ms.dto.UsuarioTokenDto;
import br.com.tonspersonalizados.usuarios_ms.entity.Empresa;
import br.com.tonspersonalizados.usuarios_ms.entity.Endereco;
import br.com.tonspersonalizados.usuarios_ms.entity.Login;
import br.com.tonspersonalizados.usuarios_ms.entity.Usuario;
import br.com.tonspersonalizados.usuarios_ms.service.EmpresaService;
import br.com.tonspersonalizados.usuarios_ms.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/usuarios")
//@CrossOrigin(origins = "*")
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
                return ResponseEntity.status(404).body("Empresa não encontrada.");
            }

            usuario.setEmpresa(empresa);
        }


        usuarioService.cadastrar(usuario);

        return ResponseEntity.status(201).body("Usuário cadastrado com sucesso!");
    }



    @PostMapping("/login") //usando o post por mais seguro já que ele possui uma cripografia própria, melhor para transitar com a senha do usuário
    public ResponseEntity<UsuarioResponseDto> login(@RequestBody @Valid LoginRequestDto loginDto){

        UsuarioTokenDto loginValidado = usuarioService.login(loginDto);

        UsuarioResponseDto responseDto = new UsuarioResponseDto();

        // loginValidado.().setUltimoLogin(LocalDateTime.now());

        // usuarioService.atualizar(loginValidado);

        return ResponseEntity.ok(responseDto);

    }


    //fazer um endppint para cadastrar usuario

    @GetMapping("/{nome}") //confirmar se vai precisar
    public ResponseEntity<Usuario> buscarPorNome(@PathVariable String nome) {
        Usuario usuario = usuarioService.buscarPorNome(nome);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(404).build();
    }

    @GetMapping("/email/{email}") //confirmar se vai precisar
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioRequestDto usuario) {
        usuarioService.atualizar(id, usuario);

        return ResponseEntity.ok("Usuário atualizado com sucesso!");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {

        usuarioService.deletar(id);

        return ResponseEntity.ok("Usuário deletado com sucesso!");

    }
}
