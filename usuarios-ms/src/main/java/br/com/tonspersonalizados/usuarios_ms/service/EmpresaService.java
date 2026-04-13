package br.com.tonspersonalizados.usuarios_ms.service;

import br.com.tonspersonalizados.usuarios_ms.dto.EmpresaRequestDto;
import br.com.tonspersonalizados.usuarios_ms.entity.Empresa;
import br.com.tonspersonalizados.usuarios_ms.exception.EmpresaNaoEncontradoException;
import br.com.tonspersonalizados.usuarios_ms.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmpresaService {


    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public Empresa buscarPorCnpj(String cnpj) {
        Empresa empresa = empresaRepository.findByCnpj(cnpj);

        if (empresa == null) {
            throw new EmpresaNaoEncontradoException("Empresa não encontrada.");
        }

        return empresa;

    }

    public List<Empresa> listarTodos(){
        return empresaRepository.findAll();
    }

    public Empresa cadastrarEmpresa(EmpresaRequestDto dto) {

        Empresa empresa = new Empresa();
        empresa.setCnpj(dto.getCnpj());
        empresa.setEmail(dto.getEmail());
        empresa.setTelefone(dto.getTelefone());
        empresa.setNomeFantasia(dto.getNomeFantasia());
        empresa.setRazaoSocial(dto.getRazaoSocial());


      return   empresaRepository.save(empresa);
    }
}
