package br.com.tonspersonalizados.repository.pedido;

import br.com.tonspersonalizados.entity.pedidos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // Listar pedidos ordenados por data (mais recente primeiro)
    List<Pedido> findAllByOrderByDataPedidoDesc();

    // Buscar pedidos de um cliente
    List<Pedido> findByUsuarioClienteIdOrderByDataPedidoDesc(Integer idCliente);
}