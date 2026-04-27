package br.com.tonspersonalizados.repository.usuarios;

import br.com.tonspersonalizados.dto.usuarios.FuncionarioResponseDto;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLoginEmail(String loginEmail);

    List<FuncionarioResponseDto> findAllByIsFuncionario(Boolean isFuncionario);

}
