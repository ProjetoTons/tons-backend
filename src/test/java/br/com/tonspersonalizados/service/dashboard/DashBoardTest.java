package br.com.tonspersonalizados.service.dashboard;

import br.com.tonspersonalizados.dto.dashboard.GraficoEtapaDto;
import br.com.tonspersonalizados.dto.dashboard.KpisDashboardDto;
import br.com.tonspersonalizados.dto.dashboard.PerformanceFuncionarioDto;
import br.com.tonspersonalizados.dto.dashboard.SubEtapaDto;
import br.com.tonspersonalizados.entity.pedidos.Pedido;
import br.com.tonspersonalizados.entity.usuarios.Empresa;
import br.com.tonspersonalizados.repository.pedido.HistoricoEtapaPedidoRepository;
import br.com.tonspersonalizados.repository.pedido.PedidoRepository;
import br.com.tonspersonalizados.service.usuarios.EmpresaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private HistoricoEtapaPedidoRepository historicoRepository;

    @Mock
    private EmpresaService empresaService;

    @InjectMocks
    private DashboardService dashboardService;

    private final LocalDate startDate = LocalDate.of(2026, 1, 1);
    private final LocalDate endDate = LocalDate.of(2026, 1, 7);

    private Pedido criarPedido(String status, String etapa, BigDecimal valor) {
        Pedido pedido = new Pedido();
        pedido.setStatus(status);
        pedido.setEtapaPedido(etapa);
        pedido.setValorTotal(valor);
        return pedido;
    }

    @Nested
    @DisplayName("calcularKpis")
    class CalcularKpisTest {

        @Test
        @DisplayName("Deve calcular KPIs do dashboard corretamente")
        void deveCalcularKpisCorretamente() {
            // Arrange
            Pedido pedido1 = criarPedido("Aguardando arte", "Design", new BigDecimal("100.00"));
            Pedido pedido2 = criarPedido("Enviado", "Logística", new BigDecimal("200.00"));
            Pedido pedido3 = criarPedido("Aguardando retirada", "Logística", new BigDecimal("50.00"));

            Empresa empresa = new Empresa();
            empresa.setMetaSemanal(new BigDecimal("1000.00"));

            when(pedidoRepository.findByDataPedidoBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(pedido1, pedido2, pedido3));

            when(empresaService.buscarGrafica()).thenReturn(empresa);

            // Act
            KpisDashboardDto resultado = dashboardService.calcularKpis(startDate, endDate);

            // Assert
            assertNotNull(resultado);
            verify(pedidoRepository).findByDataPedidoBetween(any(LocalDateTime.class), any(LocalDateTime.class));
            verify(empresaService).buscarGrafica();
        }

        @Test
        @DisplayName("Deve calcular KPIs com lista vazia")
        void deveCalcularKpisComListaVazia() {
            // Arrange
            Empresa empresa = new Empresa();
            empresa.setMetaSemanal(new BigDecimal("1000.00"));

            when(pedidoRepository.findByDataPedidoBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());

            when(empresaService.buscarGrafica()).thenReturn(empresa);

            // Act
            KpisDashboardDto resultado = dashboardService.calcularKpis(startDate, endDate);

            // Assert
            assertNotNull(resultado);
            verify(pedidoRepository).findByDataPedidoBetween(any(LocalDateTime.class), any(LocalDateTime.class));
            verify(empresaService).buscarGrafica();
        }
    }

    @Nested
    @DisplayName("graficoEtapas")
    class GraficoEtapasTest {

        @Test
        @DisplayName("Deve agrupar pedidos por etapa")
        void deveAgruparPedidosPorEtapa() {
            // Arrange
            Pedido pedido1 = criarPedido("Em produção", "Design", new BigDecimal("100.00"));
            Pedido pedido2 = criarPedido("Em produção", "Design", new BigDecimal("200.00"));
            Pedido pedido3 = criarPedido("Pronto", "Produção", new BigDecimal("50.00"));

            when(pedidoRepository.findByDataPedidoBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(pedido1, pedido2, pedido3));

            // Act
            List<GraficoEtapaDto> resultado = dashboardService.graficoEtapas(startDate, endDate);

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(pedidoRepository).findByDataPedidoBetween(any(LocalDateTime.class), any(LocalDateTime.class));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver pedidos")
        void deveRetornarListaVaziaQuandoNaoHouverPedidos() {
            // Arrange
            when(pedidoRepository.findByDataPedidoBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());

            // Act
            List<GraficoEtapaDto> resultado = dashboardService.graficoEtapas(startDate, endDate);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(pedidoRepository).findByDataPedidoBetween(any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }

    @Nested
    @DisplayName("subEtapasPorEtapa")
    class SubEtapasPorEtapaTest {

        @Test
        @DisplayName("Deve agrupar pedidos por status dentro de uma etapa")
        void deveAgruparPedidosPorStatusDentroDaEtapa() {
            // Arrange
            Pedido pedido1 = criarPedido("Aguardando arte", "Design", new BigDecimal("100.00"));
            Pedido pedido2 = criarPedido("Aguardando arte", "Design", new BigDecimal("50.00"));
            Pedido pedido3 = criarPedido("Aprovado", "Design", new BigDecimal("200.00"));

            when(pedidoRepository.findByEtapaPedidoAndDataPedidoBetween(
                    eq("Design"),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            )).thenReturn(List.of(pedido1, pedido2, pedido3));

            // Act
            List<SubEtapaDto> resultado = dashboardService.subEtapasPorEtapa("Design", startDate, endDate);

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(pedidoRepository).findByEtapaPedidoAndDataPedidoBetween(
                    eq("Design"),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            );
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando etapa não possuir pedidos")
        void deveRetornarListaVaziaQuandoEtapaNaoPossuirPedidos() {
            // Arrange
            when(pedidoRepository.findByEtapaPedidoAndDataPedidoBetween(
                    eq("Design"),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            )).thenReturn(List.of());

            // Act
            List<SubEtapaDto> resultado = dashboardService.subEtapasPorEtapa("Design", startDate, endDate);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(pedidoRepository).findByEtapaPedidoAndDataPedidoBetween(
                    eq("Design"),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            );
        }
    }

    @Nested
    @DisplayName("performanceFuncionarios")
    class PerformanceFuncionariosTest {

        @Test
        @DisplayName("Deve montar performance dos funcionários por etapa")
        void deveMontarPerformanceDosFuncionariosPorEtapa() {
            // Arrange
            Object[] row1 = new Object[]{1L, "Gustavo", 3, 2, 0, 0};
            Object[] row2 = new Object[]{2L, "João", 0, 0, 4, 1};

            when(pedidoRepository.countTarefasAtivasPorFuncionario(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(row1, row2));

            // Act
            List<PerformanceFuncionarioDto> resultado =
                    dashboardService.performanceFuncionarios(startDate, endDate);

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(pedidoRepository).countTarefasAtivasPorFuncionario(any(LocalDateTime.class), any(LocalDateTime.class));
        }

        @Test
        @DisplayName("Deve retornar funcionário com todas as contagens zeradas")
        void deveRetornarFuncionarioComContagensZeradas() {
            // Arrange
            Object[] row = new Object[]{1L, "Gustavo", 0, 0, 0, 0};

            when(pedidoRepository.countTarefasAtivasPorFuncionario(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.<Object[]>of(row));

            // Act
            List<PerformanceFuncionarioDto> resultado =
                    dashboardService.performanceFuncionarios(startDate, endDate);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(pedidoRepository).countTarefasAtivasPorFuncionario(any(LocalDateTime.class), any(LocalDateTime.class));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver histórico")
        void deveRetornarListaVaziaQuandoNaoHouverHistorico() {
            // Arrange
            when(pedidoRepository.countTarefasAtivasPorFuncionario(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());

            // Act
            List<PerformanceFuncionarioDto> resultado =
                    dashboardService.performanceFuncionarios(startDate, endDate);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(pedidoRepository).countTarefasAtivasPorFuncionario(any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }
}