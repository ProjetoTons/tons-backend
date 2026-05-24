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

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos(){
        return ResponseEntity.status(200).body(produtoService.obterTodos());
    }

    @GetMapping("/categorias/{idCategoria}")
    public ResponseEntity<List<Produto>> listarProdutosPorCategoria(@PathVariable Long idCategoria) {

        return ResponseEntity.status(200).body(produtoService.obterProdutosPorCategoria(idCategoria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> listarProdutoPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(produtoService.buscarPorId(id));
    }

    @GetMapping("/interessados")
    public ResponseEntity<List<Produto>> listarProdutosInteressados(){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();

        return ResponseEntity.status(200).body(produtoService.listarInteressados(userId));
    }

    @PostMapping("/{idProduto}/interesse")
    public ResponseEntity<Void> salvarProdutoDeInteresse(@PathVariable Long idProduto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();

        produtoService.salvarProdutoDeInteresse(idProduto, userId);
        return ResponseEntity.status(200).build();

    }

    @DeleteMapping("/{idProduto}/interesse")
    public ResponseEntity<Void> deletarProdutoDeInteresse(@PathVariable Long idProduto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();

        produtoService.removerProdutoInteressado(idProduto, userId);
        return ResponseEntity.status(200).build();

    }

    @GetMapping("/favoritos")
    public ResponseEntity<List<Produto>> listarProdutosFavoritos(){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();

        return ResponseEntity.status(200).body(produtoService.listarFavoritos(userId));
    }

    @PostMapping("/{idProduto}/favorito")
    public ResponseEntity<Void> favoritarProduto(@PathVariable Long idProduto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();

        produtoService.favoritarProduto(idProduto, userId);

        return ResponseEntity.status(204).build();
    }

    @DeleteMapping("/{idProduto}/favorito")
    public ResponseEntity<Void> removerProdutoFavoritado(@PathVariable Long idProduto) {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();

        produtoService.removerProdutoFavoritado(idProduto, userId);

        return ResponseEntity.status(204).build();
    }
}
