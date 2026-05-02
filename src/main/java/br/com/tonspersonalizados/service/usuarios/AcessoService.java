package br.com.tonspersonalizados.service.usuarios;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.tonspersonalizados.entity.usuarios.Acesso;
import br.com.tonspersonalizados.repository.usuarios.AcessoRepository;

@Service
public class AcessoService {
    private final AcessoRepository acessoRepository;

    public AcessoService(AcessoRepository acessoRepository) {
        this.acessoRepository = acessoRepository;
    }

    public List<Acesso> listarAcessosById(List<Long> ids){
        return acessoRepository.findAllByIdIn(ids);
    }

    public List<Acesso> listarTodos() {
        return acessoRepository.findAll();
    }
}
