package br.com.tonspersonalizados.entity.usuarios;

import br.com.tonspersonalizados.entity.produtos.Produto;
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

    private String fotoUrl;

    @Column(nullable = false)
    private Boolean isFuncionario; // Colocar essa coluna na modelagem

    private LocalDate dataNascimento;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime dataDeCadastro;

    private LocalDateTime dataDeDeletado;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Login login;

    @ManyToMany // muitos usuários pode ter muitas roles
    @JoinTable(
            name = "tipo_usuario",
            joinColumns = @JoinColumn(name = "fk_usuario"),
            inverseJoinColumns = @JoinColumn(name = "fk_acesso")
    )
    private List<Acesso> acessos;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fk_endereco")
    @JsonBackReference
    private Endereco endereco;

    @ManyToOne
    private Empresa empresa;


    @ManyToMany
    @JoinTable(
            name = "produto_favorito", //produtos salvos
            joinColumns = @JoinColumn(name = "fk_usuario"),
            inverseJoinColumns = @JoinColumn(name = "fk_produto")
    )
    private List<Produto> produtos;

    @ManyToMany
    @JoinTable(
            name = "produto_interesse",
            joinColumns = @JoinColumn(name = "fk_usuario"),
            inverseJoinColumns = @JoinColumn(name = "fk_produto")
    )
    private List<Produto> produtosDoCarrinho;


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

    public Boolean getFuncionario() {
        return isFuncionario;
    }

    public void setFuncionario(Boolean funcionario) {
        isFuncionario = funcionario;
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

    public List<Produto> getProdutosFavoritos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public List<Produto> getProdutosInterressados() {
        return produtosDoCarrinho;
    }

    public void setProdutosDoCarrinho(List<Produto> produtosDoCarrinho) {
        this.produtosDoCarrinho = produtosDoCarrinho;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public List<Produto> getProdutosDoCarrinho() {
        return produtosDoCarrinho;
    }
}
