package br.com.tonspersonalizados.controller.produto;

import br.com.tonspersonalizados.entity.produtos.CategoriaProduto;
import br.com.tonspersonalizados.service.produto.CategoriaProdutoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaProdutoService categoriaProdutoService;

    @InjectMocks
    private CategoriaController controller;

    @Test
    @DisplayName("Deve listar categorias com sucesso")
    void deveListarCategoriasComSucesso() {

        // Arrange
        CategoriaProduto categoria1 = new CategoriaProduto();
        CategoriaProduto categoria2 = new CategoriaProduto();

        List<CategoriaProduto> categorias =
                List.of(categoria1, categoria2);

        when(categoriaProdutoService.listarTodos())
                .thenReturn(categorias);

        // Act
        ResponseEntity<List<CategoriaProduto>> resposta =
                controller.listarCategorias();

        // Assert
        assertEquals(200, resposta.getStatusCode().value());
        assertNotNull(resposta.getBody());
        assertEquals(2, resposta.getBody().size());

        verify(categoriaProdutoService).listarTodos();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver categorias")
    void deveRetornarListaVaziaQuandoNaoHouverCategorias() {

        // Arrange
        when(categoriaProdutoService.listarTodos())
                .thenReturn(List.of());

        // Act
        ResponseEntity<List<CategoriaProduto>> resposta =
                controller.listarCategorias();

        // Assert
        assertEquals(200, resposta.getStatusCode().value());
        assertNotNull(resposta.getBody());
        assertTrue(resposta.getBody().isEmpty());

        verify(categoriaProdutoService).listarTodos();
    }
}