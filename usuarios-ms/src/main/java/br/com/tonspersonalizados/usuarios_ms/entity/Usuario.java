package br.com.tonspersonalizados.usuarios_ms.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;


    @Column(unique = true)
    private String cpf;

    @Column(nullable = false)
    private String telefone;

    private LocalDate dataNascimento;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime dataDeCadastro;


    private LocalDateTime dataDeDeletado;



    @OneToOne(mappedBy = "usuario",  cascade = CascadeType.ALL)
    private Login login;

    @ManyToMany // um usuário pode ter muitas roles
    private List<Acesso> acessos;

    @OneToOne (cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fk_endereco")
    @JsonBackReference
    private Endereco endereco;

    @ManyToOne
    private Empresa empresa;







    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public List<Acesso> getAcessos() {
        return acessos;
    }

    public void setAcessos(List<Acesso> acesso) {
        this.acessos = acesso;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }


    public LocalDateTime getDataDeCadastro() {
        return dataDeCadastro;
    }

    public void setDataDeCadastro(LocalDateTime dataDeCadastro) {
        this.dataDeCadastro = dataDeCadastro;
    }

    public LocalDateTime getDataDeDeletado() {
        return dataDeDeletado;
    }

    public void setDataDeDeletado(LocalDateTime dataDeDeletado) {
        this.dataDeDeletado = dataDeDeletado;
    }
}
