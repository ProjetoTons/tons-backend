package br.com.tonspersonalizados.usuarios_ms.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CNPJ;

public class EmpresaRequestDto {

    @NotNull
    private String nomeFantasia;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String telefone;

    @NotNull
    private String razaoSocial;

    @CNPJ
    private String cnpj;


    public String getCnpj() {
        return cnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public String getEmail() {
        return email;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }
}
