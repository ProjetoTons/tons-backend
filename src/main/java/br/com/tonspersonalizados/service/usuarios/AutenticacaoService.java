package br.com.tonspersonalizados.service.usuarios;

import br.com.tonspersonalizados.dto.usuarios.UsuarioDetalhesDto;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.repository.usuarios.UsuarioRepository;
import io.swagger.v3.oas.models.media.UUIDSchema;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticacaoService implements UserDetailsService {


    private UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    //username = email aqui
    @Override
    public UserDetails loadUserByUsername(String username) throws
            UsernameNotFoundException {


        Usuario usuario = usuarioRepository.findByLoginEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Usuário: %s  não encontrado",  username)));

        return  new UsuarioDetalhesDto(usuario);
    }

}
