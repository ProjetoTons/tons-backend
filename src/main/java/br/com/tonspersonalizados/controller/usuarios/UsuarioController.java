package br.com.tonspersonalizados.controller.usuarios;


import br.com.tonspersonalizados.dto.usuarios.*;
import br.com.tonspersonalizados.entity.usuarios.Empresa;
import br.com.tonspersonalizados.entity.usuarios.Endereco;
import br.com.tonspersonalizados.entity.usuarios.Login;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.service.usuarios.EmpresaService;
import br.com.tonspersonalizados.service.usuarios.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios")
//@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Cadastrar um novo usuário", description = "Realiza o cadastro de um novo usuário comum no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    })
    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody @Valid UsuarioRequestDto dto) {
        usuarioService.cadastrar(dto);

        return ResponseEntity.status(201).body("Usuário cadastrado com sucesso!");
    }

    @Operation(summary = "Autenticação (Login)", description = "Realiza a autenticação de um usuário ou funcionário e retorna um token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    })
    @PostMapping("/login")
    //usando o post por ser mais seguro já que ele possui uma critografia própria, melhor para transitar com a senha do usuário
    public ResponseEntity<UsuarioTokenDto> login(@RequestBody @Valid LoginRequestDto loginDto) {

        UsuarioTokenDto loginValidado = usuarioService.login(loginDto);


        return ResponseEntity.ok(loginValidado);
    }

    @Operation(summary = "Cadastrar um funcionário", description = "Realiza o cadastro de um novo funcionário. Requer autenticação e permissões específicas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Funcionário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @PostMapping("/funcionario")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<String> cadastrarFuncionario(@RequestBody @Valid FuncionarioRequestDto dto) {
        usuarioService.cadastrarFuncionario(dto);

        return ResponseEntity.status(201).body("Funcionário cadastrado com sucesso!");
    }


    @Operation(summary = "Buscar funcionários", description = "Recupera as informações de todos os funcionários. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionários encontrados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @GetMapping("funcionario")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<List<FuncionarioResponseDto>> buscarTodosFuncionarios(){
        return ResponseEntity.ok(usuarioService.listarFuncionarios());
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioRequestDto usuario) {
        usuarioService.atualizar(id, usuario);

        return ResponseEntity.ok("Usuário atualizado com sucesso!");
    }

    @Operation(summary = "Atualizar funcionário", description = "Atualiza os dados de um funcionário existente. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @PutMapping("funcionario/{id}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<String> atualizarFuncionario(@PathVariable Long id, @RequestBody @Valid FuncionarioRequestDto funcionario) {
        usuarioService.atualizarFuncionario(id, funcionario);

        return ResponseEntity.ok("Funcionário atualizado com sucesso.");
    }

    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema pelo seu ID. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<String> deletar(@PathVariable Long id) {

        usuarioService.deletar(id);

        return ResponseEntity.ok("Usuário deletado com sucesso!");
    }

    @Operation(summary = "Cadastrar endereço do usuário", description = "Cadastra um novo endereço associado a um usuário específico. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @PostMapping("/{id}/endereco")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Endereco> cadastrarEndereco(@RequestBody @Valid EnderecoRequestDto enderecoDto, @PathVariable Long id) {


        return ResponseEntity.status(201).body(usuarioService.cadastrarEnderecoUsuario(enderecoDto, id));

    }

    @Operation(summary = "Buscar endereço do usuário", description = "Recupera o endereço cadastrado para o ID do usuário fornecido. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço ou usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @GetMapping("/{id}/endereco")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Endereco> buscarEndereco(@PathVariable Long id) {

        return ResponseEntity.status(200).body(usuarioService.buscarEndereco(id));

    }

    @Operation(summary = "Atualizar endereço do usuário", description = "Atualiza os dados do endereço de um usuário específico. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado vinculados ao usuário"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @PutMapping("/{id}/endereco")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Endereco> atualizarEndereco(@RequestBody @Valid EnderecoRequestDto enderecoDto, @PathVariable Long id) {

        return ResponseEntity.status(200).body(usuarioService.atualizarEndereco(enderecoDto, id));
    }


    @Operation(summary = "Deletar endereço do usuário", description = "Remove o endereço associado ao ID do usuário. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @DeleteMapping("/{id}/endereco")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Void> deletarEndereco(@PathVariable Long id) {

        usuarioService.deletarEndereco(id);

        return ResponseEntity.status(204).build();
    }

}
