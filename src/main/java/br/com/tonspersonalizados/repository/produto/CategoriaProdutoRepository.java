package br.com.tonspersonalizados.repository.produto;

import br.com.tonspersonalizados.entity.produtos.CategoriaProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Long> {


}
