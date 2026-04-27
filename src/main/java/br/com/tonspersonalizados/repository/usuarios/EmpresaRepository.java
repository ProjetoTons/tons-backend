package br.com.tonspersonalizados.repository.usuarios;

import br.com.tonspersonalizados.entity.usuarios.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Empresa findByCnpj (String cnpj);


}
