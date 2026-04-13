package br.com.tonspersonalizados.usuarios_ms.dto;

import com.fasterxml.jackson.dataformat.yaml.util.StringQuotingChecker;

public class UsuarioTokenDto {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String token;


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
