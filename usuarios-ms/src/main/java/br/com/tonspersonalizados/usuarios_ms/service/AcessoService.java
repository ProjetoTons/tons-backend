package br.com.tonspersonalizados.usuarios_ms.service;

import br.com.tonspersonalizados.usuarios_ms.entity.Acesso;
import br.com.tonspersonalizados.usuarios_ms.repository.AcessoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcessoService {
    private final AcessoRepository acessoRepository;

    public AcessoService(AcessoRepository acessoRepository) {
        this.acessoRepository = acessoRepository;
    }

    public List<Acesso> listarAcessosById(List<Long> ids){
        return acessoRepository.findAllByIdIn(ids);
    }
}
