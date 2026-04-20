package br.com.tonspersonalizados.usuarios_ms.service;

import br.com.tonspersonalizados.usuarios_ms.dto.EmpresaRequestDto;
import br.com.tonspersonalizados.usuarios_ms.dto.EnderecoRequestDto;
import br.com.tonspersonalizados.usuarios_ms.entity.Empresa;
import br.com.tonspersonalizados.usuarios_ms.entity.Endereco;
import br.com.tonspersonalizados.usuarios_ms.entity.Usuario;
import br.com.tonspersonalizados.usuarios_ms.exception.EmpresaNaoEncontradoException;
import br.com.tonspersonalizados.usuarios_ms.exception.EnderecoNaoEncontradoException;
import br.com.tonspersonalizados.usuarios_ms.exception.UsuarioNaoEncontradoException;
import br.com.tonspersonalizados.usuarios_ms.repository.EmpresaRepository;
import br.com.tonspersonalizados.usuarios_ms.repository.EnderecoRepository;
import br.com.tonspersonalizados.usuarios_ms.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {


    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;


    public EmpresaService(EmpresaRepository empresaRepository, UsuarioRepository usuarioRepository, EnderecoRepository enderecoRepository) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
    }

    public Empresa buscarPorCnpj(String cnpj) {
        Empresa empresa = empresaRepository.findByCnpj(cnpj);

        if (empresa == null) {
            throw new EmpresaNaoEncontradoException("Empresa não encontrada.");
        }

        return empresa;

    }

    public List<Empresa> listarTodos() {
        return empresaRepository.findAll();
    }

    public Empresa cadastrarEmpresa(EmpresaRequestDto empresaDto) {

        Empresa empresa = new Empresa();
        empresa.setCnpj(empresaDto.getCnpj());
        empresa.setEmail(empresaDto.getEmail());
        empresa.setTelefone(empresaDto.getTelefone());
        empresa.setNomeFantasia(empresaDto.getNomeFantasia());
        empresa.setRazaoSocial(empresaDto.getRazaoSocial());


        return empresaRepository.save(empresa);
    }


    public Endereco cadastrarEnderecoEmpresa(EnderecoRequestDto enderecoDto, Long idEmpresa) {


      Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Empresa não encontrado"));

        Endereco endereco = new Endereco();
        endereco.setEmpresa(empresa);
        endereco.setLogradouro(enderecoDto.getLogadouro());
        endereco.setNumero(enderecoDto.getNumero());
        endereco.setCep(enderecoDto.getCep());
        endereco.setComplemento(endereco.getComplemento());

        empresa.setEndereco(endereco);

        empresaRepository.save(empresa);

        return endereco;
    }



    public Endereco buscarEndereco(Long idEmpresa) {

        return enderecoRepository.findById(idEmpresa)

                .orElse(null);

    }

    public Endereco atualizarEndereco(EnderecoRequestDto enderecoDto, Long id) {

        Endereco enderecoExistente = enderecoRepository.findByEmpresaId(id)
                .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereço não encontrado"));

        enderecoExistente.setLogradouro(enderecoDto.getLogadouro());
        enderecoExistente.setNumero(enderecoDto.getNumero());
        enderecoExistente.setCep(enderecoDto.getCep());
        enderecoExistente.setComplemento(enderecoDto.getComplemento());

        return enderecoRepository.save(enderecoExistente);

    }

    public void deletarEndereco(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(UsuarioNaoEncontradoException::new);

        if (empresa.getEndereco() != null) {

            // Desvinculando endereço da empresa para que o JPA possa deletar esse endereço
            empresa.getEndereco().setUsuario(null);
            empresa.setEndereco(null);
        }


        empresaRepository.save(empresa);
    }

}
