package br.com.tonspersonalizados.usuarios_ms.service;


import br.com.tonspersonalizados.usuarios_ms.config.GerenciadorTokenJwt;
import br.com.tonspersonalizados.usuarios_ms.dto.FuncionarioRequestDto;
import br.com.tonspersonalizados.usuarios_ms.dto.LoginRequestDto;
import br.com.tonspersonalizados.usuarios_ms.dto.UsuarioRequestDto;
import br.com.tonspersonalizados.usuarios_ms.dto.UsuarioTokenDto;
import br.com.tonspersonalizados.usuarios_ms.entity.Acesso;
import br.com.tonspersonalizados.usuarios_ms.entity.Empresa;
import br.com.tonspersonalizados.usuarios_ms.entity.Login;
import br.com.tonspersonalizados.usuarios_ms.exception.EmailJaExisteException;
import br.com.tonspersonalizados.usuarios_ms.exception.LoginInvalidoException;
import br.com.tonspersonalizados.usuarios_ms.exception.UsuarioNaoEncontradoException;
import br.com.tonspersonalizados.usuarios_ms.entity.Usuario;
import br.com.tonspersonalizados.usuarios_ms.repository.UsuarioRepository;
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


    private final AcessoService acessoService;
    private final EmpresaService empresaService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final AuthenticationManager authenticationManager;


    public UsuarioService(
            AcessoService acessoService, EmpresaService empresaService,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            GerenciadorTokenJwt gerenciadorTokenJwt,
            AuthenticationManager authenticationManager) {
        this.acessoService = acessoService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.empresaService = empresaService;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.authenticationManager = authenticationManager;
    }

    // CRUD
    public void cadastrar(UsuarioRequestDto dto) {

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setCpf(dto.getCpf());
        usuario.setTelefone(dto.getTelefone());

        Login login = new Login();
        login.setEmail(dto.getEmail());
        login.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        login.setUsuario(usuario);
        usuario.setLogin(login);

        // cadastro de endereço é feito posteriormente

        if (dto.getCnpj() != null) {

            Empresa empresa = empresaService.buscarPorCnpj(dto.getCnpj());
            usuario.setEmpresa(empresa);
        }

        if (buscarPorEmail(usuario.getLogin().getEmail()) != null) {
            throw new EmailJaExisteException("Email já cadastrado!");
        }

        usuarioRepository.save(usuario);
    }

    public void cadastrarFuncionario(FuncionarioRequestDto dto){
        Usuario funcionario = new Usuario();
        funcionario.setNome(dto.getNome());
        funcionario.setTelefone(dto.getTelefone());

        Login login = new Login();
        login.setEmail(dto.getEmail());
        login.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        login.setUsuario(funcionario);
        funcionario.setLogin(login);

        // Adicionar acessos
        List<Acesso> acessos = acessoService.listarAcessosById(dto.getAcessos());
        funcionario.setAcessos(acessos);

        usuarioRepository.save(funcionario);
    }

    public UsuarioTokenDto login(LoginRequestDto loginDto) {


        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getSenha());

        final Authentication authentication = this.authenticationManager.authenticate(credentials);


        Usuario usuario = usuarioRepository.findByLoginEmail(loginDto.getEmail());

        if (usuario == null) {
            throw new LoginInvalidoException("Login inválido");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = gerenciadorTokenJwt.generateToken(authentication);

        UsuarioTokenDto usuarioTokenDto = new UsuarioTokenDto();

        usuarioTokenDto.setId(usuario.getId());
        usuarioTokenDto.setEmail(usuario.getLogin().getEmail());
        usuarioTokenDto.setNome(usuario.getNome());
        usuarioTokenDto.setToken(token);

        return usuarioTokenDto;
    }


    public Usuario buscarPorNome(String nome) { //verificar depois
        Usuario nomeExiste = usuarioRepository.findByNome(nome);

        if (nomeExiste == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado");
        }

        return nomeExiste;
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByLoginEmail(email);
    }

    public void atualizar(Long id, UsuarioRequestDto usuarioDto) {
        Usuario usuarioExistente = usuarioRepository.findById(id).orElse(null);

        if (usuarioExistente == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado");
        }

        //atualizando informações de usuario
        usuarioExistente.setNome(usuarioDto.getNome());
        // usuarioExistente.setCpf(usuarioDto.getCpf());
        usuarioExistente.setTelefone(usuarioDto.getTelefone());

        //atualizando informações de login:
        usuarioExistente.getLogin().setEmail(usuarioDto.getEmail());
        usuarioExistente.getLogin().setSenhaHash(usuarioDto.getSenha());

        usuarioRepository.save(usuarioExistente);
    }


    public void atualizar(Usuario usuario) {

        usuarioRepository.save(usuario);
    }


    public void deletar(Long id) {
        // Soft-delete
        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado");
        }

        usuario.setDataDeDeletado(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }


}
