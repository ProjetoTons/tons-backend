package br.com.tonspersonalizados.usuarios_ms.controller;

import br.com.tonspersonalizados.usuarios_ms.dto.EmpresaRequestDto;
import br.com.tonspersonalizados.usuarios_ms.entity.Empresa;
import br.com.tonspersonalizados.usuarios_ms.service.EmpresaService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresas")
@Tag(name = "Empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<String> cadastrarEmpresa(@RequestBody @Valid EmpresaRequestDto dto) {

        empresaService.cadastrarEmpresa(dto);
        return ResponseEntity.status(201).body("Empresa cadastrada com sucesso.");
    }

    @GetMapping
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<List<Empresa>> listarTodos(){
        return ResponseEntity.ok(empresaService.listarTodos());
    }
}
