package br.com.tonspersonalizados.service.produto;

import br.com.tonspersonalizados.entity.produtos.Produto;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.exception.produto.ProdutoNaoEncontradoException;
import br.com.tonspersonalizados.repository.produto.ProdutoRepository;
import br.com.tonspersonalizados.service.usuarios.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes do ProdutoService — cobertura máxima com o mínimo de testes.
 * UsuarioService é DEPENDÊNCIA (mockada): não testamos a busca de usuário aqui,
 * apenas controlamos o usuário que ela devolve.
 * O padrão findById().orElseThrow(ProdutoNaoEncontrado), repetido em 5 métodos,
 * é validado por UM representante (buscarPorId); nos demais, o teste de sucesso
 * cobre a mesma linha pelo caminho feliz.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoService")
class ProdutoServiceTest {

    @Mock private ProdutoRepository produtoRepository;
    @Mock private UsuarioService usuarioService;
    @InjectMocks private ProdutoService produtoService;

    /** Usuário com as duas coleções inicializadas (o new Usuario() as deixa nulas). */
    private Usuario usuarioComListas(List<Produto> favoritos, List<Produto> interessados) {
        Usuario u = new Usuario();
        u.setProdutos(new ArrayList<>(favoritos));            // getProdutosFavoritos()
        u.setProdutosDoCarrinho(new ArrayList<>(interessados)); // getProdutosInterressados()
        return u;
    }

    @Nested
    @DisplayName("consultas simples")
    class ConsultasTest {

        @Test
        @DisplayName("Deve delegar obterTodos ao repositório")
        void deveObterTodos() {
            // Arrange
            when(produtoRepository.findAll()).thenReturn(List.of(new Produto()));
            // Act
            List<Produto> resultado = produtoService.obterTodos();
            // Assert
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Deve buscar produtos por categoria")
        void deveObterPorCategoria() {
            // Arrange
            when(produtoRepository.findAllByCategoriaProdutoId(7L)).thenReturn(List.of(new Produto()));
            // Act
            List<Produto> resultado = produtoService.obterProdutosPorCategoria(7L);
            // Assert
            assertEquals(1, resultado.size());
        }

        // Representante do padrão ProdutoNaoEncontradoException.
        @Test
        @DisplayName("Deve lançar ProdutoNaoEncontradoException quando o id não existir")
        void deveLancarQuandoProdutoNaoExiste() {
            // Arrange
            when(produtoRepository.findById(99L)).thenReturn(Optional.empty());
            // Act + Assert
            assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.buscarPorId(99L));
        }
    }

    @Nested
    @DisplayName("favoritos")
    class FavoritosTest {

        @Test
        @DisplayName("Deve retornar a lista de favoritos do usuário")
        void deveListarFavoritos() {
            // Arrange
            Produto p = new Produto();
            when(usuarioService.buscarPorId(1L)).thenReturn(usuarioComListas(List.of(p), List.of()));
            // Act
            List<Produto> resultado = produtoService.listarFavoritos(1L);
            // Assert
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Deve adicionar o produto aos favoritos e salvar o usuário")
        void deveFavoritar() {
            // Arrange
            Produto produto = new Produto();
            Usuario usuario = usuarioComListas(List.of(), List.of());
            when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
            when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

            // Act
            produtoService.favoritarProduto(10L, 1L);

            // Assert
            assertTrue(usuario.getProdutosFavoritos().contains(produto));
            verify(usuarioService).atualizar(usuario);
        }

        @Test
        @DisplayName("Deve remover o produto dos favoritos e salvar o usuário")
        void deveRemoverFavorito() {
            // Arrange
            Produto produto = new Produto();
            Usuario usuario = usuarioComListas(List.of(produto), List.of());
            when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
            when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

            // Act
            produtoService.removerProdutoFavoritado(10L, 1L);

            // Assert
            assertFalse(usuario.getProdutosFavoritos().contains(produto));
            verify(usuarioService).atualizar(usuario);
        }
    }

    @Nested
    @DisplayName("interessados / carrinho")
    class InteressadosTest {

        @Test
        @DisplayName("Deve retornar a lista de interessados do usuário")
        void deveListarInteressados() {
            // Arrange
            when(usuarioService.buscarPorId(1L)).thenReturn(usuarioComListas(List.of(), List.of(new Produto())));
            // Act
            List<Produto> resultado = produtoService.listarInteressados(1L);
            // Assert
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Deve adicionar o produto aos interessados e salvar o usuário")
        void deveSalvarInteresse() {
            // Arrange
            Produto produto = new Produto();
            Usuario usuario = usuarioComListas(List.of(), List.of());
            when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
            when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

            // Act
            produtoService.salvarProdutoDeInteresse(10L, 1L);

            // Assert
            assertTrue(usuario.getProdutosInterressados().contains(produto));
            verify(usuarioService).atualizar(usuario);
        }

        @Test
        @DisplayName("Deve remover o produto dos interessados e salvar o usuário")
        void deveRemoverInteresse() {
            // Arrange
            Produto produto = new Produto();
            Usuario usuario = usuarioComListas(List.of(), List.of(produto));
            when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
            when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

            // Act
            produtoService.removerProdutoInteressado(10L, 1L);

            // Assert
            assertFalse(usuario.getProdutosInterressados().contains(produto));
            verify(usuarioService).atualizar(usuario);
        }
    }
}
