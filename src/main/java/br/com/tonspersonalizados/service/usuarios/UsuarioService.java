package br.com.tonspersonalizados.service.usuarios;


import br.com.tonspersonalizados.config.GerenciadorTokenJwt;
import br.com.tonspersonalizados.dto.usuarios.*;
import br.com.tonspersonalizados.entity.usuarios.*;
import br.com.tonspersonalizados.exception.usuarios.EmailJaExisteException;
import br.com.tonspersonalizados.exception.usuarios.EnderecoNaoEncontradoException;
import br.com.tonspersonalizados.exception.usuarios.LoginInvalidoException;
import br.com.tonspersonalizados.exception.usuarios.UsuarioNaoEncontradoException;
import br.com.tonspersonalizados.repository.usuarios.EnderecoRepository;
import br.com.tonspersonalizados.repository.usuarios.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final AcessoService acessoService;
    private final EmpresaService empresaService;
    private final PasswordEncoder passwordEncoder;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final AuthenticationManager authenticationManager;


    public UsuarioService(
            AcessoService acessoService, EmpresaService empresaService,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            GerenciadorTokenJwt gerenciadorTokenJwt,
            AuthenticationManager authenticationManager, EnderecoRepository enderecoRepository) {
        this.acessoService = acessoService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.empresaService = empresaService;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.authenticationManager = authenticationManager;
        this.enderecoRepository = enderecoRepository;

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

        if (usuarioDto.getCnpj() != null) {

            Empresa empresa = empresaService.buscarPorCnpj(usuarioDto.getCnpj());
            usuario.setEmpresa(empresa);
        }

        if (buscarPorEmail(usuario.getLogin().getEmail()) != null) {
            throw new EmailJaExisteException("Email já cadastrado!");
        }

        usuarioRepository.save(usuario);
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

        // Adicionar acessos
        List<Acesso> acessos = acessoService.listarAcessosById(funcionarioDto.getAcessos());
        funcionario.setAcessos(acessos);

        usuarioRepository.save(funcionario);
    }

    public UsuarioTokenDto login(LoginRequestDto loginDto) {


        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getSenha());

        final Authentication authentication = this.authenticationManager.authenticate(credentials);


        Usuario usuario = usuarioRepository.findByLoginEmail(loginDto.getEmail())
                .orElseThrow(() -> new LoginInvalidoException("Login inválido"));


        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = gerenciadorTokenJwt.generateToken(authentication);


        UsuarioTokenDto usuarioTokenDto = new UsuarioTokenDto();

        usuarioTokenDto.setId(usuario.getId());
        usuarioTokenDto.setEmail(usuario.getLogin().getEmail());
        usuarioTokenDto.setNome(usuario.getNome());
        usuarioTokenDto.setToken(token);

        usuario.getLogin().setUltimoLogin(LocalDateTime.now());

        usuarioRepository.save(usuario);

        return usuarioTokenDto;
    }


    public List<FuncionarioResponseDto> listarFuncionarios() {

        return usuarioRepository.findAllByIsFuncionarioIsTrueAndDataDeDeletadoIsNull()
                .stream()
                .map((funcionario) -> {
                    FuncionarioResponseDto dto = new FuncionarioResponseDto();
                    dto.setId(funcionario.getId());
                    dto.setNome(funcionario.getNome());
                    dto.setTelefone(funcionario.getTelefone());
                    dto.setDataNascimento(funcionario.getDataNascimento());
                    dto.setAcessos(funcionario.getAcessos());

                    return dto;
                }).toList();
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByLoginEmail(email).orElse(null);
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
