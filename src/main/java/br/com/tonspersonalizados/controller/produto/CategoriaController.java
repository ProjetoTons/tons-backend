package br.com.tonspersonalizados.controller.produto;

import br.com.tonspersonalizados.entity.produtos.CategoriaProduto;
import br.com.tonspersonalizados.service.produto.CategoriaProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaProdutoService categoriaProdutoService;


    public CategoriaController(CategoriaProdutoService categoriaProdutoService) {
        this.categoriaProdutoService = categoriaProdutoService;
    }


    @GetMapping
    public ResponseEntity<List<CategoriaProduto>> listarCategorias(){

        return ResponseEntity.status(200).body(categoriaProdutoService.listarTodos());
    }

}
