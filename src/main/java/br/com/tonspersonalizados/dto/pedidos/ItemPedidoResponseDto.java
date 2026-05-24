package br.com.tonspersonalizados.dto.pedidos;

import java.math.BigDecimal;

public class ItemPedidoResponseDto {
    private Long idProduto;
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private CaracteristicasRequestDto caracteristicas;

    public ItemPedidoResponseDto() {}

    // Getters e Setters
    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public CaracteristicasRequestDto getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(CaracteristicasRequestDto caracteristicas) {
        this.caracteristicas = caracteristicas;
    }
}