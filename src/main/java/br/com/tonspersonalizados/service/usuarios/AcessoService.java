package br.com.tonspersonalizados.service.usuarios;

import br.com.tonspersonalizados.entity.usuarios.Acesso;
import br.com.tonspersonalizados.repository.usuarios.AcessoRepository;
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
