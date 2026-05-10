package br.com.tonspersonalizados.service.usuarios;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.tonspersonalizados.dto.usuarios.EnderecoRequestDto;
import br.com.tonspersonalizados.dto.usuarios.FuncionarioRequestDto;
import br.com.tonspersonalizados.dto.usuarios.FuncionarioResponseDto;
import br.com.tonspersonalizados.dto.usuarios.UsuarioRequestDto;
import br.com.tonspersonalizados.dto.usuarios.UsuarioResponseDto;
import br.com.tonspersonalizados.entity.usuarios.Acesso;
import br.com.tonspersonalizados.entity.usuarios.Empresa;
import br.com.tonspersonalizados.entity.usuarios.Endereco;
import br.com.tonspersonalizados.entity.usuarios.Login;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.exception.usuarios.EmailJaExisteException;
import br.com.tonspersonalizados.exception.usuarios.EnderecoNaoEncontradoException;
import br.com.tonspersonalizados.exception.usuarios.UsuarioNaoEncontradoException;
import br.com.tonspersonalizados.repository.usuarios.EnderecoRepository;
import br.com.tonspersonalizados.repository.usuarios.UsuarioRepository;
import br.com.tonspersonalizados.service.notificacoes.WhatsAppService;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final AcessoService acessoService;
    private final EmpresaService empresaService;
    private final PasswordEncoder passwordEncoder;
    private final WhatsAppService whatsAppService;

    @Value("${tons.cnpj}")
    private String cnpjTons;


    public UsuarioService(
            AcessoService acessoService, EmpresaService empresaService,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            EnderecoRepository enderecoRepository,
            WhatsAppService whatsAppService) {
        this.acessoService = acessoService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.empresaService = empresaService;
        this.enderecoRepository = enderecoRepository;
        this.whatsAppService = whatsAppService;

    }

    public void cadastrar(UsuarioRequestDto usuarioDto) {

        Usuario usuario = new Usuario();
        usuario.setFuncionario(false);
        usuario.setNome(usuarioDto.getNome());
        usuario.setCpf(usuarioDto.getCpf());
        usuario.setTelefone(usuarioDto.getTelefone());

        Login login = new Login();
        login.setEmail(usuarioDto.getEmail());
        login.setSenhaHash(passwordEncoder.encode(usuarioDto.getSenha()));
        login.setUsuario(usuario);
        usuario.setLogin(login);

        // cadastro de endereço é feito posteriormente(endpoints abaixo)

        if (usuarioDto.getEmpresaId() != null) {

            Empresa empresa = empresaService.buscarPorId(usuarioDto.getEmpresaId());
            usuario.setEmpresa(empresa);
        }

        if (buscarPorEmail(usuario.getLogin().getEmail()) != null) {
            throw new EmailJaExisteException("Email já cadastrado!");
        }

        try {
            usuarioRepository.save(usuario);
        }
        catch (DataIntegrityViolationException e){
            // Insert violou a chave unique para CPF
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos.");
        }

        try {
            whatsAppService.enviarTemplate("55" + usuario.getTelefone(), "confirmacao_cadastro", usuario.getNome());
        } catch (Exception e) {
            // Não impede o cadastro se o WhatsApp falhar
        }
    }

    public void cadastrarFuncionario(FuncionarioRequestDto funcionarioDto) {
        Usuario funcionario = new Usuario();
        funcionario.setFuncionario(true);
        funcionario.setNome(funcionarioDto.getNome());
        funcionario.setTelefone(funcionarioDto.getTelefone());
        funcionario.setDataNascimento(funcionarioDto.getDataNascimento());

        Login login = new Login();
        login.setEmail(funcionarioDto.getEmail());
        login.setSenhaHash(passwordEncoder.encode(funcionarioDto.getSenha()));
        login.setUsuario(funcionario);
        funcionario.setLogin(login);

        //assim sempre o funcionario estará vinculado a tons.
        Empresa empresa = empresaService.buscarPorCnpj(cnpjTons);
        funcionario.setEmpresa(empresa);

        // Adicionar acessos
        List<Acesso> acessos = acessoService.listarAcessosById(funcionarioDto.getAcessos());
        funcionario.setAcessos(acessos);

        usuarioRepository.save(funcionario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository
                .findByLoginEmail(email)
                .orElse(null);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
    }

    public UsuarioResponseDto buscarPorCpf(String cpf) {
        Usuario usuario = usuarioRepository
                .findByCpfAndIsFuncionarioIsFalseAndDataDeDeletadoIsNull(cpf)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setNome(usuario.getNome());
        dto.setTelefone(usuario.getTelefone());
        if (usuario.getLogin() != null) {
            dto.setEmail(usuario.getLogin().getEmail());
        }
        return dto;
    }

    public List<FuncionarioResponseDto> listarFuncionarios() {

        return usuarioRepository.findAllByIsFuncionarioIsTrueAndDataDeDeletadoIsNull()
                .stream()
                .map((funcionario) -> {
                    FuncionarioResponseDto dto = new FuncionarioResponseDto();
                    dto.setId(funcionario.getId());
                    dto.setNome(funcionario.getNome());
                    if (funcionario.getLogin() != null) {
                        dto.setEmail(funcionario.getLogin().getEmail());
                    }
                    dto.setTelefone(funcionario.getTelefone());
                    dto.setDataNascimento(funcionario.getDataNascimento());
                    dto.setAcessos(funcionario.getAcessos());
                    dto.setAtivo(funcionario.getDataDeDeletado() == null);
                    dto.setDataCriacao(funcionario.getDataDeCadastro());

                    return dto;
                }).toList();
    }

    public void atualizar(Long id, UsuarioRequestDto usuarioDto) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        usuarioExistente.setNome(usuarioDto.getNome());
        usuarioExistente.setTelefone(usuarioDto.getTelefone());

        usuarioExistente.getLogin().setEmail(usuarioDto.getEmail());
        usuarioExistente.getLogin().setSenhaHash(usuarioDto.getSenha());

        usuarioRepository.save(usuarioExistente);
    }

    public void atualizar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public void atualizarFuncionario(Long id, FuncionarioRequestDto funcionarioDto) {
        Usuario funcionarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Funcionário não encontrado"));

        funcionarioExistente.setNome(funcionarioDto.getNome());
        funcionarioExistente.setTelefone(funcionarioDto.getTelefone());
        funcionarioExistente.setDataNascimento(funcionarioDto.getDataNascimento());

        List<Acesso> acessos = acessoService.listarAcessosById(funcionarioDto.getAcessos());

        funcionarioExistente.setAcessos(acessos);

        usuarioRepository.save(funcionarioExistente);
    }

    public void deletar(Long id) {
        // Soft-delete
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        usuario.setDataDeDeletado(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }


    public Endereco cadastrarEnderecoUsuario(EnderecoRequestDto enderecoDto, Long idUsuario) {


        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        Endereco endereco = new Endereco();
        endereco.setUsuario(usuario);
        endereco.setLogradouro(enderecoDto.getLogadouro());
        endereco.setNumero(enderecoDto.getNumero());
        endereco.setCep(enderecoDto.getCep());
        endereco.setComplemento(endereco.getComplemento());

        usuario.setEndereco(endereco);

        usuarioRepository.save(usuario);

        return endereco;
    }

    public Endereco buscarEndereco(Long idUsuario) {

        return enderecoRepository.findByUsuarioId(idUsuario)
                .orElse(null);

    }

    public Endereco atualizarEndereco(EnderecoRequestDto enderecoDto, Long id) {

        Endereco enderecoExistente = enderecoRepository.findByUsuarioId(id)
                .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereço não encontrado"));

        enderecoExistente.setLogradouro(enderecoDto.getLogadouro());
        enderecoExistente.setNumero(enderecoDto.getNumero());
        enderecoExistente.setCep(enderecoDto.getCep());
        enderecoExistente.setComplemento(enderecoDto.getComplemento());

        return enderecoRepository.save(enderecoExistente);

    }

    public void deletarEndereco(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNaoEncontradoException::new);

        if (usuario.getEndereco() != null) {

            // Desvinculando endereço do usuário para que o JPA possa deletar esse endereço
            usuario.getEndereco().setUsuario(null);
            usuario.setEndereco(null);
        }


        usuarioRepository.save(usuario);
    }
}
