package br.com.tonspersonalizados.dto.usuarios;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.List;

public class FuncionarioRequestDto {

    @NotBlank
    @NotNull
    private String nome;

    @Email
    @NotNull
    private String email;

    @NotNull
    @Size(min = 11, max = 11)
    private String telefone;

    @NotNull
    private String senha;

    @URL
    private String fotoUrl;

    @NotEmpty
    private String fotoPublicId;

    @NotNull
    private LocalDate dataNascimento;

    @NotEmpty
    private List<Long> acessos;

    //sem cpf para funcionario

    public List<Long> getAcessos() {
        return acessos;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
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

    public String getFotoUrl() { return fotoUrl; }

    public String getFotoPublicId() { return fotoPublicId; }
}
