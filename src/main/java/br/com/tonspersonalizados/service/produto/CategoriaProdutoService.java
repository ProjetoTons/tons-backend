package br.com.tonspersonalizados.service.produto;

import br.com.tonspersonalizados.entity.produtos.CategoriaProduto;
import br.com.tonspersonalizados.repository.produto.CategoriaProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaProdutoService {

    private final CategoriaProdutoRepository categoriaReposirory;

    public CategoriaProdutoService(CategoriaProdutoRepository categoriaReposirory) {
        this.categoriaReposirory = categoriaReposirory;
    }

    // listar sessão
    public List<CategoriaProduto> listarTodos() {
        return categoriaReposirory.findAll();

    }
}