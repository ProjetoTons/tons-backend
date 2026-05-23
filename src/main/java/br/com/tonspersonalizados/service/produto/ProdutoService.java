package br.com.tonspersonalizados.service.produto;

import br.com.tonspersonalizados.entity.produtos.Produto;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.exception.produto.ProdutoNaoEncontradoException;
import br.com.tonspersonalizados.repository.produto.ProdutoRepository;
import br.com.tonspersonalizados.service.usuarios.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UsuarioService usuarioService;

    public ProdutoService(ProdutoRepository produtoRepository, UsuarioService usuarioService) {
        this.produtoRepository = produtoRepository;
        this.usuarioService = usuarioService;
    }

    public List<Produto> obterTodos(){
        return produtoRepository.findAll();
    }

    public List<Produto> obterProdutosPorCategoria(Long id) {
        return produtoRepository.findAllByCategoriaProdutoId(id);
    }

    public Produto buscarPorId(Long id) {

        return produtoRepository.findById(id).orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));

    }

    public List<Produto> listarFavoritos(Long idUsuario){

        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        return  usuario.getProdutosFavoritos();

    }

    public void favoritarProduto(Long idProduto, Long idUsuario) {

        Produto produto = produtoRepository.findById(idProduto).orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));


        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        usuario.getProdutosFavoritos().add(produto);
        usuarioService.atualizar(usuario);

    }

    public void removerProdutoFavoritado(Long idProduto, Long idUsuario) {

        Produto produto = produtoRepository.findById(idProduto).orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));


        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        usuario.getProdutosFavoritos().remove(produto);
        usuarioService.atualizar(usuario);

    }

    public List<Produto> listarInteressados(Long idUsuario){
        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        return usuario.getProdutosInterressados();
    }

    //seria o carrinho de compras
    public void salvarProdutoDeInteresse(Long idProduto, Long idUsuario) {

        Produto produto = produtoRepository.findById(idProduto).orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));


        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        usuario.getProdutosInterressados().add(produto);
        usuarioService.atualizar(usuario);

    }

    public void removerProdutoInteressado(Long idProduto, Long idUsuario) {

        Produto produto = produtoRepository.findById(idProduto).orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));

        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        usuario.getProdutosInterressados().remove(produto);
        usuarioService.atualizar(usuario);

    }
}
