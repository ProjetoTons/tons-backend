package br.com.tonspersonalizados.dto.pedidos;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ItemPedidoRequestDto {

    @NotNull(message = "Produto é obrigatório")
    private Long idProduto;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    private Integer quantidade;

    @NotNull(message = "Valor unitário é obrigatório")
    @Positive(message = "Valor unitário deve ser positivo")
    private BigDecimal valorUnitario;

    @NotNull(message = "Características são obrigatórias")
    @Valid
    private CaracteristicasRequestDto caracteristicas;

    public ItemPedidoRequestDto() {}


    // Getters e Setters
    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
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