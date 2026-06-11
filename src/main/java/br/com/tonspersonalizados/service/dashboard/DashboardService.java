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

        List<Object[]> rows = historicoRepository.countByResponsavelAndEtapa(inicio, fim);

        Map<Long, Map<String, Object>> agrupado = new LinkedHashMap<>();

        for (Object[] row : rows) {
            Long idFunc = ((Number) row[0]).longValue();
            String nomeFunc = (String) row[1];
            String etapa = (String) row[2];
            int count = ((Number) row[3]).intValue();

            agrupado.computeIfAbsent(idFunc, k -> {
                Map<String, Object> m = new HashMap<>();
                m.put("nome", nomeFunc);
                m.put("design", 0);
                m.put("producao", 0);
                m.put("embalagem", 0);
                m.put("logistica", 0);
                return m;
            });

            Map<String, Object> func = agrupado.get(idFunc);
            switch (etapa) {
                case "Design" -> func.put("design", count);
                case "Produção" -> func.put("producao", count);
                case "Embalagem" -> func.put("embalagem", count);
                case "Logística" -> func.put("logistica", count);
            }
        }

        return agrupado.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> data = entry.getValue();
                    return new PerformanceFuncionarioDto(
                            entry.getKey(),
                            (String) data.get("nome"),
                            new PerformanceFuncionarioDto.TarefasDto(
                                    (int) data.get("design"),
                                    (int) data.get("producao"),
                                    (int) data.get("embalagem"),
                                    (int) data.get("logistica")
                            )
                    );
                })
                .collect(Collectors.toList());
    }
}
