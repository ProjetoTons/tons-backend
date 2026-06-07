package br.com.tonspersonalizados.service.produto;

import br.com.tonspersonalizados.dto.produtos.ProdutoLogDto;
import br.com.tonspersonalizados.entity.AcaoLog;
import br.com.tonspersonalizados.entity.produtos.Produto;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.exception.produto.ProdutoNaoEncontradoException;
import br.com.tonspersonalizados.repository.produto.ProdutoRepository;
import br.com.tonspersonalizados.service.LogSistemaService;
import br.com.tonspersonalizados.service.usuarios.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UsuarioService usuarioService;
    private final LogSistemaService logSistemaService;

    public ProdutoService(ProdutoRepository produtoRepository,
            UsuarioService usuarioService,
            LogSistemaService logSistemaService) {
        this.usuarioService = usuarioService;
        this.produtoRepository = produtoRepository;
        this.logSistemaService = logSistemaService;
    }

    public List<Produto> obterTodos() {
        return produtoRepository.findAll();
    }

    public List<Produto> obterProdutosPorCategoria(Long id) {
        return produtoRepository.findAllByCategoriaProdutoId(id);
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));
    }

    public Produto cadastrar(Produto produto) {
        Produto salvo = produtoRepository.save(produto);

        logSistemaService.registrar(
                null, AcaoLog.CRIAR, "Produto",
                salvo.getId(), "Novo produto criado",
                null, ProdutoLogDto.from(salvo));

        return salvo;
    }

    public Produto atualizar(Produto produto) {
        Produto anterior = produtoRepository.findById(produto.getId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));

        ProdutoLogDto valorAnterior = ProdutoLogDto.from(anterior);

        Produto atualizado = produtoRepository.save(produto);

        logSistemaService.registrar(
                null, AcaoLog.ATUALIZAR, "Produto",
                atualizado.getId(), "Produto atualizado",
                valorAnterior, ProdutoLogDto.from(atualizado));

        return atualizado;
    }

    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));

        ProdutoLogDto anterior = ProdutoLogDto.from(produto);

        produtoRepository.delete(produto);

        logSistemaService.registrar(
                null, AcaoLog.DELETAR, "Produto",
                id, "Produto removido",
                anterior, null);
    }

    public List<Produto> listarFavoritos(Long idUsuario) {

        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        return usuario.getProdutosFavoritos();

    }

    public void favoritarProduto(Long idProduto, Long idUsuario) {

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));

        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        usuario.getProdutosFavoritos().add(produto);
        usuarioService.atualizar(usuario);

        ProdutoLogDto logDto = ProdutoLogDto.from(produto);

        logSistemaService.registrar(
                idUsuario, AcaoLog.FAVORITAR, "Produto",
                idProduto, "Produto adicionado aos favoritos",
                null, logDto);
    }

    public void removerProdutoFavoritado(Long idProduto, Long idUsuario) {

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));

        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        usuario.getProdutosFavoritos().remove(produto);
        usuarioService.atualizar(usuario);

        ProdutoLogDto logDto = ProdutoLogDto.from(produto);

        logSistemaService.registrar(
                idUsuario, AcaoLog.DESFAVORITAR, "Produto",
                idProduto, "Produto removido dos favoritos",
                logDto, null);
    }

    public List<Produto> listarInteressados(Long idUsuario) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        return usuario.getProdutosInterressados();
    }

    // seria o carrinho de compras
    public void salvarProdutoDeInteresse(Long idProduto, Long idUsuario) {

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));

        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        usuario.getProdutosInterressados().add(produto);
        usuarioService.atualizar(usuario);

        ProdutoLogDto logDto = ProdutoLogDto.from(produto);

        logSistemaService.registrar(
                idUsuario, AcaoLog.ADICIONAR_CARRINHO, "Produto",
                idProduto, "Produto adicionado ao interesse (carrinho)",
                null, logDto);
    }

    public void removerProdutoInteressado(Long idProduto, Long idUsuario) {

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));

        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        usuario.getProdutosInterressados().remove(produto);
        usuarioService.atualizar(usuario);

        ProdutoLogDto logDto = ProdutoLogDto.from(produto);

        logSistemaService.registrar(
                idUsuario, AcaoLog.REMOVER_CARRINHO, "Produto",
                idProduto, "Produto removido do interesse (carrinho)",
                logDto, null);
    }
}
