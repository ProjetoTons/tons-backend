package br.com.tonspersonalizados.usuarios_ms.service;


import br.com.tonspersonalizados.usuarios_ms.dto.LoginRequestDto;
import br.com.tonspersonalizados.usuarios_ms.dto.UsuarioRequestDto;
import br.com.tonspersonalizados.usuarios_ms.dto.UsuarioTokenDto;
import br.com.tonspersonalizados.usuarios_ms.exception.EmailJaExisteException;
import br.com.tonspersonalizados.usuarios_ms.exception.LoginInvalidoException;
import br.com.tonspersonalizados.usuarios_ms.exception.UsuarioNaoEncontradoException;
import br.com.tonspersonalizados.usuarios_ms.entity.Usuario;
import br.com.tonspersonalizados.usuarios_ms.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.Authenticator;

@Service
public class UsuarioService {


    private PasswordEncoder passwordEncoder;
    private GerenciadorTokenJwt gerenciadorTokenJwt;
    private AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;


    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          GerenciadorTokenJwt gerenciadorTokenJwt, AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.authenticationManager = authenticationManager;
    }

    // CRUD
    public void cadastrar(Usuario usuario) {


        if (buscarPorEmail(usuario.getLogin().getEmail()) != null) {

            throw new EmailJaExisteException("Email já cadastrado!");

        }
        String senhaCriptografada = passwordEncoder.encode(usuario.getLogin().getSenhaHash());
        usuario.getLogin().setSenhaHash(senhaCriptografada);

        usuarioRepository.save(usuario);

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
        if (nomeExiste != null) {
            return nomeExiste;
        }
        return null;
    }

    public Usuario buscarPorEmail(String email) { //verificar depois
        Usuario emailExiste = usuarioRepository.findByLoginEmail(email);
        if (emailExiste != null) {
            return emailExiste;
        }
        return null;
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


        //atualizando informações de endereco:
        usuarioExistente.getEndereco().setNumero(usuarioDto.getEndereco().getNumero());
        usuarioExistente.getEndereco().setLogradouro(usuarioDto.getEndereco().getLogadouro());
        usuarioExistente.getEndereco().setCep(usuarioDto.getEndereco().getCep());
        usuarioExistente.getEndereco().setComplemento(usuarioDto.getEndereco().getComplemento());
        usuarioExistente.getEndereco().setUsuario(usuarioExistente);

        usuarioRepository.save(usuarioExistente);

    }


    public void atualizar(Usuario usuario) {

        usuarioRepository.save(usuario);

    }


    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado");

        }
        usuarioRepository.deleteById(id);
    }


}
