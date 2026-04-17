package br.com.tonspersonalizados.usuarios_ms.repository;

import br.com.tonspersonalizados.usuarios_ms.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLoginEmail(String loginEmail);

    Optional<Usuario> findByNome(String nome);

}
