package br.com.tonspersonalizados.service;

import br.com.tonspersonalizados.entity.AcaoLog;
import br.com.tonspersonalizados.entity.LogSistema;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.repository.LogSistemaRepository;
import br.com.tonspersonalizados.service.usuarios.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogSistemaService")
class LogSistemaServiceTest {

    @Mock private UsuarioService usuarioService;
    @Mock private LogSistemaRepository logSistemaRepository;
    @InjectMocks private LogSistemaService logSistemaService;

    @Test
    @DisplayName("Deve registrar log com usuário quando idUsuario não for null")
    void deveRegistrarComUsuario() {
        Usuario usuario = new Usuario();
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

        logSistemaService.registrar(1L, AcaoLog.CRIAR, "Pedido", 10L, "Criou pedido", null, "novo");

        ArgumentCaptor<LogSistema> captor = ArgumentCaptor.forClass(LogSistema.class);
        verify(logSistemaRepository).save(captor.capture());
        LogSistema log = captor.getValue();
        assertSame(usuario, log.getUsuario());
        assertEquals(AcaoLog.CRIAR, log.getAcao());
        assertEquals("Pedido", log.getEntidade());
        assertEquals(10L, log.getEntidadeId());
        assertEquals("Criou pedido", log.getDescricao());
        assertNotNull(log.getValorNovo());
    }

    @Test
    @DisplayName("Deve registrar log sem usuário quando idUsuario for null")
    void deveRegistrarSemUsuario() {
        logSistemaService.registrar(null, AcaoLog.DELETAR, "Produto", 5L, "Removeu", "antes", null);

        ArgumentCaptor<LogSistema> captor = ArgumentCaptor.forClass(LogSistema.class);
        verify(logSistemaRepository).save(captor.capture());
        assertNull(captor.getValue().getUsuario());
        assertNotNull(captor.getValue().getValorAnterior());
        assertNull(captor.getValue().getValorNovo());
        verify(usuarioService, never()).buscarPorId(any());
    }

    @Test
    @DisplayName("Deve serializar valores null como null")
    void deveSerializarNullComoNull() {
        logSistemaService.registrar(null, AcaoLog.CRIAR, "X", 1L, "desc", null, null);

        ArgumentCaptor<LogSistema> captor = ArgumentCaptor.forClass(LogSistema.class);
        verify(logSistemaRepository).save(captor.capture());
        assertNull(captor.getValue().getValorAnterior());
        assertNull(captor.getValue().getValorNovo());
    }

    @Test
    @DisplayName("Deve buscar logs por usuário")
    void deveBuscarPorUsuario() {
        when(logSistemaRepository.findByUsuarioId(1L)).thenReturn(List.of(new LogSistema()));
        assertEquals(1, logSistemaService.buscarPorUsuario(1L).size());
    }

    @Test
    @DisplayName("Deve buscar logs por entidade")
    void deveBuscarPorEntidade() {
        when(logSistemaRepository.findByEntidade("Pedido")).thenReturn(List.of(new LogSistema()));
        assertEquals(1, logSistemaService.buscarPorEntidade("Pedido").size());
    }

    @Test
    @DisplayName("Deve buscar logs por entidade e id")
    void deveBuscarPorEntidadeEId() {
        when(logSistemaRepository.findByEntidadeAndEntidadeId("Pedido", 1L)).thenReturn(List.of(new LogSistema()));
        assertEquals(1, logSistemaService.buscarPorEntidadeEId("Pedido", 1L).size());
    }

    @Test
    @DisplayName("Deve buscar logs por ação")
    void deveBuscarPorAcao() {
        when(logSistemaRepository.findByAcao(AcaoLog.CRIAR)).thenReturn(List.of(new LogSistema()));
        assertEquals(1, logSistemaService.buscarPorAcao(AcaoLog.CRIAR).size());
    }

    @Test
    @DisplayName("Deve buscar logs por período")
    void deveBuscarPorPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();
        when(logSistemaRepository.findByDataLogBetween(inicio, fim)).thenReturn(List.of(new LogSistema()));
        assertEquals(1, logSistemaService.buscarPorPeriodo(inicio, fim).size());
    }
}
