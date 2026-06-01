package br.com.tonspersonalizados.repository.pedido;

import br.com.tonspersonalizados.entity.pedidos.Pedido;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // Listar pedidos ordenados por data (mais recente primeiro)
    List<Pedido> findAllByOrderByDataPedidoDesc();

    // Buscar pedidos de um cliente
    List<Pedido> findByUsuarioClienteIdOrderByDataPedidoDesc(Integer idCliente);

    // Dashboard: pedidos no período
    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    // Dashboard: pedidos de uma etapa no período (drill-down)
    List<Pedido> findByEtapaPedidoAndDataPedidoBetween(String etapaPedido, LocalDateTime inicio, LocalDateTime fim);

    // Meus pedidos (em andamento) — etapa != "Finalizado"
    List<Pedido> findByUsuarioClienteIdAndEtapaPedidoNotOrderByDataPedidoDesc(Integer idCliente, String etapa);

    // Meus pedidos (histórico) — etapa = "Finalizado"
    List<Pedido> findByUsuarioClienteIdAndEtapaPedidoOrderByDataFinalizacaoDesc(Integer idCliente, String etapa);
}