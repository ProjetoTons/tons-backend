package br.com.tonspersonalizados.dto.dashboard;

import java.math.BigDecimal;

public class SubEtapaDto {
    private String subEtapa;
    private int quantidadePedidos;
    private BigDecimal valorTotalArrecadado;

    public SubEtapaDto(String subEtapa, int quantidadePedidos, BigDecimal valorTotalArrecadado) {
        this.subEtapa = subEtapa;
        this.quantidadePedidos = quantidadePedidos;
        this.valorTotalArrecadado = valorTotalArrecadado;
    }

    public String getSubEtapa() { return subEtapa; }
    public void setSubEtapa(String subEtapa) { this.subEtapa = subEtapa; }
    public int getQuantidadePedidos() { return quantidadePedidos; }
    public void setQuantidadePedidos(int quantidadePedidos) { this.quantidadePedidos = quantidadePedidos; }
    public BigDecimal getValorTotalArrecadado() { return valorTotalArrecadado; }
    public void setValorTotalArrecadado(BigDecimal valorTotalArrecadado) { this.valorTotalArrecadado = valorTotalArrecadado; }
}
