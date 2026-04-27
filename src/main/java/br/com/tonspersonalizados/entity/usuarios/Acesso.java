package br.com.tonspersonalizados.entity.usuarios;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "acessos")
public class Acesso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String descricao;


    //relacionamentos:
    @ManyToMany  // muitos acessos podem ser atribuidos a muitos usuários.
    private List<Usuario> usuarios;


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
