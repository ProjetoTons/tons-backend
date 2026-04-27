package br.com.tonspersonalizados.dto.usuarios;

import br.com.tonspersonalizados.entity.usuarios.Acesso;

import java.time.LocalDate;
import java.util.List;

public interface FuncionarioResponseDto {
    Long getId();
    String getNome();
    String getTelefone();
    LocalDate getDataNascimento();
    List<Acesso> getAcessos();
}
