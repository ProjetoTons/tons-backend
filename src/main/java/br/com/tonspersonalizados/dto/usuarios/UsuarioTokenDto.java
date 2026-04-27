package br.com.tonspersonalizados.dto.usuarios;

import com.fasterxml.jackson.dataformat.yaml.util.StringQuotingChecker;

public class UsuarioTokenDto {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String token;

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getToken() {
        return token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
