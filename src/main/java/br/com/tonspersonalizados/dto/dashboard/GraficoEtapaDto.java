package br.com.tonspersonalizados.dto.dashboard;

import java.math.BigDecimal;

public class GraficoEtapaDto {
    private String etapa;
    private int quantidadePedidos;
    private BigDecimal valorTotalArrecadado;

    public GraficoEtapaDto(String etapa, int quantidadePedidos, BigDecimal valorTotalArrecadado) {
        this.etapa = etapa;
        this.quantidadePedidos = quantidadePedidos;
        this.valorTotalArrecadado = valorTotalArrecadado;
    }

    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
    public int getQuantidadePedidos() { return quantidadePedidos; }
    public void setQuantidadePedidos(int quantidadePedidos) { this.quantidadePedidos = quantidadePedidos; }
    public BigDecimal getValorTotalArrecadado() { return valorTotalArrecadado; }
    public void setValorTotalArrecadado(BigDecimal valorTotalArrecadado) { this.valorTotalArrecadado = valorTotalArrecadado; }
}
