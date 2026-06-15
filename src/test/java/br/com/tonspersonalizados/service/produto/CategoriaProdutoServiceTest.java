package br.com.tonspersonalizados.service.produto;

import br.com.tonspersonalizados.entity.produtos.CategoriaProduto;
import br.com.tonspersonalizados.repository.produto.CategoriaProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaProdutoService")
class CategoriaProdutoServiceTest {

    @Mock private CategoriaProdutoRepository categoriaRepository;
    @InjectMocks private CategoriaProdutoService categoriaProdutoService;

    @Test
    @DisplayName("Deve listar todas as categorias")
    void deveListarTodos() {
        when(categoriaRepository.findAll()).thenReturn(List.of(new CategoriaProduto(), new CategoriaProduto()));
        List<CategoriaProduto> resultado = categoriaProdutoService.listarTodos();
        assertEquals(2, resultado.size());
        verify(categoriaRepository).findAll();
    }
}
