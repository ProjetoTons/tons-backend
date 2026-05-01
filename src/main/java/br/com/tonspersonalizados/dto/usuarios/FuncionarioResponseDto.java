package br.com.tonspersonalizados.dto.usuarios;

import br.com.tonspersonalizados.entity.usuarios.Acesso;

import java.time.LocalDate;
import java.util.List;

public class FuncionarioResponseDto {
    /*
    Long getId();
    String getNome();
    String getTelefone();
    LocalDate getDataNascimento();
    List<Acesso> getAcessos();
     */

    private Long id;
    private String nome;
    private String telefone;
    private LocalDate dataNascimento;
    private List<Acesso> acessos;

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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
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

    public void setAcessos(List<Acesso> acessos) {
        this.acessos = acessos;
    }
}
