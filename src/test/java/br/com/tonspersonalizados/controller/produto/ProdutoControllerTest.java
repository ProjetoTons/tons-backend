package br.com.tonspersonalizados.controller.produto;

import br.com.tonspersonalizados.config.AutenticacaoFilter;
import br.com.tonspersonalizados.config.SecurityConfiguracao;
import br.com.tonspersonalizados.entity.produtos.Produto;
import br.com.tonspersonalizados.exception.produto.ProdutoNaoEncontradoException;
import br.com.tonspersonalizados.service.produto.ProdutoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de camada web do ProdutoController com @WebMvcTest + MockMvc.
 *
 * - addFilters=false e a exclusão de SecurityConfiguracao/AutenticacaoFilter
 *   tiram a stack de segurança (JWT) do contexto: aqui interessa só o status HTTP.
 * - O ProdutoService é substituído por um mock (@MockitoBean — sucessor do
 *   @MockBean, removido no Spring Boot 4).
 * - Testamos apenas os endpoints públicos GET (os demais dependem do
 *   SecurityContext, fora do escopo deste slice).
 */
@WebMvcTest(controllers = ProdutoController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguracao.class, AutenticacaoFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ProdutoController (web)")
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;

    @Test
    @DisplayName("GET /produtos deve retornar 200")
    void deveListarProdutos() throws Exception {
        // Arrange (lista vazia: evita serializar a entidade Produto, cujo getId() é primitivo)
        when(produtoService.obterTodos()).thenReturn(List.of());

        // Act + Assert
        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /produtos/{id} deve retornar 200 quando o produto existir")
    void deveBuscarPorId() throws Exception {
        // Arrange (id setado: getId() é primitivo sobre campo Long, evita NPE na serialização)
        Produto produto = new Produto();
        produto.setId(1L);
        when(produtoService.buscarPorId(1L)).thenReturn(produto);

        // Act + Assert
        mockMvc.perform(get("/produtos/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /produtos/{id} deve retornar 404 quando o produto não existir")
    void deveRetornar404QuandoNaoExiste() throws Exception {
        // Arrange — exceção anotada com @ResponseStatus(NOT_FOUND)
        when(produtoService.buscarPorId(99L))
                .thenThrow(new ProdutoNaoEncontradoException("Produto não encontrado"));

        // Act + Assert
        mockMvc.perform(get("/produtos/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /produtos/categorias/{id} deve retornar 200")
    void deveListarPorCategoria() throws Exception {
        when(produtoService.obterProdutosPorCategoria(1L)).thenReturn(List.of());
        mockMvc.perform(get("/produtos/categorias/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /produtos/favoritos deve retornar 200")
    void deveListarFavoritos() throws Exception {
        setSecurityContext(1L);
        when(produtoService.listarFavoritos(1L)).thenReturn(List.of());
        mockMvc.perform(get("/produtos/favoritos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /produtos/{id}/favorito deve retornar 204")
    void deveFavoritar() throws Exception {
        setSecurityContext(1L);
        mockMvc.perform(post("/produtos/{id}/favorito", 10))
                .andExpect(status().isNoContent());
        verify(produtoService).favoritarProduto(10L, 1L);
    }

    @Test
    @DisplayName("DELETE /produtos/{id}/favorito deve retornar 204")
    void deveRemoverFavorito() throws Exception {
        setSecurityContext(1L);
        mockMvc.perform(delete("/produtos/{id}/favorito", 10))
                .andExpect(status().isNoContent());
        verify(produtoService).removerProdutoFavoritado(10L, 1L);
    }

    @Test
    @DisplayName("DELETE /produtos/favoritos deve retornar 204")
    void deveLimparFavoritos() throws Exception {
        setSecurityContext(1L);
        mockMvc.perform(delete("/produtos/favoritos"))
                .andExpect(status().isNoContent());
        verify(produtoService).limparFavoritos(1L);
    }

    @Test
    @DisplayName("GET /produtos/interessados deve retornar 200")
    void deveListarInteressados() throws Exception {
        setSecurityContext(1L);
        when(produtoService.listarInteressados(1L)).thenReturn(List.of());
        mockMvc.perform(get("/produtos/interessados"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /produtos/{id}/interesse deve retornar 200")
    void deveSalvarInteresse() throws Exception {
        setSecurityContext(1L);
        mockMvc.perform(post("/produtos/{id}/interesse", 10))
                .andExpect(status().isOk());
        verify(produtoService).salvarProdutoDeInteresse(10L, 1L);
    }

    @Test
    @DisplayName("DELETE /produtos/{id}/interesse deve retornar 200")
    void deveRemoverInteresse() throws Exception {
        setSecurityContext(1L);
        mockMvc.perform(delete("/produtos/{id}/interesse", 10))
                .andExpect(status().isOk());
        verify(produtoService).removerProdutoInteressado(10L, 1L);
    }

    @Test
    @DisplayName("DELETE /produtos/interesse deve retornar 204")
    void deveLimparInteresse() throws Exception {
        setSecurityContext(1L);
        mockMvc.perform(delete("/produtos/interesse"))
                .andExpect(status().isNoContent());
        verify(produtoService).limparInteresse(1L);
    }

    private void setSecurityContext(Long userId) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("user", null, List.of());
        auth.setDetails(userId);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
