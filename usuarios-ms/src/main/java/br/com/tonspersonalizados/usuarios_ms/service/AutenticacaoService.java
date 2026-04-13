package br.com.tonspersonalizados.usuarios_ms.service;

import br.com.tonspersonalizados.usuarios_ms.dto.UsuarioDetalhesDto;
import br.com.tonspersonalizados.usuarios_ms.entity.Usuario;
import br.com.tonspersonalizados.usuarios_ms.repository.UsuarioRepository;
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


        Usuario usuario = usuarioRepository.findByLoginEmail(username);

        if (usuario == null){
            throw new UsernameNotFoundException(String.format("Usuário: %s  não encontrado",  username));
        }

        return  new UsuarioDetalhesDto(usuario);
    }

}
