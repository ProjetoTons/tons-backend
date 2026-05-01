# 📱 Guia Completo — Integração WhatsApp Business API (Meta Cloud API) com Spring Boot

---

## Visão Geral

Este guia documenta o passo a passo para configurar a **API oficial do WhatsApp Business (Meta Cloud API)** no backend da Tons Personalizados.

### Por que usar a API oficial da Meta?

- **Gratuita** — 1.000 mensagens de serviço/mês sem custo (sandbox ilimitado para testes)
- **Confiável** — Infraestrutura da Meta, sem necessidade de servidores próprios
- **Profissional** — Suporte a templates ricos (botões, imagens, variáveis)
- **Automação** — Permite enviar notificações automáticas via triggers (cadastro, mudança de fase de pedido, etc.)

### O que muda no projeto?

O backend passa a enviar mensagens de WhatsApp diretamente para os clientes via chamada HTTP REST à API da Meta. Isso permite:
- Confirmar cadastros automaticamente
- Notificar clientes sobre status de pedidos
- Avisar sobre cada fase da confecção

---

## PASSO 1 — Criar conta no Meta for Developers

### O que fazer:
1. Acesse [developers.facebook.com](https://developers.facebook.com)
2. Faça login com sua conta Facebook
3. Clique em **"Meus apps"** → **"Criar app"**
4. Selecione o tipo **"Empresa"**
5. Dê um nome ao app (ex: `tons`)
6. No painel do app, clique em **"Adicionar produto"** → selecione **"WhatsApp"**

### Por que:
A Meta exige um App registrado para gerar tokens de acesso e associar um número de telefone de teste para envio de mensagens.

### O que muda:
Você terá acesso ao painel de configuração da API, com um **número de teste** (`+1 555-145-0402`) e um **token de acesso temporário** (válido por 24h).

---

## PASSO 2 — Obter as credenciais

### O que fazer:
No painel do app, vá em **WhatsApp** → **Configuração da API**. Anote:

| Campo | Onde encontrar | Exemplo |
|-------|----------------|---------|
| **Phone Number ID** | "Identificação do número de telefone" | `1112803255242637` |
| **WhatsApp Business Account ID** | "Identificação da conta do WhatsApp Business" | `1464486798473225` |
| **Token de acesso** | Campo no topo da página (clique em "Gerar token de acesso") | `EAF9Q2Gp1MAQB...` |

> ⚠️ **CUIDADO:** O Phone Number ID é **diferente** do WhatsApp Business Account ID. No código, usamos o **Phone Number ID**.

### Por que:
O Phone Number ID identifica **qual número** envia a mensagem. O Token autoriza a chamada à API. Sem esses dois, a Meta rejeita a requisição.

### O que muda:
Essas credenciais serão configuradas no `application.properties` do Spring Boot.

---

## PASSO 3 — Adicionar destinatários de teste

### O que fazer:
1. No painel da API, seção **"Até"**, clique em **"Gerenciar lista de números de telefone"**
2. Adicione os números que vão **receber** mensagens (ex: seu celular)
3. A Meta envia um código de verificação por SMS — confirme

### Por que:
No modo sandbox (desenvolvimento), a Meta só permite enviar mensagens para números **previamente autorizados**. Isso evita spam durante testes.

### O que muda:
Os números adicionados poderão receber mensagens do número de teste. Em produção (com empresa verificada), essa restrição não existe.

---

## PASSO 4 — Configurar o `application.properties`

### O que fazer:
Adicione as seguintes propriedades no arquivo `src/main/resources/application.properties`:

```properties
# ---------------------------------------------------------------------
# WhatsApp Business (Meta Cloud API)
# ---------------------------------------------------------------------
whatsapp.api.url=https://graph.facebook.com/v25.0
whatsapp.api.phone-number-id=SEU_PHONE_NUMBER_ID
whatsapp.api.access-token=SEU_TOKEN_DE_ACESSO
```

### Por que:
Externalizar as credenciais no `application.properties` permite trocar tokens sem alterar código Java. Quando o token expirar (24h no modo dev), basta atualizar aqui e reiniciar.

### O que muda:
O `WhatsAppService` lê essas propriedades via `@Value` na inicialização do Spring.

---

## PASSO 5 — Criar o DTO de requisição

### O que fazer:
Crie o arquivo `src/main/java/br/com/tonspersonalizados/dto/notificacoes/WhatsAppRequestDto.java`:

```java
package br.com.tonspersonalizados.dto.notificacoes;

public class WhatsAppRequestDto {

    private String telefone;   // Número do destinatário (ex: "5511999999999")
    private String mensagem;   // Texto livre (só funciona na janela de 24h)

    // getters e setters
}
```

### Por que:
O DTO encapsula apenas os dados necessários para enviar texto livre via WhatsApp. Templates são enviados diretamente pelo service sem DTO.

### O que muda:
O DTO é usado apenas no endpoint de texto livre (`/enviar-mensagem`). Endpoints de template recebem o telefone via `@PathVariable`.

---

## PASSO 6 — Criar o WhatsAppService

### O que fazer:
Crie o arquivo `src/main/java/br/com/tonspersonalizados/service/notificacoes/WhatsAppService.java`:

```java
package br.com.tonspersonalizados.service.notificacoes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WhatsAppService {

    private final RestClient restClient;
    private final String phoneNumberId;

    public WhatsAppService(
            @Value("${whatsapp.api.url}") String apiUrl,
            @Value("${whatsapp.api.phone-number-id}") String phoneNumberId,
            @Value("${whatsapp.api.access-token}") String accessToken
    ) {
        this.phoneNumberId = phoneNumberId;
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();
    }

    public String enviarTemplate(String telefone, String template) {
        String body = """
                {
                  "messaging_product": "whatsapp",
                  "to": "%s",
                  "type": "template",
                  "template": { "name": "%s", "language": { "code": "pt_BR" } }
                }
                """.formatted(limparTelefone(telefone), template);

        return enviar(body);
    }

    public String enviarMensagem(String telefone, String mensagem) {
        String body = """
                {
                  "messaging_product": "whatsapp",
                  "to": "%s",
                  "type": "text",
                  "text": { "body": "%s" }
                }
                """.formatted(limparTelefone(telefone), mensagem);

        return enviar(body);
    }

    private String enviar(String body) {
        return restClient.post()
                .uri("/{phoneNumberId}/messages", phoneNumberId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    private String limparTelefone(String telefone) {
        return telefone.replaceAll("[^0-9]", "");
    }
}
```

### Por que:
O service tem dois métodos simples e diretos:
- `enviarTemplate(telefone, template)` — envia template aprovado (funciona **sempre**)
- `enviarMensagem(telefone, mensagem)` — envia texto livre (só na janela de 24h)

Usa o `RestClient` do Spring Boot 4, substituto moderno do `RestTemplate`.

### O que muda:
Qualquer parte do sistema pode injetar `WhatsAppService` com uma linha:
```java
whatsAppService.enviarTemplate("5511999999999", "hello_world");
```

---

## PASSO 7 — Criar o WhatsAppController

### O que fazer:
Crie `src/main/java/br/com/tonspersonalizados/controller/notificacoes/WhatsAppController.java`:

```java
package br.com.tonspersonalizados.controller.notificacoes;

import br.com.tonspersonalizados.dto.notificacoes.WhatsAppRequestDto;
import br.com.tonspersonalizados.service.notificacoes.WhatsAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/whatsapp")
@CrossOrigin(origins = "*")
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    public WhatsAppController(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    @PostMapping("/enviar-mensagem")
    public ResponseEntity<String> enviarMensagem(@RequestBody WhatsAppRequestDto dto) {
        try {
            String resposta = whatsAppService.enviarMensagem(dto.getTelefone(), dto.getMensagem());
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao enviar WhatsApp: " + e.getMessage());
        }
    }

    @PostMapping("/confirmar-cadastro/{telefone}")
    public ResponseEntity<String> confirmarCadastro(@PathVariable String telefone) {
        try {
            String resposta = whatsAppService.enviarTemplate(telefone, "hello_world");
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao enviar WhatsApp: " + e.getMessage());
        }
    }
}
```

### Por que:
Cada tipo de notificação tem seu **próprio endpoint**, tornando o código mais legível e fácil de testar. Para adicionar novos templates (pedido, fase, etc.), basta criar um novo método no controller.

### O que muda:

| Endpoint | Ação |
|----------|------|
| `POST /whatsapp/enviar-mensagem` | Texto livre (body: `telefone` + `mensagem`) |
| `POST /whatsapp/confirmar-cadastro/{telefone}` | Template de confirmação de cadastro |

---

## PASSO 8 — Liberar os endpoints no Spring Security

### O que fazer:
No arquivo `SecurityConfiguracao.java`, adicione as rotas na lista `URLS_PERMITIDAS`:

```java
private static final String[] URLS_PERMITIDAS = {
        // ... rotas existentes ...
        "/notificacao/**",
        "/whatsapp/**",
        // ...
};
```

### Por que:
Sem isso, o Spring Security retorna `401 Unauthorized` para qualquer chamada ao `/whatsapp/**`.

### O que muda:
Os endpoints de notificação e WhatsApp ficam acessíveis sem token JWT.

---

## PASSO 9 — Integrar com o cadastro de usuários

### O que fazer:
No `UsuarioService.java`, injete o `WhatsAppService` e adicione o envio após o `save`:

```java
usuarioRepository.save(usuario);

try {
    whatsAppService.enviarTemplate("55" + usuario.getTelefone(), "hello_world");
} catch (Exception e) {
    // Não impede o cadastro se o WhatsApp falhar
}
```

### Por que:
Automatiza o envio de mensagem de boas-vindas com apenas **uma linha**. O `try/catch` garante que uma falha no WhatsApp (token expirado, número inválido) **não impede** o cadastro do usuário.

### O que muda:
Todo novo usuário cadastrado recebe uma mensagem de confirmação no WhatsApp automaticamente.

---

## PASSO 10 — Criar templates personalizados

### O que fazer:
1. Acesse o [WhatsApp Manager](https://business.facebook.com/wa/manage/message-templates/)
2. Clique em **"Criar modelo"**
3. Preencha:
   - **Categoria:** Utilitário
   - **Nome:** `confirmacao_cadastro` (sem espaços, só letras minúsculas e underline)
   - **Idioma:** Português (BR)
   - **Corpo:** `Olá {{1}}! Seu cadastro na Tons Personalizados foi confirmado com sucesso!`
4. Envie para aprovação

### Por que:
Templates são a **única forma** de iniciar uma conversa com o cliente. O `hello_world` é genérico — templates personalizados passam profissionalismo.

### O que muda:
Após aprovação, troque `"hello_world"` pelo nome do template no código. Variáveis (`{{1}}`, `{{2}}`) permitem personalizar a mensagem por cliente/pedido.

---

## Conceitos Importantes

### Janela de 24 horas
A Meta impõe uma regra: após enviar um template, o cliente precisa **responder** para abrir uma janela de 24h. Dentro dessa janela, o backend pode enviar texto livre. Fora dela, só templates.

### Tipos de mensagem

| Tipo | Quando usar | Precisa de janela? |
|------|-------------|-------------------|
| **Template** | Iniciar conversa, notificações automáticas | Não |
| **Texto livre** | Respostas, mensagens dentro da conversa | Sim (24h) |

### Token de acesso
- **Temporário** (modo dev): expira em 24h, gere novo no painel
- **Permanente** (produção): criado via System User no Business Manager, não expira

### Limites

| Modo | Mensagens | Destinatários |
|------|-----------|---------------|
| **Sandbox** | Ilimitadas | Apenas números cadastrados manualmente (máx. 5) |
| **Produção** | 1.000/mês grátis | Qualquer número com WhatsApp |

---

## Templates sugeridos para a Tons Personalizados

| Nome do template | Trigger | Texto sugerido |
|------------------|---------|----------------|
| `confirmacao_cadastro` | Cadastro de usuário | "Olá {{1}}! Seu cadastro na Tons foi confirmado." |
| `pedido_confirmado` | Criação de pedido | "Olá {{1}}! Seu pedido #{{2}} foi recebido." |
| `producao_iniciada` | Início da confecção | "{{1}}, a produção do pedido #{{2}} começou!" |
| `producao_fase` | Mudança de fase | "{{1}}, seu pedido #{{2}} está na fase: {{3}}" |
| `pedido_pronto` | Confecção finalizada | "{{1}}, seu pedido #{{2}} está pronto!" |
| `pedido_enviado` | Despacho | "{{1}}, pedido #{{2}} enviado! Rastreio: {{3}}" |

---

## Troubleshooting

| Erro | Causa | Solução |
|------|-------|---------|
| `401 Unauthorized` | Token expirado | Gerar novo token no painel da Meta |
| `400 Object with ID 'X' does not exist` | Phone Number ID errado | Verificar o ID correto em WhatsApp → API Setup |
| `400 Unsupported post request` | Versão da API incompatível | Usar a versão mostrada no painel (ex: `v25.0`) |
| Mensagem não chega (200 OK) | Texto livre sem janela aberta | Enviar template primeiro, esperar resposta |
| `131030 - Recipient not in allowed list` | Número não cadastrado no sandbox | Adicionar o número na lista de destinatários |
