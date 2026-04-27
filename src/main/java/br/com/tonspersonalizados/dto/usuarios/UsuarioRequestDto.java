package br.com.tonspersonalizados.dto.usuarios;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;


public class UsuarioRequestDto {

    @NotBlank
    @NotNull
    private String nome;

    @NotNull
    @CPF
    private String cpf;

    @Email
    @NotNull
    private String email;

    @NotNull
    @Size(min = 11, max = 11)
    private String telefone;

    @NotNull
    private String senha;

    @CNPJ
    private String cnpj;


    public String getCnpj() {
        return cnpj;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }

    public String getTelefone() {
        return telefone;
    }
}
