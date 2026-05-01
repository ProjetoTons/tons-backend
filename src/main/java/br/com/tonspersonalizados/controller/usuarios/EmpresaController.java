package br.com.tonspersonalizados.controller.usuarios;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tonspersonalizados.dto.usuarios.EmpresaRequestDto;
import br.com.tonspersonalizados.dto.usuarios.EnderecoRequestDto;
import br.com.tonspersonalizados.entity.usuarios.Empresa;
import br.com.tonspersonalizados.entity.usuarios.Endereco;
import br.com.tonspersonalizados.service.usuarios.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/empresas")
@Tag(name = "Empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @Operation(summary = "Cadastrar uma nova empresa", description = "Realiza o cadastro de uma nova empresa no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empresa cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @PostMapping
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<String> cadastrarEmpresa(@RequestBody @Valid EmpresaRequestDto dto) {

        empresaService.cadastrarEmpresa(dto);
        return ResponseEntity.status(201).body("Empresa cadastrada com sucesso.");
    }

    @Operation(summary = "Listar todas as empresas", description = "Retorna uma lista com todas as empresas cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @GetMapping
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<List<Empresa>> listarTodos() {
        return ResponseEntity.ok(empresaService.listarTodos());
    }

    @Operation(summary = "Buscar empresa por CNPJ", description = "Recupera os dados de uma empresa pelo CNPJ. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa encontrada"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @GetMapping("/cnpj/{cnpj}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Empresa> buscarPorCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(empresaService.buscarPorCnpj(cnpj));
    }

    @Operation(summary = "Cadastrar endereço da empresa", description = "Cadastra um novo endereço associado a uma empresa específica. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @PostMapping("/{id}/endereco")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Endereco> cadastrarEnderecoEmpresa(@RequestBody @Valid EnderecoRequestDto enderecoDto, @PathVariable Long id) {

        return ResponseEntity.status(201).body(empresaService.cadastrarEnderecoEmpresa(enderecoDto, id));
    }

    @Operation(summary = "Buscar endereço da empresa", description = "Recupera o endereço cadastrado para o ID da empresa fornecido. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço ou empresa não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @GetMapping("/{id}/endereco")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Endereco> buscarEndereco(@PathVariable Long id) {

        return ResponseEntity.status(200).body(empresaService.buscarEndereco(id));

    }

    @Operation(summary = "Atualizar endereço da empresa", description = "Atualiza os dados do endereço de uma empresa específica. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado vinculados à empresa"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @PutMapping("/{id}/endereco")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Endereco> atualizarEndereco(@RequestBody @Valid EnderecoRequestDto enderecoDto, @PathVariable Long id) {

        return ResponseEntity.status(200).body(empresaService.atualizarEndereco(enderecoDto, id));
    }


    @Operation(summary = "Deletar endereço da empresa", description = "Remove o endereço associado ao ID da empresa. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @DeleteMapping("/{id}/endereco")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Void> deletarEndereco(@PathVariable Long id) {

        empresaService.deletarEndereco(id);

        return ResponseEntity.status(204).build();
    }
}
