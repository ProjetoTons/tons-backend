package br.com.tonspersonalizados.service.usuarios;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.tonspersonalizados.config.GerenciadorTokenJwt;
import br.com.tonspersonalizados.dto.notificacoes.NotificacaoDto;
import br.com.tonspersonalizados.dto.usuarios.LoginRequestDto;
import br.com.tonspersonalizados.dto.usuarios.UsuarioDetalhesDto;
import br.com.tonspersonalizados.dto.usuarios.UsuarioTokenDto;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.exception.usuarios.LoginInvalidoException;
import br.com.tonspersonalizados.exception.usuarios.UsuarioNaoEncontradoException;
import br.com.tonspersonalizados.service.notificacoes.NotificacaoService;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Value("${jwt.resetSenhaUrl}")
    private String resetUrl;

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final NotificacaoService notificacaoService;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private AuthenticationManager authenticationManager;

    public AutenticacaoService(UsuarioService usuarioService,
                               PasswordEncoder passwordEncoder,
                               NotificacaoService notificacaoService,
                               GerenciadorTokenJwt gerenciadorTokenJwt,
                               @Lazy AuthenticationManager authenticationManager) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.notificacaoService = notificacaoService;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.authenticationManager = authenticationManager;
    }


    //username = email aqui
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.buscarPorEmail(username);

        if (usuario == null) {
            throw new UsernameNotFoundException(String.format("Usuário: %s  não encontrado", username));
        }

        return new UsuarioDetalhesDto(usuario);
    }

    public UsuarioTokenDto login(LoginRequestDto loginDto) {

        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getSenha());

        final Authentication authentication = this.authenticationManager.authenticate(credentials);


        Usuario usuario = usuarioService.buscarPorEmail(loginDto.getEmail());

        if (usuario == null) {
            throw new LoginInvalidoException("Login inválido");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = gerenciadorTokenJwt.generateToken(authentication);

        UsuarioTokenDto usuarioTokenDto = new UsuarioTokenDto();

        usuarioTokenDto.setId(usuario.getId());
        usuarioTokenDto.setEmail(usuario.getLogin().getEmail());
        usuarioTokenDto.setNome(usuario.getNome());
        usuarioTokenDto.setCnpj(usuario.getEmpresa() != null ? usuario.getEmpresa().getCnpj() : null);
        usuarioTokenDto.setToken(token);

        usuario.getLogin().setUltimoLogin(LocalDateTime.now());

        usuarioService.atualizar(usuario);

        return usuarioTokenDto;
    }

    public void enviarEmailResetSenha(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException("Usuário com email informado não encontrado.");
        }

        String token = gerenciadorTokenJwt.generateResetToken(
                String.valueOf(usuario.getId()), usuario.getLogin().getEmail(), usuario.getLogin().getSenhaHash()
        );

        String linkComToken = resetUrl + token;
        String corpo = """
                Olá, %s!
                
                Você pode alterar sua senha aqui:
                %s
                
                Caso não consiga acessar o link, copie e cole no seu navegador.
                
                Se você não solicitou a troca de senha, ignore este email.
                """;

        NotificacaoDto emailDto = new NotificacaoDto();
        emailDto.setAssunto("Recuperação de senha");
        emailDto.setDestinatario(usuario.getLogin().getEmail());
        emailDto.setCorpo(corpo.formatted(usuario.getNome(), linkComToken));

        notificacaoService.enviarEmail(emailDto);
    }

    public void resetarSenha(String token, String novaSenha) {
        String userId = gerenciadorTokenJwt.getUserIdFromResetToken(token);
        Usuario usuario = usuarioService.buscarPorId(Long.parseLong(userId));

        boolean tokenValido = gerenciadorTokenJwt.validateResetToken(token, usuario.getLogin().getSenhaHash());

        if (!tokenValido) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Token inválido ou expirado.");
        }

        usuario.getLogin().setSenhaHash(passwordEncoder.encode(novaSenha));
        usuarioService.atualizar(usuario);
    }
}
