package br.com.tonspersonalizados.repository.pedido;

import br.com.tonspersonalizados.entity.pedidos.Pedido;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    @Query(value = "SELECT p FROM Pedido p " +
            "LEFT JOIN p.usuarioCliente cliente " +
            "LEFT JOIN p.usuario vendedor " +
            "LEFT JOIN p.usuarioResponsavel responsavel " +
            "WHERE (:etapa IS NULL OR p.etapaPedido = :etapa) " +
            "AND (:status IS NULL OR LOWER(p.status) = LOWER(:status)) " +
            "AND (:responsavelId IS NULL OR responsavel.id = :responsavelId) " +
            "AND (:somenteAtivos = false OR :etapa IS NOT NULL OR p.etapaPedido IS NULL " +
            "OR LOWER(p.etapaPedido) NOT IN ('finalizado', 'finalizados', 'cancelado')) " +
            "AND (:busca IS NULL " +
            "OR LOWER(COALESCE(p.numPedido, '')) LIKE LOWER(CONCAT('%', :busca, '%')) " +
            "OR LOWER(COALESCE(cliente.nome, '')) LIKE LOWER(CONCAT('%', :busca, '%')) " +
            "OR LOWER(COALESCE(vendedor.nome, '')) LIKE LOWER(CONCAT('%', :busca, '%')) " +
            "OR LOWER(COALESCE(responsavel.nome, '')) LIKE LOWER(CONCAT('%', :busca, '%')))",
            countQuery = "SELECT COUNT(p) FROM Pedido p " +
                    "LEFT JOIN p.usuarioCliente cliente " +
                    "LEFT JOIN p.usuario vendedor " +
                    "LEFT JOIN p.usuarioResponsavel responsavel " +
                    "WHERE (:etapa IS NULL OR p.etapaPedido = :etapa) " +
                    "AND (:status IS NULL OR LOWER(p.status) = LOWER(:status)) " +
                    "AND (:responsavelId IS NULL OR responsavel.id = :responsavelId) " +
                    "AND (:somenteAtivos = false OR :etapa IS NOT NULL OR p.etapaPedido IS NULL " +
                    "OR LOWER(p.etapaPedido) NOT IN ('finalizado', 'finalizados', 'cancelado')) " +
                    "AND (:busca IS NULL " +
                    "OR LOWER(COALESCE(p.numPedido, '')) LIKE LOWER(CONCAT('%', :busca, '%')) " +
                    "OR LOWER(COALESCE(cliente.nome, '')) LIKE LOWER(CONCAT('%', :busca, '%')) " +
                    "OR LOWER(COALESCE(vendedor.nome, '')) LIKE LOWER(CONCAT('%', :busca, '%')) " +
                    "OR LOWER(COALESCE(responsavel.nome, '')) LIKE LOWER(CONCAT('%', :busca, '%')))")
    Page<Pedido> buscarPedidos(
            @Param("etapa") String etapa,
            @Param("status") String status,
            @Param("busca") String busca,
            @Param("responsavelId") Long responsavelId,
            @Param("somenteAtivos") boolean somenteAtivos,
            Pageable pageable);

    // Listar pedidos ordenados por data (mais recente primeiro)
    List<Pedido> findAllByOrderByDataPedidoDesc();

    // Buscar pedidos de um cliente
    List<Pedido> findByUsuarioClienteIdOrderByDataPedidoDesc(Integer idCliente);

    // Dashboard: pedidos no período
    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    // Dashboard: pedidos de uma etapa no período (drill-down)
    List<Pedido> findByEtapaPedidoAndDataPedidoBetween(String etapaPedido, LocalDateTime inicio, LocalDateTime fim);

    // Meus pedidos em andamento — considera as formas singular e plural de finalização
    List<Pedido> findByUsuarioClienteIdAndEtapaPedidoNotInOrderByDataPedidoDesc(
            Integer idCliente, List<String> etapas);

    // Meus pedidos no histórico — considera as formas singular e plural de finalização
    List<Pedido> findByUsuarioClienteIdAndEtapaPedidoInOrderByDataFinalizacaoDesc(
            Integer idCliente, List<String> etapas);

    @Query(value = "SELECT p FROM Pedido p " +
            "WHERE p.usuarioCliente.id = :idCliente " +
            "AND LOWER(p.etapaPedido) IN :etapas",
            countQuery = "SELECT COUNT(p) FROM Pedido p " +
                    "WHERE p.usuarioCliente.id = :idCliente " +
                    "AND LOWER(p.etapaPedido) IN :etapas")
    Page<Pedido> buscarHistoricoClientePaginado(
            @Param("idCliente") Integer idCliente,
            @Param("etapas") List<String> etapas,
            Pageable pageable);

    // Substitua "usuarioResponsavel" pelo nome exato que está na sua classe Pedido.java se for diferente!
    @Query("SELECT p.usuarioResponsavel.id, p.usuarioResponsavel.nome, " +
            "SUM(CASE WHEN p.etapaPedido = 'Design' THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN p.etapaPedido = 'Produção' THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN p.etapaPedido = 'Embalagem' THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN p.etapaPedido = 'Logística' THEN 1L ELSE 0L END) " +
            "FROM Pedido p " +
            "WHERE LOWER(p.etapaPedido) NOT IN ('finalizado', 'finalizados', 'cancelado') " +
            "AND p.usuarioResponsavel IS NOT NULL " +
            "AND p.dataPedido BETWEEN :inicio AND :fim " +
            "GROUP BY p.usuarioResponsavel.id, p.usuarioResponsavel.nome")
    List<Object[]> countTarefasAtivasPorFuncionario(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}