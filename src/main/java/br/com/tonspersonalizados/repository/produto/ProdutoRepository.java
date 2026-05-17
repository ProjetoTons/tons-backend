package br.com.tonspersonalizados.repository.produto;

import br.com.tonspersonalizados.entity.produtos.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findAllByCategoriaProdutoId (Long id);
}
