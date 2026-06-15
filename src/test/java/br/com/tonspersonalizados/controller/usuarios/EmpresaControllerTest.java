package br.com.tonspersonalizados.controller.usuarios;

import br.com.tonspersonalizados.dto.usuarios.EmpresaRequestDto;
import br.com.tonspersonalizados.dto.usuarios.EnderecoRequestDto;
import br.com.tonspersonalizados.entity.usuarios.Empresa;
import br.com.tonspersonalizados.entity.usuarios.Endereco;
import br.com.tonspersonalizados.service.usuarios.EmpresaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaControllerTest {

    @Mock
    private EmpresaService empresaService;

    @InjectMocks
    private EmpresaController controller;

    private Empresa criarEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setId(1L);
        empresa.setCnpj("12345678000199");
        empresa.setMetaSemanal(new BigDecimal("1500.00"));
        return empresa;
    }

    private Endereco criarEndereco() {
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setLogradouro("Rua Teste");
        endereco.setNumero("123");
        endereco.setCep("08000000");
        endereco.setBairro("Centro");
        endereco.setCidade("Suzano");
        endereco.setEstado("SP");
        return endereco;
    }

    @Nested
    @DisplayName("cadastrarEmpresa")
    class CadastrarEmpresaTest {

        @Test
        @DisplayName("Deve cadastrar empresa e retornar 201")
        void deveCadastrarEmpresaERetornar201() {
            EmpresaRequestDto dto = new EmpresaRequestDto();
            Empresa empresa = criarEmpresa();

            when(empresaService.cadastrarEmpresa(dto)).thenReturn(empresa);

            ResponseEntity<Empresa> resposta = controller.cadastrarEmpresa(dto);

            assertEquals(201, resposta.getStatusCode().value());
            assertEquals(empresa, resposta.getBody());
            verify(empresaService).cadastrarEmpresa(dto);
        }
    }

    @Nested
    @DisplayName("listarTodos")
    class ListarTodosTest {

        @Test
        @DisplayName("Deve listar empresas e retornar 200")
        void deveListarEmpresasERetornar200() {
            List<Empresa> empresas = List.of(criarEmpresa());

            when(empresaService.listarTodos()).thenReturn(empresas);

            ResponseEntity<List<Empresa>> resposta = controller.listarTodos();

            assertEquals(200, resposta.getStatusCode().value());
            assertNotNull(resposta.getBody());
            assertEquals(1, resposta.getBody().size());
            verify(empresaService).listarTodos();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver empresas")
        void deveRetornarListaVaziaQuandoNaoHouverEmpresas() {
            when(empresaService.listarTodos()).thenReturn(List.of());

            ResponseEntity<List<Empresa>> resposta = controller.listarTodos();

            assertEquals(200, resposta.getStatusCode().value());
            assertNotNull(resposta.getBody());
            assertTrue(resposta.getBody().isEmpty());
            verify(empresaService).listarTodos();
        }
    }

    @Nested
    @DisplayName("buscarPorCnpj")
    class BuscarPorCnpjTest {

        @Test
        @DisplayName("Deve buscar empresa por CNPJ e retornar 200")
        void deveBuscarEmpresaPorCnpjERetornar200() {
            Empresa empresa = criarEmpresa();

            when(empresaService.buscarPorCnpj("12345678000199")).thenReturn(empresa);

            ResponseEntity<Empresa> resposta =
                    controller.buscarPorCnpj("12345678000199");

            assertEquals(200, resposta.getStatusCode().value());
            assertEquals(empresa, resposta.getBody());
            verify(empresaService).buscarPorCnpj("12345678000199");
        }
    }

    @Nested
    @DisplayName("endereço da empresa")
    class EnderecoEmpresaTest {

        @Test
        @DisplayName("Deve cadastrar endereço da empresa e retornar 201")
        void deveCadastrarEnderecoEmpresaERetornar201() {
            EnderecoRequestDto dto = new EnderecoRequestDto();
            Endereco endereco = criarEndereco();

            when(empresaService.cadastrarEnderecoEmpresa(dto, 1L))
                    .thenReturn(endereco);

            ResponseEntity<Endereco> resposta =
                    controller.cadastrarEnderecoEmpresa(dto, 1L);

            assertEquals(201, resposta.getStatusCode().value());
            assertEquals(endereco, resposta.getBody());
            verify(empresaService).cadastrarEnderecoEmpresa(dto, 1L);
        }

        @Test
        @DisplayName("Deve buscar endereço da empresa e retornar 200")
        void deveBuscarEnderecoEmpresaERetornar200() {
            Endereco endereco = criarEndereco();

            when(empresaService.buscarEndereco(1L)).thenReturn(endereco);

            ResponseEntity<Endereco> resposta =
                    controller.buscarEndereco(1L);

            assertEquals(200, resposta.getStatusCode().value());
            assertEquals(endereco, resposta.getBody());
            verify(empresaService).buscarEndereco(1L);
        }

        @Test
        @DisplayName("Deve atualizar endereço da empresa e retornar 200")
        void deveAtualizarEnderecoEmpresaERetornar200() {
            EnderecoRequestDto dto = new EnderecoRequestDto();
            Endereco endereco = criarEndereco();

            when(empresaService.atualizarEndereco(dto, 1L))
                    .thenReturn(endereco);

            ResponseEntity<Endereco> resposta =
                    controller.atualizarEndereco(dto, 1L);

            assertEquals(200, resposta.getStatusCode().value());
            assertEquals(endereco, resposta.getBody());
            verify(empresaService).atualizarEndereco(dto, 1L);
        }

        @Test
        @DisplayName("Deve deletar endereço da empresa e retornar 204")
        void deveDeletarEnderecoEmpresaERetornar204() {
            doNothing().when(empresaService).deletarEndereco(1L);

            ResponseEntity<Void> resposta =
                    controller.deletarEndereco(1L);

            assertEquals(204, resposta.getStatusCode().value());
            assertNull(resposta.getBody());
            verify(empresaService).deletarEndereco(1L);
        }
    }

    @Nested
    @DisplayName("meta semanal")
    class MetaSemanalTest {

        @Test
        @DisplayName("Deve atualizar meta semanal e retornar 200")
        void deveAtualizarMetaSemanalERetornar200() {
            Empresa empresa = criarEmpresa();
            empresa.setMetaSemanal(new BigDecimal("2000.00"));

            Map<String, BigDecimal> body =
                    Map.of("metaSemanal", new BigDecimal("2000.00"));

            when(empresaService.atualizarMetaSemanalGrafica(new BigDecimal("2000.00")))
                    .thenReturn(empresa);

            ResponseEntity<Map<String, BigDecimal>> resposta =
                    controller.atualizarMetaSemanal(body);

            assertEquals(200, resposta.getStatusCode().value());
            assertNotNull(resposta.getBody());
            assertEquals(new BigDecimal("2000.00"), resposta.getBody().get("metaSemanal"));

            verify(empresaService)
                    .atualizarMetaSemanalGrafica(new BigDecimal("2000.00"));
        }

        @Test
        @DisplayName("Deve buscar meta semanal e retornar 200")
        void deveBuscarMetaSemanalERetornar200() {
            Empresa empresa = criarEmpresa();

            when(empresaService.buscarGrafica()).thenReturn(empresa);

            ResponseEntity<Map<String, BigDecimal>> resposta =
                    controller.buscarMetaSemanal();

            assertEquals(200, resposta.getStatusCode().value());
            assertNotNull(resposta.getBody());
            assertEquals(new BigDecimal("1500.00"), resposta.getBody().get("metaSemanal"));

            verify(empresaService).buscarGrafica();
        }
    }
}