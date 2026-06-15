package br.com.tonspersonalizados.repository.pedido;

import br.com.tonspersonalizados.entity.pedidos.Pedido;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Substitua "usuarioResponsavel" pelo nome exato que está na sua classe Pedido.java se for diferente!
    @Query("SELECT p.usuarioResponsavel.id, p.usuarioResponsavel.nome, " +
            "SUM(CASE WHEN p.etapaPedido = 'Design' THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN p.etapaPedido = 'Produção' THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN p.etapaPedido = 'Embalagem' THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN p.etapaPedido = 'Logística' THEN 1L ELSE 0L END) " +
            "FROM Pedido p " +
            "WHERE p.etapaPedido NOT IN ('Finalizados', 'Cancelado') " +
            "AND p.usuarioResponsavel IS NOT NULL " +
            "AND p.dataPedido BETWEEN :inicio AND :fim " +
            "GROUP BY p.usuarioResponsavel.id, p.usuarioResponsavel.nome")
    List<Object[]> countTarefasAtivasPorFuncionario(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}