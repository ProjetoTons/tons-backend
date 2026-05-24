package br.com.tonspersonalizados.dto.pedidos;

import jakarta.validation.constraints.Size;

public class CaracteristicasRequestDto {

    private String descricaoArte;

    @Size(max = 45)
    private String corEstampa;

    @Size(max = 45)
    private String corMaterial;

    @Size(max = 45)
    private String composicao;

    @Size(max = 45)
    private String tamanho;

    @Size(max = 45)
    private String fornecedor;

    public CaracteristicasRequestDto() {}


    // Getters e Setters

    public String getDescricaoArte() {
        return descricaoArte;
    }

    public void setDescricaoArte(String descricaoArte) {
        this.descricaoArte = descricaoArte;
    }

    public String getCorEstampa() {
        return corEstampa;
    }

    public void setCorEstampa(String corEstampa) {
        this.corEstampa = corEstampa;
    }

    public String getCorMaterial() {
        return corMaterial;
    }

    public void setCorMaterial(String corMaterial) {
        this.corMaterial = corMaterial;
    }

    public String getComposicao() {
        return composicao;
    }

    public void setComposicao(String composicao) {
        this.composicao = composicao;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }
}