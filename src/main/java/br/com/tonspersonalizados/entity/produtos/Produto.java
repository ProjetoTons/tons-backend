package br.com.tonspersonalizados.entity.produtos;

import br.com.tonspersonalizados.entity.usuarios.Usuario;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Produto {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String tipoMaterial;

    @ManyToOne
    @JoinColumn(name = "id_categoria") //fk
    private  CategoriaProduto categoriaProduto;

    @ManyToMany(mappedBy = "produtos") //nome da propriedade na classe usuario
    private List<Usuario> usuarios;

    //para a lista de interesses(carrinho de compras) do usuario
    @ManyToMany(mappedBy = "produtosDoCarrinho")
    private List<Usuario> usuariosInteressados;



    public CategoriaProduto getCategoriaProduto() {
        return categoriaProduto;
    }

    public void setCategoriaProduto(CategoriaProduto categoriaProduto) {
        this.categoriaProduto = categoriaProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Usuario> getUsuariosInteressados() {
        return usuariosInteressados;
    }

    public void setUsuariosInteressados(List<Usuario> usuariosInteressados) {
        this.usuariosInteressados = usuariosInteressados;
    }
}
