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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
