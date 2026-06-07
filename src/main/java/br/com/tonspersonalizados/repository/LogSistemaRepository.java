package br.com.tonspersonalizados.repository;

import br.com.tonspersonalizados.entity.AcaoLog;
import br.com.tonspersonalizados.entity.LogSistema;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogSistemaRepository extends JpaRepository<LogSistema, Long> {

    List<LogSistema> findByUsuario(Usuario usuario);

    List<LogSistema> findByUsuarioId(Long idUsuario);

    List<LogSistema> findByEntidade(String entidade);

    List<LogSistema> findByEntidadeAndEntidadeId(String entidade, Long entidadeId);

    List<LogSistema> findByAcao(AcaoLog acao);

    List<LogSistema> findByDataLogBetween(LocalDateTime inicio, LocalDateTime fim);
}
