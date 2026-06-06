package br.com.tonspersonalizados.service;

import br.com.tonspersonalizados.entity.AcaoLog;
import br.com.tonspersonalizados.entity.LogSistema;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.repository.LogSistemaRepository;
import br.com.tonspersonalizados.service.usuarios.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogSistemaService {

    private final ObjectMapper objectMapper;
    private final UsuarioService usuarioService;
    private final LogSistemaRepository logSistemaRepository;

    public LogSistemaService(
            UsuarioService usuarioService,
            LogSistemaRepository logSistemaRepository) {
        this.objectMapper = new ObjectMapper();
        this.usuarioService = usuarioService;
        this.logSistemaRepository = logSistemaRepository;
    }

    public String serializar(Object obj) {
        if (obj == null)
            return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public void registrar(Long idUsuario, AcaoLog acao, String entidade,
            Long entidadeId, String descricao,
            String valorAnterior, String valorNovo) {

        Usuario usuario = null;
        if (idUsuario != null) {
            usuario = usuarioService.buscarPorId(idUsuario);
        }

        LogSistema log = new LogSistema();
        log.setUsuario(usuario);
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setDescricao(descricao);
        log.setValorAnterior(valorAnterior);
        log.setValorNovo(valorNovo);

        logSistemaRepository.save(log);
    }

    public List<LogSistema> buscarPorUsuario(Long idUsuario) {
        return logSistemaRepository.findByUsuarioId(idUsuario);
    }

    public List<LogSistema> buscarPorEntidade(String entidade) {
        return logSistemaRepository.findByEntidade(entidade);
    }

    public List<LogSistema> buscarPorEntidadeEId(String entidade, Long entidadeId) {
        return logSistemaRepository.findByEntidadeAndEntidadeId(entidade, entidadeId);
    }

    public List<LogSistema> buscarPorAcao(AcaoLog acao) {
        return logSistemaRepository.findByAcao(acao);
    }

    public List<LogSistema> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return logSistemaRepository.findByDataLogBetween(inicio, fim);
    }
}