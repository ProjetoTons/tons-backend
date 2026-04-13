package br.com.tonspersonalizados.usuarios_ms.dto;

public class UsuarioResponseDto {
    private String nome;
    private String email;
    private String telefone;


    public void setEmail(String email) {
        this.email = email;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
