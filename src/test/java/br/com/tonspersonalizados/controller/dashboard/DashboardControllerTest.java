package br.com.tonspersonalizados.controller.dashboard;

import br.com.tonspersonalizados.dto.dashboard.*;
import br.com.tonspersonalizados.service.dashboard.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardController")
class DashboardControllerTest {

    @Mock private DashboardService dashboardService;
    @InjectMocks private DashboardController controller;

    private final LocalDate start = LocalDate.of(2026, 1, 1);
    private final LocalDate end = LocalDate.of(2026, 1, 7);

    @Test
    @DisplayName("GET /dashboard/kpis deve retornar 200 com KPIs")
    void deveRetornarKpis() {
        KpisDashboardDto kpis = new KpisDashboardDto(BigDecimal.TEN, 1, 2, 3, BigDecimal.ONE, 10);
        when(dashboardService.calcularKpis(start, end)).thenReturn(kpis);

        ResponseEntity<KpisDashboardDto> resp = controller.getKpis(start, end);
        assertEquals(200, resp.getStatusCode().value());
        assertNotNull(resp.getBody());
    }

    @Test
    @DisplayName("GET /dashboard/grafico-etapas deve retornar 200")
    void deveRetornarGraficoEtapas() {
        when(dashboardService.graficoEtapas(start, end)).thenReturn(List.of());

        ResponseEntity<List<GraficoEtapaDto>> resp = controller.getGraficoEtapas(start, end);
        assertEquals(200, resp.getStatusCode().value());
    }

    @Test
    @DisplayName("GET /dashboard/grafico-etapas/{etapa} deve retornar 200")
    void deveRetornarSubEtapas() {
        when(dashboardService.subEtapasPorEtapa("Design", start, end)).thenReturn(List.of());

        ResponseEntity<List<SubEtapaDto>> resp = controller.getSubEtapas("Design", start, end);
        assertEquals(200, resp.getStatusCode().value());
    }

    @Test
    @DisplayName("GET /dashboard/performance-funcionarios deve retornar 200")
    void deveRetornarPerformance() {
        when(dashboardService.performanceFuncionarios(start, end)).thenReturn(List.of());

        ResponseEntity<List<PerformanceFuncionarioDto>> resp = controller.getPerformance(start, end);
        assertEquals(200, resp.getStatusCode().value());
    }
}
