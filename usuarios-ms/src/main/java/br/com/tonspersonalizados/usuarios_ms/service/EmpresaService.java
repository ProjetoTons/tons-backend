package br.com.tonspersonalizados.usuarios_ms.service;

import br.com.tonspersonalizados.usuarios_ms.model.Empresa;
import br.com.tonspersonalizados.usuarios_ms.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService {


    private final EmpresaRepository repository;

    public EmpresaService(EmpresaRepository repository) {
        this.repository = repository;
    }

    public Empresa buscarPorCnpj(String cnpj){

      return   repository.findByCnpj(cnpj);

    }
}
