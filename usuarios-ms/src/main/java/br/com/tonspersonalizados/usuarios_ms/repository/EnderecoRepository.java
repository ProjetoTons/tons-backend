package br.com.tonspersonalizados.usuarios_ms.repository;

import br.com.tonspersonalizados.usuarios_ms.entity.Empresa;
import br.com.tonspersonalizados.usuarios_ms.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    Optional<Endereco> findByUsuarioId (Long id);
    Optional<Endereco> findByEmpresaId(Long id);
}
