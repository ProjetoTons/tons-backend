package br.com.tonspersonalizados.service.usuarios;

import br.com.tonspersonalizados.dto.usuarios.*;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do {@link UsuarioService}.
 *
 * Filosofia deste conjunto: COBERTURA MÁXIMA COM O MÍNIMO DE TESTES.
 * Cada teste só existe se cobre uma linha ou branch que nenhum outro cobre.
 *  - Não há testes de getter/setter, DTO, entidade nem mapeamento puro.
 *  - O padrão findById().orElseThrow() (repetido em 8 métodos) é validado
 *    por UM representante de cada tipo de exceção; nos demais métodos a
 *    própria linha do orElseThrow é coberta pelo teste de sucesso.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService")
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EnderecoRepository enderecoRepository;
    @Mock private AcessoService acessoService;
    @Mock private EmpresaService empresaService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private WhatsAppService whatsAppService;
    @Mock private CloudinaryService cloudinaryService;

    @InjectMocks private UsuarioService usuarioService;

    private static final String CNPJ_TONS = "12345678000199";

    @BeforeEach
    void setUp() {
        // @Value não é resolvido sem contexto Spring; injetamos manualmente.
        ReflectionTestUtils.setField(usuarioService, "cnpjTons", CNPJ_TONS);
    }

    private Usuario usuarioComLogin(String email) {
        Usuario u = new Usuario();
        u.setNome("João");
        u.setCpf("12345678901");
        u.setTelefone("11999998888");
        Login login = new Login();
        login.setEmail(email);
        login.setUsuario(u);
        u.setLogin(login);
        return u;
    }

    // ===================================================================
    @Nested
    @DisplayName("cadastrar")
    class CadastrarTest {

        private UsuarioRequestDto dtoValido() {
            UsuarioRequestDto dto = new UsuarioRequestDto(); // tem setters -> objeto real
            dto.setNome("João");
            dto.setCpf("12345678901");
            dto.setTelefone("11999998888");
            dto.setEmail("joao@email.com");
            dto.setSenha("senha123");
            dto.setEmpresaId(10L); // cobre o branch empresaId != null no caminho feliz
            return dto;
        }

        @Test
        @DisplayName("Deve cadastrar usuário, vincular empresa e notificar quando e-mail é novo")
        void deveCadastrarComSucesso() {
            // Arrange
            when(empresaService.buscarPorId(10L)).thenReturn(new Empresa());
            when(passwordEncoder.encode("senha123")).thenReturn("hash");
            when(usuarioRepository.findByLoginEmail("joao@email.com")).thenReturn(Optional.empty());

            // Act
            usuarioService.cadastrar(dtoValido());

            // Assert
            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).save(captor.capture());
            assertFalse(captor.getValue().getFuncionario());
            assertNotNull(captor.getValue().getEmpresa());
            verify(whatsAppService).enviarTemplate(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Deve lançar EmailJaExisteException quando o e-mail já existir")
        void deveLancarQuandoEmailJaExiste() {
            // Arrange
            when(empresaService.buscarPorId(10L)).thenReturn(new Empresa());
            when(passwordEncoder.encode(anyString())).thenReturn("hash");
            when(usuarioRepository.findByLoginEmail("joao@email.com"))
                    .thenReturn(Optional.of(new Usuario()));

            // Act + Assert
            assertThrows(EmailJaExisteException.class, () -> usuarioService.cadastrar(dtoValido()));
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve traduzir violação de CPF único em ResponseStatusException 400")
        void deveTraduzirViolacaoEm400() {
            // Arrange
            when(empresaService.buscarPorId(10L)).thenReturn(new Empresa());
            when(passwordEncoder.encode(anyString())).thenReturn("hash");
            when(usuarioRepository.findByLoginEmail(anyString())).thenReturn(Optional.empty());
            when(usuarioRepository.save(any())).thenThrow(new DataIntegrityViolationException("cpf"));

            // Act + Assert
            ResponseStatusException ex =
                    assertThrows(ResponseStatusException.class, () -> usuarioService.cadastrar(dtoValido()));
            assertEquals(400, ex.getStatusCode().value());
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("cadastrarFuncionario")
    class CadastrarFuncionarioTest {

        @Test
        @DisplayName("Deve cadastrar funcionário vinculado à Tons e com os acessos informados")
        void deveCadastrarFuncionario() {
            // Arrange
            FuncionarioRequestDto dto = mock(FuncionarioRequestDto.class); // sem setters -> mock
            when(dto.getSenha()).thenReturn("senha");
            when(dto.getAcessos()).thenReturn(List.of(1L, 2L));
            Empresa tons = new Empresa();
            when(empresaService.buscarPorCnpj(CNPJ_TONS)).thenReturn(tons);
            when(acessoService.listarAcessosById(List.of(1L, 2L)))
                    .thenReturn(List.of(new Acesso(), new Acesso()));
            when(passwordEncoder.encode("senha")).thenReturn("hash");

            // Act
            usuarioService.cadastrarFuncionario(dto);

            // Assert
            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).save(captor.capture());
            assertTrue(captor.getValue().getFuncionario());
            assertSame(tons, captor.getValue().getEmpresa());
            assertEquals(2, captor.getValue().getAcessos().size());
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorIdTest {

        // Representante do padrão UsuarioNaoEncontradoException + cobre a linha do orElseThrow.
        @Test
        @DisplayName("Deve lançar UsuarioNaoEncontradoException quando o id não existir")
        void deveLancarQuandoNaoExiste() {
            // Arrange
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            // Act + Assert
            assertThrows(UsuarioNaoEncontradoException.class, () -> usuarioService.buscarPorId(99L));
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("buscarPorIdDto")
    class BuscarPorIdDtoTest {

        @Test
        @DisplayName("Deve retornar o DTO do usuário quando existir")
        void deveRetornarDto() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioComLogin("joao@email.com")));

            // Act
            UsuarioResponseDto dto = usuarioService.buscarPorIdDto(1L);

            // Assert (mínima: só confirma que o método executou e devolveu dados)
            assertNotNull(dto);
            assertEquals("joao@email.com", dto.getEmail());
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("buscarPorCpf")
    class BuscarPorCpfTest {

        @Test
        @DisplayName("Deve retornar o DTO quando o CPF existir")
        void deveRetornarDto() {
            // Arrange
            when(usuarioRepository.findByCpfAndIsFuncionarioIsFalseAndDataDeDeletadoIsNull("123"))
                    .thenReturn(Optional.of(usuarioComLogin("joao@email.com")));

            // Act
            UsuarioResponseDto dto = usuarioService.buscarPorCpf("123");

            // Assert
            assertNotNull(dto);
            assertEquals("12345678901", dto.getCpf());
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("listarFuncionarios")
    class ListarFuncionariosTest {

        // Um único teste com login != null cobre TODAS as linhas do map (inclusive o setEmail).
        @Test
        @DisplayName("Deve mapear funcionários ativos incluindo o e-mail")
        void deveMapearFuncionarios() {
            // Arrange
            Usuario func = usuarioComLogin("func@tons.com");
            func.setNome("Funcionário");
            when(usuarioRepository.findAllByIsFuncionarioIsTrueAndDataDeDeletadoIsNull())
                    .thenReturn(List.of(func));

            // Act
            List<FuncionarioResponseDto> resultado = usuarioService.listarFuncionarios();

            // Assert
            assertEquals(1, resultado.size());
            assertEquals("func@tons.com", resultado.get(0).getEmail());
            assertTrue(resultado.get(0).getAtivo());
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("atualizar (por id)")
    class AtualizarPorIdTest {

        // Teste A: idEmpresa != null + endereço NOVO (usuário sem endereço).
        @Test
        @DisplayName("Deve vincular empresa e criar endereço novo quando informados")
        void deveVincularEmpresaECriarEndereco() {
            // Arrange
            Usuario existente = usuarioComLogin("a@a.com"); // sem endereço
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(empresaService.buscarPorId(5L)).thenReturn(new Empresa());

            EnderecoRequestDto endDto = mock(EnderecoRequestDto.class);
            when(endDto.getLogadouro()).thenReturn("Rua A");
            UsuarioAtualizarRequestDto dto = mock(UsuarioAtualizarRequestDto.class);
            when(dto.getNome()).thenReturn("Novo");
            when(dto.getIdEmpresa()).thenReturn(5L);
            when(dto.getEndereco()).thenReturn(endDto);

            // Act
            usuarioService.atualizar(1L, dto);

            // Assert
            assertNotNull(existente.getEmpresa());
            assertEquals("Rua A", existente.getEndereco().getLogradouro());
            assertSame(existente, existente.getEndereco().getUsuario());
            verify(usuarioRepository).save(existente);
        }

        // Teste B: idEmpresa == null com empresa existente (desvincula) + endereço EXISTENTE (atualiza).
        @Test
        @DisplayName("Deve desvincular a empresa e atualizar o endereço existente")
        void deveDesvincularEmpresaEAtualizarEndereco() {
            // Arrange
            Usuario existente = usuarioComLogin("a@a.com");
            existente.setEmpresa(new Empresa());
            Endereco enderecoAtual = new Endereco();
            existente.setEndereco(enderecoAtual);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));

            EnderecoRequestDto endDto = mock(EnderecoRequestDto.class);
            when(endDto.getLogadouro()).thenReturn("Rua Nova");
            UsuarioAtualizarRequestDto dto = mock(UsuarioAtualizarRequestDto.class);
            when(dto.getNome()).thenReturn("X");
            when(dto.getIdEmpresa()).thenReturn(null);
            when(dto.getEndereco()).thenReturn(endDto);

            // Act
            usuarioService.atualizar(1L, dto);

            // Assert
            assertNull(existente.getEmpresa());                       // desvinculado
            assertSame(enderecoAtual, existente.getEndereco());        // mesma instância
            assertEquals("Rua Nova", enderecoAtual.getLogradouro());   // atualizada
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("atualizar (por entidade)")
    class AtualizarPorEntidadeTest {

        @Test
        @DisplayName("Deve persistir o usuário recebido")
        void devePersistir() {
            // Arrange
            Usuario usuario = usuarioComLogin("a@a.com");

            // Act
            usuarioService.atualizar(usuario);

            // Assert
            verify(usuarioRepository).save(usuario);
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("atualizarFuncionario")
    class AtualizarFuncionarioTest {

        // Sucesso com troca de foto cobre o branch verdadeiro (deletar no Cloudinary) + acessos + save.
        @Test
        @DisplayName("Deve trocar a foto e apagar a antiga quando a URL mudar")
        void deveTrocarFoto() {
            // Arrange
            Usuario existente = usuarioComLogin("f@f.com");
            existente.setFotoUrl("http://antiga");
            existente.setFotoPublicId("public-antigo");
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));

            FuncionarioAtualizarRequestDto dto = mock(FuncionarioAtualizarRequestDto.class);
            when(dto.getFotoUrl()).thenReturn("http://nova");
            when(dto.getFotoPublicId()).thenReturn("public-novo");
            when(dto.getAcessos()).thenReturn(List.of(1L));
            when(acessoService.listarAcessosById(List.of(1L))).thenReturn(List.of(new Acesso()));

            // Act
            usuarioService.atualizarFuncionario(1L, dto);

            // Assert
            verify(cloudinaryService).deletar("public-antigo");
            assertEquals("http://nova", existente.getFotoUrl());
            verify(usuarioRepository).save(existente);
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("deletar")
    class DeletarTest {

        @Test
        @DisplayName("Deve aplicar soft-delete preenchendo dataDeDeletado")
        void deveAplicarSoftDelete() {
            // Arrange
            Usuario usuario = usuarioComLogin("a@a.com");
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

            // Act
            usuarioService.deletar(1L);

            // Assert
            assertNotNull(usuario.getDataDeDeletado());
            verify(usuarioRepository).save(usuario);
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("cadastrarEnderecoUsuario")
    class CadastrarEnderecoUsuarioTest {

        @Test
        @DisplayName("Deve criar o endereço, vinculá-lo ao usuário e retorná-lo")
        void deveCadastrarEndereco() {
            // Arrange
            Usuario usuario = usuarioComLogin("a@a.com");
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            EnderecoRequestDto endDto = mock(EnderecoRequestDto.class);
            when(endDto.getLogadouro()).thenReturn("Rua B");

            // Act
            Endereco resultado = usuarioService.cadastrarEnderecoUsuario(endDto, 1L);

            // Assert
            assertNotNull(resultado);
            assertEquals("Rua B", resultado.getLogradouro());
            assertSame(usuario, resultado.getUsuario());
            verify(usuarioRepository).save(usuario);
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("buscarEndereco")
    class BuscarEnderecoTest {

        @Test
        @DisplayName("Deve retornar o endereço quando existir")
        void deveRetornarEndereco() {
            // Arrange
            Endereco endereco = new Endereco();
            when(enderecoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(endereco));

            // Act
            Endereco resultado = usuarioService.buscarEndereco(1L);

            // Assert
            assertSame(endereco, resultado);
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("atualizarEndereco")
    class AtualizarEnderecoTest {

        @Test
        @DisplayName("Deve atualizar e salvar o endereço quando existir")
        void deveAtualizarEndereco() {
            // Arrange
            Endereco existente = new Endereco();
            when(enderecoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(existente));
            when(enderecoRepository.save(existente)).thenReturn(existente);
            EnderecoRequestDto endDto = mock(EnderecoRequestDto.class);
            when(endDto.getLogadouro()).thenReturn("Nova");

            // Act
            Endereco resultado = usuarioService.atualizarEndereco(endDto, 1L);

            // Assert
            assertEquals("Nova", resultado.getLogradouro());
            verify(enderecoRepository).save(existente);
        }

        // Representante do padrão EnderecoNaoEncontradoException.
        @Test
        @DisplayName("Deve lançar EnderecoNaoEncontradoException quando não existir")
        void deveLancarQuandoNaoExiste() {
            // Arrange
            EnderecoRequestDto endDto = mock(EnderecoRequestDto.class);
            when(enderecoRepository.findByUsuarioId(99L)).thenReturn(Optional.empty());

            // Act + Assert
            assertThrows(EnderecoNaoEncontradoException.class,
                    () -> usuarioService.atualizarEndereco(endDto, 99L));
        }
    }

    // ===================================================================
    @Nested
    @DisplayName("deletarEndereco")
    class DeletarEnderecoTest {

        // Com endereço cobre o branch verdadeiro (desvínculo) — que é a regra de negócio do método.
        @Test
        @DisplayName("Deve desvincular o endereço do usuário antes de salvar")
        void deveDesvincularEndereco() {
            // Arrange
            Usuario usuario = usuarioComLogin("a@a.com");
            Endereco endereco = new Endereco();
            endereco.setUsuario(usuario);
            usuario.setEndereco(endereco);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

            // Act
            usuarioService.deletarEndereco(1L);

            // Assert
            assertNull(usuario.getEndereco());
            assertNull(endereco.getUsuario());
            verify(usuarioRepository).save(usuario);
        }
    }
}
