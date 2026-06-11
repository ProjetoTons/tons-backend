package br.com.tonspersonalizados.service.usuarios;

import br.com.tonspersonalizados.dto.usuarios.EmpresaRequestDto;
import br.com.tonspersonalizados.dto.usuarios.EnderecoRequestDto;
import br.com.tonspersonalizados.entity.usuarios.Empresa;
import br.com.tonspersonalizados.entity.usuarios.Endereco;
import br.com.tonspersonalizados.exception.usuarios.CnpjInvalidoException;
import br.com.tonspersonalizados.exception.usuarios.EmpresaNaoEncontradoException;
import br.com.tonspersonalizados.exception.usuarios.EnderecoNaoEncontradoException;
import br.com.tonspersonalizados.repository.usuarios.EmpresaRepository;
import br.com.tonspersonalizados.repository.usuarios.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes do EmpresaService — cobertura máxima com o mínimo de testes.
 * Regras de negócio testadas: empresa não pode usar o CNPJ da Tons,
 * exceções de "não encontrado", soft-unlink de endereço e meta semanal
 * (com fallback para ZERO). DTOs sem setter são mockados.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmpresaService")
class EmpresaServiceTest {

    @Mock private EmpresaRepository empresaRepository;
    @Mock private EnderecoRepository enderecoRepository;
    @InjectMocks private EmpresaService empresaService;

    private static final String CNPJ_TONS = "12345678000199";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(empresaService, "cnpjTons", CNPJ_TONS);
    }

    @Nested
    @DisplayName("buscas")
    class BuscasTest {

        @Test
        @DisplayName("Deve retornar a empresa quando o CNPJ existir")
        void deveBuscarPorCnpj() {
            // Arrange
            Empresa empresa = new Empresa();
            when(empresaRepository.findByCnpj("111")).thenReturn(empresa);
            // Act
            Empresa resultado = empresaService.buscarPorCnpj("111");
            // Assert
            assertSame(empresa, resultado);
        }

        @Test
        @DisplayName("Deve lançar EmpresaNaoEncontradoException quando o CNPJ não existir")
        void deveLancarQuandoCnpjNaoExiste() {
            // Arrange
            when(empresaRepository.findByCnpj("000")).thenReturn(null);
            // Act + Assert
            assertThrows(EmpresaNaoEncontradoException.class, () -> empresaService.buscarPorCnpj("000"));
        }

        // Representante do padrão findById().orElseThrow(EmpresaNaoEncontrado).
        @Test
        @DisplayName("Deve lançar EmpresaNaoEncontradoException quando o id não existir")
        void deveLancarQuandoIdNaoExiste() {
            // Arrange
            when(empresaRepository.findById(99L)).thenReturn(Optional.empty());
            // Act + Assert
            assertThrows(EmpresaNaoEncontradoException.class, () -> empresaService.buscarPorId(99L));
        }

        @Test
        @DisplayName("Deve listar todas as empresas")
        void deveListarTodos() {
            // Arrange
            when(empresaRepository.findAll()).thenReturn(List.of(new Empresa()));
            // Act + Assert
            assertEquals(1, empresaService.listarTodos().size());
        }
    }

    @Nested
    @DisplayName("cadastrarEmpresa")
    class CadastrarEmpresaTest {

        @Test
        @DisplayName("Deve cadastrar a empresa quando o CNPJ for diferente do CNPJ da Tons")
        void deveCadastrar() {
            // Arrange
            EmpresaRequestDto dto = mock(EmpresaRequestDto.class);
            when(dto.getCnpj()).thenReturn("99999999000100"); // != CNPJ_TONS
            Empresa salva = new Empresa();
            when(empresaRepository.save(any(Empresa.class))).thenReturn(salva);

            // Act
            Empresa resultado = empresaService.cadastrarEmpresa(dto);

            // Assert
            assertSame(salva, resultado);
            verify(empresaRepository).save(any(Empresa.class));
        }

        @Test
        @DisplayName("Deve lançar CnpjInvalidoException ao tentar cadastrar com o CNPJ da Tons")
        void deveLancarCnpjInvalido() {
            // Arrange
            EmpresaRequestDto dto = mock(EmpresaRequestDto.class);
            when(dto.getCnpj()).thenReturn(CNPJ_TONS);

            // Act + Assert
            assertThrows(CnpjInvalidoException.class, () -> empresaService.cadastrarEmpresa(dto));
            verify(empresaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("endereço da empresa")
    class EnderecoTest {

        @Test
        @DisplayName("Deve cadastrar o endereço e vinculá-lo à empresa")
        void deveCadastrarEndereco() {
            // Arrange
            Empresa empresa = new Empresa();
            when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
            EnderecoRequestDto endDto = mock(EnderecoRequestDto.class);
            when(endDto.getLogadouro()).thenReturn("Rua X");

            // Act
            Endereco resultado = empresaService.cadastrarEnderecoEmpresa(endDto, 1L);

            // Assert
            assertEquals("Rua X", resultado.getLogradouro());
            assertSame(empresa, resultado.getEmpresa());
            verify(empresaRepository).save(empresa);
        }

        @Test
        @DisplayName("Deve retornar o endereço quando existir")
        void deveBuscarEndereco() {
            // Arrange
            Endereco endereco = new Endereco();
            when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
            // Act + Assert
            assertSame(endereco, empresaService.buscarEndereco(1L));
        }

        @Test
        @DisplayName("Deve atualizar e salvar o endereço quando existir")
        void deveAtualizarEndereco() {
            // Arrange
            Endereco existente = new Endereco();
            when(enderecoRepository.findByEmpresaId(1L)).thenReturn(Optional.of(existente));
            when(enderecoRepository.save(existente)).thenReturn(existente);
            EnderecoRequestDto endDto = mock(EnderecoRequestDto.class);
            when(endDto.getLogadouro()).thenReturn("Rua Nova");

            // Act
            Endereco resultado = empresaService.atualizarEndereco(endDto, 1L);

            // Assert
            assertEquals("Rua Nova", resultado.getLogradouro());
            verify(enderecoRepository).save(existente);
        }

        // Representante do padrão EnderecoNaoEncontradoException.
        @Test
        @DisplayName("Deve lançar EnderecoNaoEncontradoException ao atualizar endereço inexistente")
        void deveLancarEnderecoNaoEncontrado() {
            // Arrange
            EnderecoRequestDto endDto = mock(EnderecoRequestDto.class);
            when(enderecoRepository.findByEmpresaId(99L)).thenReturn(Optional.empty());
            // Act + Assert
            assertThrows(EnderecoNaoEncontradoException.class,
                    () -> empresaService.atualizarEndereco(endDto, 99L));
        }

        @Test
        @DisplayName("Deve desvincular o endereço da empresa antes de salvar")
        void deveDeletarEndereco() {
            // Arrange
            Empresa empresa = new Empresa();
            empresa.setEndereco(new Endereco());
            when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

            // Act
            empresaService.deletarEndereco(1L);

            // Assert
            assertNull(empresa.getEndereco());
            verify(empresaRepository).save(empresa);
        }
    }

    @Nested
    @DisplayName("meta semanal")
    class MetaSemanalTest {

        @Test
        @DisplayName("Deve atualizar a meta semanal de uma empresa pelo id")
        void deveAtualizarMeta() {
            // Arrange
            Empresa empresa = new Empresa();
            when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
            when(empresaRepository.save(empresa)).thenReturn(empresa);

            // Act
            Empresa resultado = empresaService.atualizarMetaSemanal(1L, new BigDecimal("500"));

            // Assert
            assertEquals(new BigDecimal("500"), resultado.getMetaSemanal());
        }

        // Cobre buscarGrafica (caminho feliz) + o fallback ZERO quando o valor é nulo.
        @Test
        @DisplayName("Deve usar ZERO ao atualizar a meta da gráfica com valor nulo")
        void deveAtualizarMetaGraficaComValorNulo() {
            // Arrange
            Empresa grafica = new Empresa();
            when(empresaRepository.findByCnpj(CNPJ_TONS)).thenReturn(grafica);
            when(empresaRepository.save(grafica)).thenReturn(grafica);

            // Act
            Empresa resultado = empresaService.atualizarMetaSemanalGrafica(null);

            // Assert
            assertEquals(BigDecimal.ZERO, resultado.getMetaSemanal());
        }

        @Test
        @DisplayName("Deve lançar EmpresaNaoEncontradoException quando a gráfica não existir")
        void deveLancarQuandoGraficaNaoExiste() {
            // Arrange
            when(empresaRepository.findByCnpj(CNPJ_TONS)).thenReturn(null);
            // Act + Assert
            assertThrows(EmpresaNaoEncontradoException.class,
                    () -> empresaService.atualizarMetaSemanalGrafica(BigDecimal.TEN));
        }
    }
}
