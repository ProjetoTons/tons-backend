# 📦 Guia Completo — Integração Olist (Tiny ERP) API V2 com Spring Boot

---

## Visão Geral

Este guia documenta o passo a passo para integrar o **Olist (antigo Tiny ERP)** no backend da Tons Personalizados usando a **API V2**.

### Por que integrar com o Olist?

- **Nota Fiscal** — O João precisa emitir NF-e para cada pedido. O Olist é o ERP onde isso é feito
- **Área do Cliente** — O site terá uma seção onde o cliente vê seus pedidos, buscados diretamente do Olist por CPF
- **Centralização** — Pedidos criados no site vão automaticamente para o ERP, sem digitação manual
- **Financeiro** — Controle de contas a receber, parcelas e formas de pagamento
- **Automação** — Webhooks permitem atualizar status no site quando o Olist mudar a situação

### O que muda no projeto?

O backend se comunica com o Olist via chamadas HTTP REST. Isso permite:
- **Cadastrar pedidos** no ERP automaticamente quando o formulário "Novo Pedido" for salvo
- **Buscar pedidos** de um cliente pelo CPF para exibir na área do cliente
- Obter detalhes completos de um pedido (itens, valores, situação)
- Consultar status de pedidos no Olist
- Gerar nota fiscal a partir de um pedido
- Receber notificações de mudanças via webhooks

---

## Conceitos da API V2

### Como funciona a autenticação

A API do Olist é **simples** — não usa OAuth nem Bearer token no header. Cada chamada envia o **token** como parâmetro no body da requisição (`application/x-www-form-urlencoded`).

```
POST https://api.tiny.com.br/api2/pedido.incluir.php

Content-Type: application/x-www-form-urlencoded

token=SEU_TOKEN&pedido={"cliente":{"nome":"João"}}&formato=json
```

### Formato das respostas

Toda resposta segue o mesmo padrão:

```json
{
  "retorno": {
    "status_processamento": 3,
    "status": "OK",
    "registros": [
      {
        "registro": {
          "id": 123456,
          "numero": 1001
        }
      }
    ]
  }
}
```

| Campo | Significado |
|-------|-------------|
| `status_processamento` | `1` = Erro, `2` = Parcial, `3` = OK |
| `status` | `"OK"` ou `"Erro"` |
| `registros` | Lista de resultados (quando sucesso) |
| `erros` | Lista de erros (quando falha) |

### Limites da API

| Tipo de limite | Valor |
|----------------|-------|
| Requisições por minuto | 30 |
| Requisições por hora | 500 |
| Requisições diárias | Depende do plano |

> ⚠️ **IMPORTANTE:** Se exceder os limites, a API retorna erro `429`. Implemente retry com delay.

---

## PASSO 1 — Obter as credenciais no Olist

### O que fazer:
1. Acesse o painel do Olist ERP: [erp.olist.com](https://erp.olist.com)
2. Vá em **Configurações** → **Integrações** → **Tokens e credenciais da API**
3. Anote as credenciais:

| Campo | Onde encontrar | Exemplo |
|-------|----------------|---------|
| **Identificador** | Tela de integrações | `12739` |
| **Token** | Tela de integrações | `13d00798e15aeb9032b8f19b90f66ba0...` |
| **Endpoint de parceiro** | Tela de integrações | `https://erp.tiny.com.br/webhook/api/v1/parceiro/127` |

### Por que:
O token é **obrigatório** em toda chamada à API. Sem ele, o Olist rejeita a requisição. O endpoint de parceiro é usado para configurar webhooks.

### O que muda:
Essas credenciais serão configuradas no `application.properties` do Spring Boot.

---

## PASSO 2 — Configurar o `application.properties`

### O que fazer:
Adicione as seguintes propriedades no arquivo `src/main/resources/application.properties`:

```properties
# ---------------------------------------------------------------------
# Olist / Tiny ERP (API V2)
# ---------------------------------------------------------------------
olist.api.url=https://api.tiny.com.br/api2
olist.api.token=SEU_TOKEN_AQUI
```

### Por que:
Externalizar as credenciais no `application.properties` permite trocar o token sem alterar código Java. Se a empresa trocar de plano ou revogar o token, basta atualizar aqui e reiniciar.

### O que muda:
O `OlistService` lê essas propriedades via `@Value` na inicialização do Spring.

---

## PASSO 3 — Criar os DTOs

### O que fazer:
Crie os DTOs para representar os dados enviados e recebidos da API do Olist.

**3.1 — DTO para incluir pedido:**

Crie `src/main/java/br/com/tonspersonalizados/dto/olist/OlistPedidoRequestDto.java`:

```java
package br.com.tonspersonalizados.dto.olist;

import java.util.List;

public class OlistPedidoRequestDto {

    private String numero_ecommerce;  // Número do pedido no nosso sistema
    private String data_pedido;        // Formato: dd/mm/yyyy
    private String data_prevista;      // Data de entrega prevista
    private String obs;                // Observações do pedido
    private OlistClienteDto cliente;
    private List<OlistItemDto> itens;

    // getters e setters

    public String getNumero_ecommerce() { return numero_ecommerce; }
    public void setNumero_ecommerce(String numero_ecommerce) { this.numero_ecommerce = numero_ecommerce; }
    public String getData_pedido() { return data_pedido; }
    public void setData_pedido(String data_pedido) { this.data_pedido = data_pedido; }
    public String getData_prevista() { return data_prevista; }
    public void setData_prevista(String data_prevista) { this.data_prevista = data_prevista; }
    public String getObs() { return obs; }
    public void setObs(String obs) { this.obs = obs; }
    public OlistClienteDto getCliente() { return cliente; }
    public void setCliente(OlistClienteDto cliente) { this.cliente = cliente; }
    public List<OlistItemDto> getItens() { return itens; }
    public void setItens(List<OlistItemDto> itens) { this.itens = itens; }
}
```

**3.2 — DTO do cliente:**

Crie `src/main/java/br/com/tonspersonalizados/dto/olist/OlistClienteDto.java`:

```java
package br.com.tonspersonalizados.dto.olist;

public class OlistClienteDto {

    private String nome;           // Nome completo ou razão social (obrigatório)
    private String tipo_pessoa;    // "F" = Física, "J" = Jurídica
    private String cpf_cnpj;       // CPF ou CNPJ (obrigatório para NF)
    private String email;
    private String fone;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String cidade;
    private String uf;

    // getters e setters

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo_pessoa() { return tipo_pessoa; }
    public void setTipo_pessoa(String tipo_pessoa) { this.tipo_pessoa = tipo_pessoa; }
    public String getCpf_cnpj() { return cpf_cnpj; }
    public void setCpf_cnpj(String cpf_cnpj) { this.cpf_cnpj = cpf_cnpj; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFone() { return fone; }
    public void setFone(String fone) { this.fone = fone; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
}
```

**3.3 — DTO de item do pedido:**

Crie `src/main/java/br/com/tonspersonalizados/dto/olist/OlistItemDto.java`:

```java
package br.com.tonspersonalizados.dto.olist;

public class OlistItemDto {

    private String descricao;       // Nome do produto (obrigatório)
    private String unidade;         // "UN", "CX", "KG" (obrigatório)
    private double quantidade;      // Quantidade (obrigatório)
    private double valor_unitario;  // Valor unitário (obrigatório)
    private String info_adicional;  // Detalhes extras (tamanho, cor, composição)

    // getters e setters

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }
    public double getValor_unitario() { return valor_unitario; }
    public void setValor_unitario(double valor_unitario) { this.valor_unitario = valor_unitario; }
    public String getInfo_adicional() { return info_adicional; }
    public void setInfo_adicional(String info_adicional) { this.info_adicional = info_adicional; }
}
```

### Por que:
Os DTOs encapsulam os dados no formato que a API do Olist espera. São usados no endpoint de **cadastro de pedido**.

### O que muda:
O controller recebe um `OlistPedidoRequestDto` do frontend e repassa ao `OlistService` que monta a chamada HTTP.

---

## PASSO 4 — Criar o OlistService

### O que fazer:
Crie `src/main/java/br/com/tonspersonalizados/service/olist/OlistService.java`:

```java
package br.com.tonspersonalizados.service.olist;

import br.com.tonspersonalizados.dto.olist.OlistPedidoRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OlistService {

    private final RestClient restClient;
    private final String token;
    private final ObjectMapper objectMapper;

    public OlistService(
            @Value("${olist.api.url}") String apiUrl,
            @Value("${olist.api.token}") String token
    ) {
        this.token = token;
        this.objectMapper = new ObjectMapper();
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    // ============================================================
    // BUSCA DE PEDIDOS (Área do Cliente)
    // ============================================================

    /**
     * Pesquisa pedidos pelo CPF/CNPJ do cliente.
     * POST https://api.tiny.com.br/api2/pedidos.pesquisa.php
     *
     * Retorna lista resumida (id, numero, data, nome, valor, situação).
     * Usado na área do cliente para listar os pedidos.
     */
    public List<Map<String, String>> pesquisarPedidosPorCpf(String cpfCnpj) {
        String body = "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                + "&cpf_cnpj=" + URLEncoder.encode(cpfCnpj, StandardCharsets.UTF_8)
                + "&formato=json";

        String resposta = restClient.post()
                .uri("/pedidos.pesquisa.php")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(String.class);

        return parsePedidos(resposta);
    }

    /**
     * Obtém os dados completos de um pedido pelo ID no Olist.
     * POST https://api.tiny.com.br/api2/pedido.obter.php
     *
     * Retorna todos os detalhes: cliente, itens, valores, situação, etc.
     * Usado na tela de detalhe do pedido.
     */
    public String obterPedido(String idOlist) {
        String body = "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                + "&id=" + URLEncoder.encode(idOlist, StandardCharsets.UTF_8)
                + "&formato=json";

        return restClient.post()
                .uri("/pedido.obter.php")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    // ============================================================
    // CADASTRO DE PEDIDO (Formulário "Novo Pedido")
    // ============================================================

    /**
     * Inclui um pedido no Olist/Tiny ERP.
     * POST https://api.tiny.com.br/api2/pedido.incluir.php
     *
     * Recebe os dados do formulário "Novo Pedido" e envia ao Olist.
     * Retorna o id e número do pedido criado no ERP.
     */
    public Map<String, String> incluirPedido(OlistPedidoRequestDto pedidoDto) {
        try {
            String pedidoJson = objectMapper.writeValueAsString(
                    montarPedidoParaApi(pedidoDto)
            );

            String body = "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                    + "&pedido=" + URLEncoder.encode(pedidoJson, StandardCharsets.UTF_8)
                    + "&formato=json";

            String resposta = restClient.post()
                    .uri("/pedido.incluir.php")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return parseRespostaInclusao(resposta);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao incluir pedido no Olist: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // Métodos auxiliares
    // ============================================================

    /**
     * Faz o parse da lista de pedidos retornada pela pesquisa.
     */
    private List<Map<String, String>> parsePedidos(String json) {
        List<Map<String, String>> pedidos = new ArrayList<>();

        try {
            JsonNode retorno = objectMapper.readTree(json).path("retorno");
            String status = retorno.path("status").asText();

            if (!"OK".equals(status)) {
                return pedidos;
            }

            JsonNode pedidosNode = retorno.path("pedidos");
            if (pedidosNode.isArray()) {
                for (JsonNode node : pedidosNode) {
                    JsonNode pedido = node.path("pedido");
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("id", pedido.path("id").asText());
                    map.put("numero", pedido.path("numero").asText());
                    map.put("numero_ecommerce", pedido.path("numero_ecommerce").asText());
                    map.put("data_pedido", pedido.path("data_pedido").asText());
                    map.put("data_prevista", pedido.path("data_prevista").asText());
                    map.put("nome_cliente", pedido.path("nome").asText());
                    map.put("valor", pedido.path("valor").asText());
                    map.put("situacao", pedido.path("situacao").asText());
                    map.put("codigo_rastreamento", pedido.path("codigo_rastreamento").asText());
                    pedidos.add(map);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao parsear pedidos do Olist: " + e.getMessage(), e);
        }

        return pedidos;
    }

    /**
     * Faz o parse da resposta de inclusão de pedido.
     */
    private Map<String, String> parseRespostaInclusao(String json) {
        try {
            JsonNode root = objectMapper.readTree(json).path("retorno");
            Map<String, String> resultado = new LinkedHashMap<>();
            resultado.put("status", root.path("status").asText());
            resultado.put("status_processamento", String.valueOf(root.path("status_processamento").asInt()));

            JsonNode registros = root.path("registros");
            if (registros.isArray() && !registros.isEmpty()) {
                JsonNode registro = registros.get(0).path("registro");
                resultado.put("id", registro.path("id").asText());
                resultado.put("numero", registro.path("numero").asText());
            }

            return resultado;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao parsear resposta do Olist: " + e.getMessage(), e);
        }
    }

    /**
     * Monta o JSON do pedido no formato que a API do Olist espera.
     */
    private Object montarPedidoParaApi(OlistPedidoRequestDto dto) {
        return new LinkedHashMap<>() {{
            put("numero_ecommerce", dto.getNumero_ecommerce());
            put("data_pedido", dto.getData_pedido());
            put("data_prevista", dto.getData_prevista());
            put("obs", dto.getObs());
            put("cliente", new LinkedHashMap<>() {{
                put("nome", dto.getCliente().getNome());
                put("tipo_pessoa", dto.getCliente().getTipo_pessoa());
                put("cpf_cnpj", dto.getCliente().getCpf_cnpj());
                put("email", dto.getCliente().getEmail());
                put("fone", dto.getCliente().getFone());
                put("endereco", dto.getCliente().getEndereco());
                put("numero", dto.getCliente().getNumero());
                put("complemento", dto.getCliente().getComplemento());
                put("bairro", dto.getCliente().getBairro());
                put("cep", dto.getCliente().getCep());
                put("cidade", dto.getCliente().getCidade());
                put("uf", dto.getCliente().getUf());
            }});
            put("itens", dto.getItens().stream().map(item ->
                    new LinkedHashMap<>() {{
                        put("item", new LinkedHashMap<>() {{
                            put("descricao", item.getDescricao());
                            put("unidade", item.getUnidade());
                            put("quantidade", item.getQuantidade());
                            put("valor_unitario", item.getValor_unitario());
                            put("info_adicional", item.getInfo_adicional());
                        }});
                    }}
            ).toList());
        }};
    }
}
```

### Por que:
O service centraliza **toda** comunicação com o Olist. Cada método faz UMA coisa:
- `pesquisarPedidosPorCpf(cpf)` — busca todos os pedidos de um cliente pelo CPF → para listar na **área do cliente**
- `obterPedido(id)` — obtém os detalhes completos de um pedido → para a **tela de detalhe**
- `incluirPedido(dto)` — cria pedido no ERP → quando o **formulário "Novo Pedido"** for salvo

A API do Olist usa `application/x-www-form-urlencoded` (não JSON no body), por isso os parâmetros são montados manualmente com `URLEncoder`.

### O que muda:
Qualquer parte do sistema pode injetar `OlistService`:
```java
olistService.pesquisarPedidosPorCpf("12345678909");  // busca
olistService.incluirPedido(pedidoDto);                 // cadastro
```

---

## PASSO 5 — Criar o OlistController

### O que fazer:
Crie `src/main/java/br/com/tonspersonalizados/controller/olist/OlistController.java`:

```java
package br.com.tonspersonalizados.controller.olist;

import br.com.tonspersonalizados.dto.olist.OlistPedidoRequestDto;
import br.com.tonspersonalizados.service.olist.OlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/olist")
@CrossOrigin(origins = "*")
public class OlistController {

    private final OlistService olistService;

    public OlistController(OlistService olistService) {
        this.olistService = olistService;
    }

    // ============================================================
    // BUSCA DE PEDIDOS (Área do Cliente)
    // ============================================================

    @GetMapping("/pedidos/cliente/{cpfCnpj}")
    public ResponseEntity<List<Map<String, String>>> buscarPedidosPorCpf(
            @PathVariable String cpfCnpj) {
        try {
            List<Map<String, String>> pedidos = olistService.pesquisarPedidosPorCpf(cpfCnpj);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/pedidos/{idOlist}")
    public ResponseEntity<String> obterPedido(@PathVariable String idOlist) {
        try {
            String resposta = olistService.obterPedido(idOlist);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao obter pedido: " + e.getMessage());
        }
    }

    // ============================================================
    // CADASTRO DE PEDIDO (Formulário "Novo Pedido")
    // ============================================================

    @PostMapping("/pedidos")
    public ResponseEntity<Map<String, String>> incluirPedido(
            @RequestBody OlistPedidoRequestDto pedidoDto) {
        try {
            Map<String, String> resposta = olistService.incluirPedido(pedidoDto);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
```

### Por que:
Três endpoints, cada um para uma funcionalidade:

| Endpoint | Método | Para quê |
|----------|--------|----------|
| `GET /olist/pedidos/cliente/{cpf}` | Listar pedidos | **Área do cliente** — lista todos os pedidos daquele CPF |
| `GET /olist/pedidos/{id}` | Detalhe do pedido | **Tela de detalhe** — retorna dados completos (itens, valores, etc.) |
| `POST /olist/pedidos` | Cadastrar pedido | **Formulário "Novo Pedido"** — cria o pedido no Olist |

### O que muda:
- O frontend chama `GET /olist/pedidos/cliente/12345678909` para listar na área do cliente
- O frontend chama `POST /olist/pedidos` com o JSON do formulário para cadastrar

---

## PASSO 6 — Liberar os endpoints no Spring Security

### O que fazer:
No arquivo `SecurityConfiguracao.java`, adicione a rota na lista `URLS_PERMITIDAS`:

```java
private static final String[] URLS_PERMITIDAS = {
        // ... rotas existentes ...
        "/olist/**",
        // ...
};
```

### Por que:
Sem isso, o Spring Security retorna `401 Unauthorized` para qualquer chamada ao `/olist/**`.

### O que muda:
Os endpoints do Olist ficam acessíveis.

---

## PASSO 7 — Testar com o Postman/Insomnia

### 7.1 — Buscar pedidos pelo CPF do cliente (Área do Cliente)

**GET** `http://localhost:8080/olist/pedidos/cliente/12345678909`

### Resposta esperada (sucesso):

```json
[
  {
    "id": "789456123",
    "numero": "1001",
    "numero_ecommerce": "TONS-001",
    "data_pedido": "28/04/2026",
    "data_prevista": "15/05/2026",
    "nome_cliente": "João Silva",
    "valor": "2357.50",
    "situacao": "aprovado",
    "codigo_rastreamento": ""
  },
  {
    "id": "789456200",
    "numero": "1015",
    "numero_ecommerce": "TONS-015",
    "data_pedido": "25/04/2026",
    "data_prevista": "10/05/2026",
    "nome_cliente": "João Silva",
    "valor": "890.00",
    "situacao": "enviado",
    "codigo_rastreamento": "BR123456789"
  }
]
```

### Resposta quando não encontra pedidos:

```json
[]
```

### 7.2 — Obter detalhes de um pedido específico

**GET** `http://localhost:8080/olist/pedidos/789456123`

Retorna o JSON completo do Olist com cliente, itens, valores, parcelas, etc.

### 7.3 — Cadastrar um pedido no Olist (Formulário "Novo Pedido")

**POST** `http://localhost:8080/olist/pedidos`

```json
{
  "numero_ecommerce": "TONS-001",
  "data_pedido": "28/04/2026",
  "data_prevista": "15/05/2026",
  "obs": "Pedido de teste - Tons Personalizados",
  "cliente": {
    "nome": "João Silva",
    "tipo_pessoa": "F",
    "cpf_cnpj": "12345678909",
    "email": "joao@email.com",
    "fone": "11999999999",
    "endereco": "Rua das Flores",
    "numero": "123",
    "complemento": "Apto 4",
    "bairro": "Centro",
    "cep": "01001000",
    "cidade": "São Paulo",
    "uf": "SP"
  },
  "itens": [
    {
      "descricao": "Camiseta Premium - Tamanho G - Branca",
      "unidade": "UN",
      "quantidade": 50,
      "valor_unitario": 35.90,
      "info_adicional": "100% Algodão, estampa frente e verso"
    },
    {
      "descricao": "Caneca Personalizada - 300ml",
      "unidade": "UN",
      "quantidade": 25,
      "valor_unitario": 22.50,
      "info_adicional": "Sublimação, arte fornecida pelo cliente"
    }
  ]
}
```

### Resposta esperada (sucesso):

```json
{
  "status": "OK",
  "status_processamento": "3",
  "id": "789456123",
  "numero": "1001"
}
```

### Por que:
Testar antes de integrar com o frontend evita debugar dois sistemas ao mesmo tempo.

---

## Mapeamento: Formulário "Novo Pedido" → API Olist

Baseado no formulário do frontend da Tons Personalizados:

### Dados do Cliente

| Campo no Formulário | Campo na API | Obrigatório para NF? |
|---------------------|-------------|----------------------|
| Nome Completo / Razão Social | `cliente.nome` | **Sim** |
| CPF/CNPJ *(do cadastro)* | `cliente.cpf_cnpj` | **Sim** |
| Endereço *(do cadastro)* | `cliente.endereco` + `numero` + `bairro` + `cep` + `cidade` + `uf` | **Sim** |
| Telefone *(do cadastro)* | `cliente.fone` | Não |
| Email *(do cadastro)* | `cliente.email` | Não |

### Itens e Composição

| Campo no Formulário | Campo na API | Obrigatório? |
|---------------------|-------------|-------------|
| Produto (ex: Camiseta Premium) | `item.descricao` | **Sim** |
| Tamanho (G, M) | Inclui na `descricao` | — |
| Cor Estampa / Cor Material | Inclui na `descricao` ou `info_adicional` | — |
| Composição (100% Algodão) | `item.info_adicional` | Não |
| QTD | `item.quantidade` | **Sim** |
| Preço Unitário | `item.valor_unitario` | **Sim** |
| Unidade | `item.unidade` (ex: "UN") | **Sim** |

---

## Fluxo Completo: Pedido → Nota Fiscal

```
1. Frontend salva pedido ──→ POST /olist/pedidos
   (cria no Olist automaticamente)

2. João revisa no painel do Olist ──→ Aprova manualmente

3. João emite NF-e no painel do Olist ──→ Transmite para a SEFAZ

4. Cliente consulta na área do cliente ──→ GET /olist/pedidos/cliente/{cpf}
   (busca status atualizado direto do Olist)
```

---

## Filtros disponíveis na pesquisa de pedidos

A API do Olist aceita os seguintes filtros (podem ser combinados):

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| **`cpf_cnpj`** | string | CPF ou CNPJ do cliente ✅ **(usado no projeto)** |
| `cliente` | string | Nome ou código (ou parte) do cliente |
| `numero` | string | Número do pedido no Olist |
| `numeroEcommerce` | string | Número do pedido no seu sistema |
| `situacao` | string | Status do pedido (aberto, aprovado, enviado...) |
| `dataInicial` | string | Data de cadastro inicial (dd/mm/yyyy) |
| `dataFinal` | string | Data de cadastro final (dd/mm/yyyy) |
| `pagina` | int | Paginação (100 registros por página) |

> ⚠️ **Pelo menos um** filtro deve ser informado. Não é possível listar todos sem filtro.

---

## Situações do Pedido no Olist

| Código | Descrição | Significado para o cliente |
|--------|-----------|---------------------------|
| `aberto` | Pedido criado | "Seu pedido foi registrado" |
| `aprovado` | Pedido aprovado | "Seu pedido foi confirmado" |
| `preparando_envio` | Em produção | "Seu pedido está sendo confeccionado" |
| `faturado` | NF emitida | "Nota fiscal emitida" |
| `pronto_envio` | Pronto para entrega | "Seu pedido está pronto!" |
| `enviado` | Despachado | "Seu pedido foi enviado" |
| `entregue` | Entregue | "Pedido entregue" |
| `cancelado` | Cancelado | "Pedido cancelado" |

---

## Troubleshooting

| Erro | Causa | Solução |
|------|-------|---------|
| `Token inválido` | Token expirado ou incorreto | Verificar o token em Configurações → Integrações |
| `Limite de requisições excedido` | Mais de 30 req/min | Implementar retry com delay de 2s |
| Lista vazia `[]` | CPF não tem pedidos no Olist | Verificar se o João cadastrou o pedido com esse CPF |
| `CNPJ/CPF inválido` | CPF/CNPJ com formato errado | Enviar somente números, sem pontos ou traços |
| `status_processamento: 1` | Erro genérico | Ler o array `erros[]` na resposta para detalhes |
