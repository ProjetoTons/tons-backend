package br.com.tonspersonalizados.usuarios_ms.repository;

import br.com.tonspersonalizados.usuarios_ms.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public Usuario findByEmail(String email);
    public Usuario findByNome(String nome);
}
