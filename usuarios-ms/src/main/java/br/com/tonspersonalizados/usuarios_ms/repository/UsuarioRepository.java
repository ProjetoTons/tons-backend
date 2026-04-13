package br.com.tonspersonalizados.usuarios_ms.repository;

import br.com.tonspersonalizados.usuarios_ms.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

     Usuario findByLoginEmail(String loginEmail);
     Usuario findByNome(String nome);

     //Usuario findByLoginEmailAndLoginSenhaHash(String email, String senha);
}
