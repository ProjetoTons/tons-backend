package br.com.tonspersonalizados.repository.pedido;

import br.com.tonspersonalizados.entity.pedidos.HistoricoEtapaPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoricoEtapaPedidoRepository extends JpaRepository<HistoricoEtapaPedido, Integer> {

    // Lista histórico em ordem cronológica
    List<HistoricoEtapaPedido> findByPedidoIdOrderByDataEntradaAsc(Integer idPedido);
}