package br.com.tonspersonalizados.dto.usuarios;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CNPJ;

import java.time.LocalDate;
import java.util.List;

public class UsuarioAtualizarRequestDto {


    @NotBlank
    @NotNull
    private String nome;

    @Email
    @NotNull
    private String email;

    @NotNull
    @Size(min = 11, max = 11)
    private String telefone;

    private Long idEmpresa;

    private EnderecoRequestDto endereco;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public Long getIdEmpresa() { return idEmpresa; }

    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public EnderecoRequestDto getEndereco() { return endereco; }
}
