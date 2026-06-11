package br.com.tonspersonalizados.service.usuarios;

import br.com.tonspersonalizados.entity.usuarios.Acesso;
import br.com.tonspersonalizados.repository.usuarios.AcessoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AcessoService é um simples "pass-through" para o repositório:
 * não há regra de negócio, branch nem exceção. Por isso bastam 2 testes,
 * um por método, garantindo apenas a delegação correta.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AcessoService")
class AcessoServiceTest {

    @Mock private AcessoRepository acessoRepository;
    @InjectMocks private AcessoService acessoService;

    @Test
    @DisplayName("Deve delegar listarAcessosById ao repositório repassando os ids")
    void deveDelegarListarPorId() {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        List<Acesso> esperado = List.of(new Acesso());
        when(acessoRepository.findAllByIdIn(ids)).thenReturn(esperado);

        // Act
        List<Acesso> resultado = acessoService.listarAcessosById(ids);

        // Assert
        assertSame(esperado, resultado);
        verify(acessoRepository).findAllByIdIn(ids);
    }

    @Test
    @DisplayName("Deve delegar listarTodos ao repositório")
    void deveDelegarListarTodos() {
        // Arrange
        List<Acesso> esperado = List.of(new Acesso(), new Acesso());
        when(acessoRepository.findAll()).thenReturn(esperado);

        // Act
        List<Acesso> resultado = acessoService.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
        verify(acessoRepository).findAll();
    }
}
