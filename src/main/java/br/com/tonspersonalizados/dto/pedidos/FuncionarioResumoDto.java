package br.com.tonspersonalizados.dto.pedidos;

public class FuncionarioResumoDto {
    private Long id;
    private String nome;

    public FuncionarioResumoDto() {}


    // Getters e Setters
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
}