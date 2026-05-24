package br.com.tonspersonalizados.dto.pedidos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EtapaRequestDto {

    @NotNull(message = "Responsável da etapa é obrigatório")
    private Long idResponsavelEtapa;

    @NotBlank(message = "Etapa é obrigatória")
    @Size(max = 45)
    private String etapa;

    @NotBlank(message = "Status é obrigatório")
    @Size(max = 45)
    private String status;

    @NotNull(message = "Data de entrada é obrigatória")
    private LocalDateTime dataEntrada;

    private LocalDateTime dataSaida;

    @Size(max = 250)
    private String observacoes;

    public EtapaRequestDto() {}


    // Getters e Setters
    public Long getIdResponsavelEtapa() {
        return idResponsavelEtapa;
    }

    public void setIdResponsavelEtapa(Long idResponsavelEtapa) {
        this.idResponsavelEtapa = idResponsavelEtapa;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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