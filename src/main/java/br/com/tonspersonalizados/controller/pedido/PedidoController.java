package br.com.tonspersonalizados.controller.pedido;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.tonspersonalizados.dto.pedidos.CancelamentoPedidoRequestDto;
import br.com.tonspersonalizados.dto.pedidos.EtapaRequestDto;
import br.com.tonspersonalizados.dto.pedidos.HistoricoEtapaResponseDto;
import br.com.tonspersonalizados.dto.pedidos.PedidoRequestDto;
import br.com.tonspersonalizados.dto.pedidos.PedidoResponseDto;
import br.com.tonspersonalizados.service.pedido.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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

    @GetMapping("/meus")
    @Operation(summary = "Listar pedidos em andamento do cliente autenticado")
    public ResponseEntity<List<PedidoResponseDto>> meusPedidos() {
        Integer idCliente = getUsuarioAutenticadoId();
        return ResponseEntity.ok(pedidoService.listarMeusPedidosEmAndamento(idCliente));
    }

    @GetMapping("/meus/historico")
    @Operation(summary = "Listar pedidos finalizados do cliente autenticado")
    public ResponseEntity<List<PedidoResponseDto>> meusPedidosHistorico() {
        Integer idCliente = getUsuarioAutenticadoId();
        return ResponseEntity.ok(pedidoService.listarMeusPedidosFinalizados(idCliente));
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

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido completo com itens")
    public ResponseEntity<PedidoResponseDto> atualizarPedido(
            @PathVariable Integer id, @Valid @RequestBody PedidoRequestDto request) {
        return ResponseEntity.ok(pedidoService.atualizarPedido(id, request));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar pedido")
    public ResponseEntity<PedidoResponseDto> cancelarPedido(
            @PathVariable Integer id, @Valid @RequestBody CancelamentoPedidoRequestDto request) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id, request.getMotivo()));
    }

    private Integer getUsuarioAutenticadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getDetails();
        return userId.intValue();
    }
}