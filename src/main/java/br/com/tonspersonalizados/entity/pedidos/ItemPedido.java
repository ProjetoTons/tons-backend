package br.com.tonspersonalizados.entity.pedidos;

import br.com.tonspersonalizados.entity.produtos.Produto;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "item_pedido")
public class ItemPedido {

    @Id
    @Column(name = "id_item_pedido")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // A qual pedido este item pertence
    @ManyToOne
    @JoinColumn(name = "fk_pedido", nullable = false)
    private Pedido pedido;

    // Qual produto foi selecionado
    @ManyToOne
    @JoinColumn(name = "fk_produto", nullable = false)
    private Produto produto;

    // Características específicas deste item (cor, tamanho, arte, etc.)
    @OneToOne
    @JoinColumn(name = "fk_caracteristicas_item_pedido", nullable = false)
    private CaracteristicasItemPedido caracteristicas;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "valor_unitario_item", precision = 8, scale = 2)
    private BigDecimal valorUnitario;

    public ItemPedido() {}



    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public CaracteristicasItemPedido getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(CaracteristicasItemPedido caracteristicas) { this.caracteristicas = caracteristicas; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
}