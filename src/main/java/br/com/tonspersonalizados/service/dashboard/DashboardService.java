package br.com.tonspersonalizados.service.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.tonspersonalizados.dto.dashboard.GraficoEtapaDto;
import br.com.tonspersonalizados.dto.dashboard.KpisDashboardDto;
import br.com.tonspersonalizados.dto.dashboard.PerformanceFuncionarioDto;
import br.com.tonspersonalizados.dto.dashboard.SubEtapaDto;
import br.com.tonspersonalizados.entity.pedidos.Pedido;
import br.com.tonspersonalizados.entity.usuarios.Empresa;
import br.com.tonspersonalizados.repository.pedido.HistoricoEtapaPedidoRepository;
import br.com.tonspersonalizados.repository.pedido.PedidoRepository;
import br.com.tonspersonalizados.service.usuarios.EmpresaService;

@Service
public class DashboardService {

    private final PedidoRepository pedidoRepository;
    private final HistoricoEtapaPedidoRepository historicoRepository;
    private final EmpresaService empresaService;

    public DashboardService(PedidoRepository pedidoRepository,
                            HistoricoEtapaPedidoRepository historicoRepository,
                            EmpresaService empresaService) {
        this.pedidoRepository = pedidoRepository;
        this.historicoRepository = historicoRepository;
        this.empresaService = empresaService;
    }

    public KpisDashboardDto calcularKpis(LocalDate startDate, LocalDate endDate) {
        LocalDateTime inicio = startDate.atStartOfDay();
        LocalDateTime fim = endDate.atTime(23, 59, 59);

        List<Pedido> pedidos = pedidoRepository.findByDataPedidoBetween(inicio, fim);

        BigDecimal totalValor = pedidos.stream()
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int aguardandoArte = (int) pedidos.stream()
                .filter(p -> "Aguardando arte".equalsIgnoreCase(p.getStatus()))
                .count();

        int aguardandoRetirada = (int) pedidos.stream()
                .filter(p -> "Aguardando retirada".equalsIgnoreCase(p.getStatus()))
                .count();

        int enviada = (int) pedidos.stream()
                .filter(p -> "Enviado".equalsIgnoreCase(p.getStatus()))
                .count();

        BigDecimal metaSemanal = BigDecimal.ZERO;
        try {
            Empresa empresa = empresaService.buscarGrafica();
            if (empresa.getMetaSemanal() != null) {
                metaSemanal = empresa.getMetaSemanal();
            }
        } catch (Exception e) {
            // empresa gráfica não cadastrada ainda
        }

        return new KpisDashboardDto(totalValor, aguardandoArte, aguardandoRetirada, enviada,
                metaSemanal, pedidos.size());
    }

    public List<GraficoEtapaDto> graficoEtapas(LocalDate startDate, LocalDate endDate) {
        LocalDateTime inicio = startDate.atStartOfDay();
        LocalDateTime fim = endDate.atTime(23, 59, 59);

        List<Pedido> pedidos = pedidoRepository.findByDataPedidoBetween(inicio, fim);

        Map<String, List<Pedido>> porEtapa = pedidos.stream()
                .filter(p -> p.getEtapaPedido() != null)
                .collect(Collectors.groupingBy(Pedido::getEtapaPedido));

        return porEtapa.entrySet().stream()
                .map(entry -> new GraficoEtapaDto(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream()
                                .map(Pedido::getValorTotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .collect(Collectors.toList());
    }

    public List<SubEtapaDto> subEtapasPorEtapa(String etapa, LocalDate startDate, LocalDate endDate) {
        LocalDateTime inicio = startDate.atStartOfDay();
        LocalDateTime fim = endDate.atTime(23, 59, 59);

        List<Pedido> pedidos = pedidoRepository.findByEtapaPedidoAndDataPedidoBetween(etapa, inicio, fim);

        Map<String, List<Pedido>> porStatus = pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getStatus));

        return porStatus.entrySet().stream()
                .map(entry -> new SubEtapaDto(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream()
                                .map(Pedido::getValorTotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .collect(Collectors.toList());
    }

    public List<PerformanceFuncionarioDto> performanceFuncionarios(LocalDate startDate, LocalDate endDate) {
        LocalDateTime inicio = startDate.atStartOfDay();
        LocalDateTime fim = endDate.atTime(23, 59, 59);

        List<Object[]> rows = pedidoRepository.countTarefasAtivasPorFuncionario(inicio, fim);

        return rows.stream().map(row -> {
            Long idFunc = ((Number) row[0]).longValue();
            String nomeFunc = (String) row[1];
            int countDesign = ((Number) row[2]).intValue();
            int countProducao = ((Number) row[3]).intValue();
            int countEmbalagem = ((Number) row[4]).intValue();
            int countLogistica = ((Number) row[5]).intValue();

            return new PerformanceFuncionarioDto(
                    idFunc,
                    nomeFunc,
                    new PerformanceFuncionarioDto.TarefasDto(
                            countDesign,
                            countProducao,
                            countEmbalagem,
                            countLogistica
                    )
            );
        }).collect(Collectors.toList());
    }
}
