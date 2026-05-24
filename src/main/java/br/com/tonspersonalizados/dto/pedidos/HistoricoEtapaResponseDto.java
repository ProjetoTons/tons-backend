package br.com.tonspersonalizados.dto.pedidos;

import java.time.LocalDateTime;

public class HistoricoEtapaResponseDto {
    private String etapa;
    private String statusEtapa;
    private String nomeResponsavel;
    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;
    private String observacoes;

    public HistoricoEtapaResponseDto() {}

    // Getters e Setters
    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getStatusEtapa() {
        return statusEtapa;
    }

    public void setStatusEtapa(String statusEtapa) {
        this.statusEtapa = statusEtapa;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}