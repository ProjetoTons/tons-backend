package br.com.tonspersonalizados.controller.dashboard;

import br.com.tonspersonalizados.dto.dashboard.*;
import br.com.tonspersonalizados.service.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Endpoints de estatísticas da dashboard")
@SecurityRequirement(name = "Bearer")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/kpis")
    @Operation(summary = "KPIs da dashboard", description = "Retorna totais, contagem por status no período.")
    public ResponseEntity<KpisDashboardDto> getKpis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(dashboardService.calcularKpis(startDate, endDate));
    }

    @GetMapping("/grafico-etapas")
    @Operation(summary = "Pedidos agrupados por macro-etapa", description = "Quantidade e valor por etapa no período.")
    public ResponseEntity<List<GraficoEtapaDto>> getGraficoEtapas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(dashboardService.graficoEtapas(startDate, endDate));
    }

    @GetMapping("/grafico-etapas/{etapa}")
    @Operation(summary = "Sub-etapas de uma macro-etapa (drill-down)", description = "Retorna sub-status de uma etapa específica.")
    public ResponseEntity<List<SubEtapaDto>> getSubEtapas(
            @PathVariable String etapa,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(dashboardService.subEtapasPorEtapa(etapa, startDate, endDate));
    }

    @GetMapping("/performance-funcionarios")
    @Operation(summary = "Performance de funcionários por macro-etapa", description = "Contagem de tarefas por funcionário e etapa.")
    public ResponseEntity<List<PerformanceFuncionarioDto>> getPerformance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(dashboardService.performanceFuncionarios(startDate, endDate));
    }
}
