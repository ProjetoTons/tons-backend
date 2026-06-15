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
}