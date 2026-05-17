package br.com.tonspersonalizados.controller.produto;


import br.com.tonspersonalizados.entity.produtos.Produto;
import br.com.tonspersonalizados.service.produto.ProdutoService;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;


    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/categorias/{idCategoria}")
    public ResponseEntity<List<Produto>> listarProdutosPorCategoria(@PathVariable Long idCategoria) {

        return ResponseEntity.status(200).body(produtoService.obterProdutosPorCategoria(idCategoria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> listarProdutoPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(produtoService.buscarPorId(id));
    }


    @PostMapping("/{idProduto}/favorito")
    public ResponseEntity<Void> favoritarProduto(@PathVariable Long idProduto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();

        produtoService.favoritarProduto(idProduto, userId);

        return ResponseEntity.status(200).build();
    }


    @PostMapping("/{idProduto}/interesse")
    public ResponseEntity<Void> salarProdutosDeInteresse(@PathVariable Long idProduto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();

        produtoService.salvarProdutoDeInteresse(idProduto, userId);
        return ResponseEntity.status(200).build();

    }

    @GetMapping("/{idUsuario}/favoritos")
    public ResponseEntity<List<Produto>> listarProdutosFavoritos(@PathVariable Long idUsuario){

        return ResponseEntity.status(200).body(produtoService.listarFavoritos(idUsuario));

    }


    @DeleteMapping("/{idProduto}/favorito")
    public ResponseEntity<Void> removerProduto(@PathVariable Long idProduto) {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();

        produtoService.removerProduto(idProduto, userId);

        return ResponseEntity.status(204).build();
    }
}
