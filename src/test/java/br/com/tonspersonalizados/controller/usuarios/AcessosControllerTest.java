package br.com.tonspersonalizados.controller.usuarios;

import br.com.tonspersonalizados.entity.usuarios.Acesso;
import br.com.tonspersonalizados.service.usuarios.AcessoService;
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
class AcessoControllerTest {

    @Mock
    private AcessoService acessoService;

    @InjectMocks
    private AcessoController controller;

    @Test
    @DisplayName("Deve listar todos os acessos com sucesso")
    void deveListarTodosOsAcessosComSucesso() {

        // Arrange
        Acesso acesso1 = new Acesso();
        Acesso acesso2 = new Acesso();

        List<Acesso> acessos = List.of(acesso1, acesso2);

        when(acessoService.listarTodos())
                .thenReturn(acessos);

        // Act
        ResponseEntity<List<Acesso>> resposta =
                controller.listarTodos();

        // Assert
        assertEquals(200, resposta.getStatusCode().value());
        assertNotNull(resposta.getBody());
        assertEquals(2, resposta.getBody().size());

        verify(acessoService).listarTodos();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver acessos")
    void deveRetornarListaVaziaQuandoNaoHouverAcessos() {

        // Arrange
        when(acessoService.listarTodos())
                .thenReturn(List.of());

        // Act
        ResponseEntity<List<Acesso>> resposta =
                controller.listarTodos();

        // Assert
        assertEquals(200, resposta.getStatusCode().value());
        assertNotNull(resposta.getBody());
        assertTrue(resposta.getBody().isEmpty());

        verify(acessoService).listarTodos();
    }
}