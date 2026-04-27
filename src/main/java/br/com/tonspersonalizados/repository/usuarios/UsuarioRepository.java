package br.com.tonspersonalizados.repository.usuarios;

import br.com.tonspersonalizados.entity.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLoginEmail(String loginEmail);

    Optional<Usuario> findByNome(String nome);

}
