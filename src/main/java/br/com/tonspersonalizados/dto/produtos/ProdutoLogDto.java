package br.com.tonspersonalizados.dto.produtos;

import br.com.tonspersonalizados.entity.produtos.Produto;

public class ProdutoLogDto {
    private Long id;
    private String nome;
    private String descricao;
    private String tipoMaterial;
    private String nomeCategoria;

    public ProdutoLogDto() {
    }

    public ProdutoLogDto(Long id, String nome, String descricao, String tipoMaterial, String nomeCategoria) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tipoMaterial = tipoMaterial;
        this.nomeCategoria = nomeCategoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public static ProdutoLogDto from(Produto p) {
        if (p == null)
            return null;

        return new ProdutoLogDto(
                p.getId(),
                p.getNome(),
                p.getDescricao(),
                p.getTipoMaterial(),
                p.getCategoriaProduto() != null ? p.getCategoriaProduto().getNome() : null);
    }
}
