package br.com.tonspersonalizados.repository.usuarios;

import br.com.tonspersonalizados.entity.usuarios.Empresa;
import br.com.tonspersonalizados.entity.usuarios.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    Optional<Endereco> findByUsuarioId (Long id);
    Optional<Endereco> findByEmpresaId(Long id);
}
