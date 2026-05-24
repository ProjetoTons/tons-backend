package br.com.tonspersonalizados.dto.usuarios;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EnderecoRequestDto {

    @NotNull
    private String logadouro;

    @NotNull
    private String numero;

    @Size(min= 8, max = 8)
    @NotNull
    private String cep;


    private String complemento;

    private String bairro;

    private String cidade;

    private String estado;


    public String getCep() {
        return cep;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getLogadouro() {
        return logadouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }
}
