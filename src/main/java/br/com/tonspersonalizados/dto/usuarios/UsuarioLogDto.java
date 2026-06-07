package br.com.tonspersonalizados.dto.usuarios;

import br.com.tonspersonalizados.entity.usuarios.Usuario;

public class UsuarioLogDto {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private Boolean isFuncionario;

    public UsuarioLogDto() {}

    public UsuarioLogDto(Long id, String nome, String email,
                         String cpf, String telefone, Boolean isFuncionario) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
        this.isFuncionario = isFuncionario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public Boolean getIsFuncionario() { return isFuncionario; }
    public void setIsFuncionario(Boolean isFuncionario) { this.isFuncionario = isFuncionario; }

    /** Cria um snapshot seguro de um usuário (sem senha nem tokens). */
    public static UsuarioLogDto from(Usuario u) {
        return new UsuarioLogDto(
                u.getId(),
                u.getNome(),
                u.getLogin() != null ? u.getLogin().getEmail() : null,
                u.getCpf(),
                u.getTelefone(),
                u.getFuncionario()
        );
    }
}
