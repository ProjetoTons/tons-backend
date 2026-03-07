package br.com.tonspersonalizados.usuarios_ms.service;


import br.com.tonspersonalizados.usuarios_ms.model.Usuario;
import br.com.tonspersonalizados.usuarios_ms.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // CRUD
    public Boolean cadastrar(Usuario usuario){
        if (validarCadastro(usuario) && validarEmail(usuario) && validarSenha(usuario)) {
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    public Usuario buscarPorNome(String nome) {
        Usuario nomeExiste = usuarioRepository.findByNome(nome);
        if (nomeExiste != null) {
            return nomeExiste;
        }
        return null;
    }

    public Usuario buscarPorEmail(String email) {
        Usuario emailExiste = usuarioRepository.findByEmail(email);
        if (emailExiste != null) {
            return emailExiste;
        }
        return null;
    }

    public Boolean atualizar(Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(usuario.getId()).orElse(null);
        if (usuarioExistente == null){
            return false;
        }
        if (validarCadastro(usuario) && validarEmail(usuario) && validarSenha(usuario)) {
            usuarioExistente.setNome(usuario.getNome());
            usuarioExistente.setEmail(usuario.getEmail());
            usuarioExistente.setSenha(usuario.getSenha());
            usuarioRepository.save(usuarioExistente);
        }
        return true;
    }

    public Boolean deletar(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }


    // Validações
    public Boolean validarCadastro(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getEmail() == null || usuario.getSenha() == null) {
            System.out.println("Erro: Campos obrigatórios não preenchidos.");
            return false;
        }
        return true;
    }

    public Boolean validarEmail(Usuario usuario) {
        if (usuario.getEmail().contains("@")) {
            return true;
        }
        System.out.println("Erro: Email inválido. O email deve conter '@'.");
        return false;
    }

    public Boolean validarSenha(Usuario usuario) {
        String especiais = "!@#$%^&*()_+-=[]{}|;':\",./<>?\\";

        if (usuario.getSenha().length() >= 8) {
            for (char c : especiais.toCharArray()) {
                if (usuario.getSenha().contains(String.valueOf(c))) {
                    return true;
                }
            }
        }
        System.out.println("Erro: Senha inválida. A senha deve conter pelo menos 8 caracteres e um caractere especial.");
        return false;
    }

}
