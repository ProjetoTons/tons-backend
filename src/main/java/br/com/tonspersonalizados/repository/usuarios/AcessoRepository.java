package br.com.tonspersonalizados.repository.usuarios;

import br.com.tonspersonalizados.entity.usuarios.Acesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcessoRepository extends JpaRepository<Acesso, Long> {
    List<Acesso> findAllByIdIn(List<Long> ids);
}
