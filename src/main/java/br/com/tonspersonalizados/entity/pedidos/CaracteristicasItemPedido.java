package br.com.tonspersonalizados.entity.pedidos;

import jakarta.persistence.*;

@Entity
@Table(name = "caracteristicas_item_pedido")
public class CaracteristicasItemPedido {

    @Id
    @Column(name = "id_caracteristicas_item_pedido")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "descricao_arte", columnDefinition = "TEXT")
    private String descricaoArte;

    @Column(name = "cor_estampa", length = 45)
    private String corEstampa;

    @Column(name = "cor_material", length = 45)
    private String corMaterial;

    @Column(name = "composicao", length = 45)
    private String composicao;

    @Column(name = "tamanho", length = 45)
    private String tamanho;

    @Column(name = "fornecedor", length = 45)
    private String fornecedor;

    public CaracteristicasItemPedido() {}




    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDescricaoArte() { return descricaoArte; }
    public void setDescricaoArte(String descricaoArte) { this.descricaoArte = descricaoArte; }

    public String getCorEstampa() { return corEstampa; }
    public void setCorEstampa(String corEstampa) { this.corEstampa = corEstampa; }

    public String getCorMaterial() { return corMaterial; }
    public void setCorMaterial(String corMaterial) { this.corMaterial = corMaterial; }

    public String getComposicao() { return composicao; }
    public void setComposicao(String composicao) { this.composicao = composicao; }

    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }

    public String getFornecedor() { return fornecedor; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }
}