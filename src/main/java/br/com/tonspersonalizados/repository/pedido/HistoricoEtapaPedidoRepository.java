package br.com.tonspersonalizados.repository.pedido;

import br.com.tonspersonalizados.entity.pedidos.HistoricoEtapaPedido;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoricoEtapaPedidoRepository extends JpaRepository<HistoricoEtapaPedido, Integer> {

    // Lista histórico em ordem cronológica
    List<HistoricoEtapaPedido> findByPedidoIdOrderByDataEntradaAsc(Integer idPedido);

    // Dashboard: performance de funcionários por macro-etapa
    @Query("SELECT h.responsavelEtapa.id, h.responsavelEtapa.nome, h.etapa, COUNT(h) " +
           "FROM HistoricoEtapaPedido h " +
           "WHERE h.dataEntrada >= :inicio AND h.dataEntrada <= :fim " +
           "AND h.etapa IN ('Design', 'Produção', 'Embalagem', 'Logística') " +
           "GROUP BY h.responsavelEtapa.id, h.responsavelEtapa.nome, h.etapa " +
           "ORDER BY h.responsavelEtapa.nome")
    List<Object[]> countByResponsavelAndEtapa(@Param("inicio") LocalDateTime inicio,
                                               @Param("fim") LocalDateTime fim);
}