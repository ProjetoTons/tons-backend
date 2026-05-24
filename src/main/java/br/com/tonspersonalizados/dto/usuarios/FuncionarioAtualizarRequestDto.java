package br.com.tonspersonalizados.dto.usuarios;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.util.List;

public class FuncionarioAtualizarRequestDto {


    @NotBlank
    @NotNull
    private String nome;

    @Email
    @NotNull
    private String email;

    @NotNull
    @Size(min = 11, max = 11)
    private String telefone;

    @NotEmpty
    private List<Long> acessos;

    @URL
    private String fotoUrl;

    @NotEmpty
    private String fotoPublicId;


    public List<Long> getAcessos() {
        return acessos;
    }

    public void setAcessos(List<Long> acessos) {
        this.acessos = acessos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getFotoPublicId() { return fotoPublicId; }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
