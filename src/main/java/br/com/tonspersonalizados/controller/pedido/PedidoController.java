package br.com.tonspersonalizados.controller.pedido;

import br.com.tonspersonalizados.dto.pedidos.*;
import br.com.tonspersonalizados.service.pedido.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Gerenciamento de pedidos da gráfica")
@SecurityRequirement(name = "Bearer")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @Operation(summary = "Criar pedido completo com itens")
    public ResponseEntity<PedidoResponseDto> criarPedido(@Valid @RequestBody PedidoRequestDto request) {
        return ResponseEntity.status(201).body(pedidoService.criarPedido(request));
    }

    @GetMapping
    @Operation(summary = "Listar todos os pedidos")
    public ResponseEntity<List<PedidoResponseDto>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID com itens")
    public ResponseEntity<PedidoResponseDto> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @PostMapping("/{id}/etapas")
    @Operation(summary = "Avançar/voltar etapa do pedido")
    public ResponseEntity<PedidoResponseDto> avancarEtapa(
            @PathVariable Integer id, @Valid @RequestBody EtapaRequestDto request) {
        return ResponseEntity.ok(pedidoService.avancarEtapa(id, request));
    }

    @PatchMapping("/{id}/responsavel")
    @Operation(summary = "Atribui responsável")
    public ResponseEntity<PedidoResponseDto> atribuirResponsavel(
            @PathVariable Integer id, @RequestParam Long idResponsavel) {
        return ResponseEntity.ok(pedidoService.atribuirResponsavel(id, idResponsavel));
    }

    @GetMapping("/{id}/etapas")
    @Operation(summary = "Histórico de etapas do pedido")
    public ResponseEntity<List<HistoricoEtapaResponseDto>> listarHistorico(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.listarHistorico(id));
    }
}