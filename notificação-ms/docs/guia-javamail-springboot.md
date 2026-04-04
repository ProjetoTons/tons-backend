# 📧 Guia Completo — Envio de E-mails com Spring Boot (JavaMail)

---

## PASSO 1 — Adicionar a dependência no `pom.xml`

### O que fazer:
Adicione o **Spring Boot Starter Mail** dentro da tag `<dependencies>` do seu `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

Depois de adicionar, recarregue o Maven (no IntelliJ: ícone do Maven → **Reload All Maven Projects**).

### O que é?
É um pacote do Spring Boot que traz tudo que você precisa para enviar e-mails: a biblioteca JavaMail e as auto-configurações do Spring. Sem ela, você teria que importar e configurar o JavaMail manualmente, que é bem mais trabalhoso.

### Por que fazer?
Porque o Spring Boot sozinho não vem com suporte a e-mail. Essa dependência "ativa" essa funcionalidade e faz o Spring criar automaticamente o bean `JavaMailSender` baseado nas configurações do `application.properties`.

### Como funciona?
Quando você adiciona o `spring-boot-starter-mail` no `pom.xml`, o Maven **baixa** essa biblioteca e todas as suas sub-dependências (incluindo o JavaMail da Jakarta).

Na hora que o Spring Boot inicia a aplicação, ele tem um mecanismo chamado **auto-configuration**: ele detecta que essa biblioteca está no classpath e automaticamente **cria e configura** o bean `JavaMailSender` para você. É por isso que você consegue injetar `JavaMailSender` no Service sem nunca ter feito `new JavaMailSender()` — o Spring cuida disso.

---

## PASSO 2 — Gerar a Senha de App no Gmail

### O que fazer:
1. Acesse: [myaccount.google.com](https://myaccount.google.com)
2. Vá em **Segurança**
3. Ative a **Verificação em duas etapas** (se ainda não tiver)
4. Depois de ativar, pesquise por **"Senhas de app"** na barra de busca da conta Google
5. Crie uma senha de app para "E-mail" → "Outro" → dê o nome "Spring Boot"
6. O Google vai gerar uma senha de **16 caracteres** (ex: `abcd efgh ijkl mnop`)
7. **Guarde essa senha!** Você só vê ela uma vez

> ⚠️ **IMPORTANTE:** A senha que vai no `application.properties` é essa senha de app de 16 caracteres, **NÃO** a senha normal da conta Gmail.

### O que é?
A Senha de App é um **token alternativo** que o Google gera exclusivamente para uma aplicação específica, substituindo sua senha real.

### Por que fazer?
Desde 2022, o Gmail **não permite** que aplicações terceiras façam login com a senha normal da conta (para proteger contra roubo de senhas). A Senha de App é a forma autorizada de autenticar aplicações.

### Como funciona?
Quando o Spring se conecta ao servidor SMTP do Gmail, ele envia esse token no lugar da senha. O Gmail valida o token e **libera** o envio. Se você revogar a Senha de App na conta Google, o Spring para de conseguir enviar — sem afetar sua senha real.

---

## PASSO 3 — Configurar o `application.properties`

### O que fazer:
No arquivo `src/main/resources/application.properties`, adicione:

```properties
# Configurações do servidor SMTP do Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seuemail@gmail.com
spring.mail.password=senha-de-app-16-caracteres

# Propriedades de segurança (TLS)
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

> ⚠️ **Segurança:** Em projetos reais, nunca coloque a senha direto no properties. Use variáveis de ambiente. Para projeto acadêmico, funciona.

### O que é cada propriedade?
- `spring.mail.host=smtp.gmail.com` → É o **endereço do servidor** que vai enviar o e-mail. Todo provedor de e-mail tem um. O do Gmail é esse.
- `spring.mail.port=587` → É a **porta de comunicação** do servidor SMTP. A porta 587 é a padrão para envio com criptografia TLS.
- `spring.mail.username` → O e-mail que vai **aparecer como remetente** e que autentica no servidor.
- `spring.mail.password` → A credencial para **provar ao servidor** que você tem permissão de enviar por aquele e-mail.
- `mail.smtp.auth=true` → Diz ao servidor: **"eu vou me identificar"**. Sem isso, o Gmail rejeita a conexão.
- `mail.smtp.starttls.enable/required` → Ativa a **criptografia TLS**. É como um "cadeado" na comunicação. O Gmail **exige** isso — se não ativar, ele recusa a conexão.
- `mail.smtp.ssl.trust=smtp.gmail.com` → Diz ao JavaMail para **confiar no certificado SSL** do servidor `smtp.gmail.com`. Sem isso, o Java pode não reconhecer o certificado do Gmail no seu truststore local e lançar o erro `PKIX path building failed` — que significa que ele não conseguiu validar a identidade do servidor. Essa propriedade resolve o problema dizendo: **"pode confiar nesse servidor, eu garanto"**.

### Por que configurar aqui?
Porque o Spring Boot lê essas propriedades automaticamente na inicialização e usa para construir o `JavaMailSender`. Você configura uma vez, e todo o projeto já sabe como se conectar ao servidor de e-mail.

### Como funciona a conexão SMTP?

```
Sua aplicação Spring Boot
        │
        │ 1. Abre conexão TCP na porta 587
        ▼
   smtp.gmail.com (servidor do Google)
        │
        │ 2. Inicia handshake STARTTLS (criptografia)
        ▼
   Conexão agora é criptografada (TLS)
        │
        │ 3. Envia username + senha de app (autenticação)
        ▼
   Gmail valida as credenciais
        │
        │ 4. Envia os dados do e-mail (de, para, assunto, corpo)
        ▼
   Gmail coloca o e-mail na fila de entrega
        │
        │ 5. Gmail entrega o e-mail ao destinatário
        ▼
   📧 E-mail chega na caixa de entrada
```

Cada propriedade do `application.properties` controla uma dessas etapas. Se faltar qualquer uma, a conexão é recusada.

---

## PASSO 4 — Criar o DTO para receber os dados do e-mail

### O que fazer:
Crie a classe no pacote `dto`:

**Caminho:** `br.com.tonspersonalizados.notificacao_ms.dto.EmailDTO`

```java
package br.com.tonspersonalizados.notificacao_ms.dto;

public class EmailDTO {

    private String destinatario;
    private String assunto;
    private String corpo;

    // Construtores
    public EmailDTO() {}

    public EmailDTO(String destinatario, String assunto, String corpo) {
        this.destinatario = destinatario;
        this.assunto = assunto;
        this.corpo = corpo;
    }

    // Getters e Setters
    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public String getCorpo() { return corpo; }
    public void setCorpo(String corpo) { this.corpo = corpo; }
}
```

> 💡 Se estiver usando **Lombok**, pode substituir tudo por `@Data`, `@AllArgsConstructor` e `@NoArgsConstructor`.

### O que é um DTO?
É um **Data Transfer Object** — um objeto simples que serve **apenas para transportar dados** entre camadas da aplicação. Ele não tem lógica de negócio, só campos, getters e setters.

### Por que criar?
Porque quando o front-end (ou outro microsserviço) fizer a requisição POST, ele vai enviar um JSON. O Spring precisa de uma classe Java para **mapear esse JSON automaticamente**. O DTO é essa classe. Sem ele, você teria que extrair os dados manualmente do corpo da requisição.

### Como o DTO é mapeado do JSON?
Quando chega uma requisição POST com este JSON:

```json
{
    "destinatario": "fulano@gmail.com",
    "assunto": "Teste",
    "corpo": "Olá!"
}
```

O Spring usa uma biblioteca chamada **Jackson** (já vem embutida) que faz a **desserialização**: ela lê cada chave do JSON e procura um campo com o **mesmo nome** na classe `EmailDTO`. Depois chama o `setter` correspondente para preencher o objeto.

```
JSON "destinatario" → dto.setDestinatario("fulano@gmail.com")
JSON "assunto"      → dto.setAssunto("Teste")
JSON "corpo"        → dto.setCorpo("Olá!")
```

Por isso os nomes dos campos do DTO **precisam bater** com as chaves do JSON (ou usar `@JsonProperty` para mapear nomes diferentes).

---

## PASSO 5 — Criar o Service de envio de e-mail

### O que fazer:
Crie a classe no pacote `service`:

**Caminho:** `br.com.tonspersonalizados.notificacao_ms.service.EmailService`

```java
package br.com.tonspersonalizados.notificacao_ms.service;

import br.com.tonspersonalizados.notificacao_ms.dto.EmailDTO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(EmailDTO dto) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom("seuemail@gmail.com");
            mensagem.setTo(dto.getDestinatario());
            mensagem.setSubject(dto.getAssunto());
            mensagem.setText(dto.getCorpo());

            mailSender.send(mensagem);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}
```

### O que é?
É a camada de **lógica de negócio**. É onde fica o código que realmente **faz as coisas acontecerem** — no seu caso, montar e enviar o e-mail.

**Classes utilizadas:**
- `JavaMailSender` — É uma **interface do Spring** que encapsula toda a complexidade de conexão SMTP, autenticação e envio. Você só chama `.send()` e ele cuida do resto. O Spring cria esse objeto automaticamente com base no que você configurou no `application.properties`.
- `SimpleMailMessage` — É uma classe que representa um **e-mail em texto puro**. Você configura destinatário, assunto, corpo — como preencher um formulário de e-mail. Para e-mails com HTML, existe o `MimeMessage`.

### Por que separar do Controller?
Por causa do princípio de **responsabilidade única**:
- O **Controller** só cuida de **receber a requisição HTTP** e **devolver a resposta**
- O **Service** cuida da **lógica** (enviar e-mail, validar dados, etc.)

Se amanhã você precisar enviar e-mail de outro lugar (ex: um listener de fila, um agendamento automático), basta chamar o Service — sem duplicar código.

### Como funciona linha por linha?

```java
SimpleMailMessage mensagem = new SimpleMailMessage();
// 1. Cria um "envelope" de e-mail vazio

mensagem.setFrom("seuemail@gmail.com");
// 2. Define QUEM está enviando (precisa ser o mesmo do application.properties)

mensagem.setTo(dto.getDestinatario());
// 3. Define QUEM vai receber

mensagem.setSubject(dto.getAssunto());
// 4. Define o ASSUNTO (a linha de título do e-mail)

mensagem.setText(dto.getCorpo());
// 5. Define o CORPO (o conteúdo do e-mail, em texto puro)

mailSender.send(mensagem);
// 6. Aqui o Spring ABRE a conexão SMTP (aquele fluxo do passo 3),
//    envia tudo e fecha a conexão
```

Se der erro em qualquer etapa (senha errada, e-mail inválido, sem internet), o `.send()` lança uma exceção — por isso o try-catch.

---

## PASSO 6 — Criar o endpoint no Controller

### O que fazer:
No `NotificacaoController`, adicione:

```java
package br.com.tonspersonalizados.notificacao_ms.controller;

import br.com.tonspersonalizados.notificacao_ms.dto.EmailDTO;
import br.com.tonspersonalizados.notificacao_ms.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificacao")
@CrossOrigin(origins = "*")
public class NotificacaoController {

    private final EmailService emailService;

    public NotificacaoController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/enviar-email")
    public ResponseEntity<String> enviarEmail(@RequestBody EmailDTO dto) {
        try {
            emailService.enviarEmail(dto);
            return ResponseEntity.ok("E-mail enviado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}
```

### O que é cada coisa?
- **`@RequestBody`** → Annotation que diz ao Spring: **"pega o JSON que veio no corpo da requisição e converte para esse objeto Java"**. É a ponte entre o JSON e o seu DTO.
- **`ResponseEntity`** → Classe que representa a **resposta HTTP completa** — status code + corpo. Permite retornar `200 OK` com mensagem de sucesso, `500 Internal Server Error` com mensagem de erro, etc. É mais profissional do que retornar só uma String.

### Por que o try-catch no Controller?
Para **não estourar erro 500 genérico** no cliente. Se o envio falhar (e-mail inválido, servidor fora do ar, senha errada), você captura o erro e retorna uma mensagem amigável.

### Como funciona o fluxo completo?

```
Cliente (Postman) ──POST /notificacao/enviar-email──▶ Spring Boot
                                                        │
                    1. Spring vê @PostMapping e          │
                       roteia para o método              │
                    2. @RequestBody pega o JSON e        │
                       converte para EmailDTO            │
                    3. Chama emailService.enviarEmail()   │
                    4. Se deu certo:                      │
                       ResponseEntity.ok() → HTTP 200    │
                    5. Se deu erro:                       │
                       ResponseEntity.internalServerError │
                       → HTTP 500                        │
                                                        ▼
Cliente (Postman) ◀─── JSON de resposta ─────────────────
```

O `ResponseEntity` monta a resposta HTTP completa:
- `.ok("mensagem")` → status **200** + corpo com a string
- `.internalServerError().body("erro")` → status **500** + corpo com a mensagem de erro

---

## PASSO 7 — Testar

Use o **Postman** ou **Insomnia** para testar. Faça um `POST` para:

```
http://localhost:8080/notificacao/enviar-email
```

Com o body JSON:

```json
{
    "destinatario": "emaildodestino@gmail.com",
    "assunto": "Teste de e-mail",
    "corpo": "Olá! Este é um e-mail de teste enviado pelo Spring Boot."
}
```

---

## 🧠 Tabela de Conceitos

| Conceito | Descrição |
|---|---|
| **SMTP** | Protocolo usado para enviar e-mails. O Gmail disponibiliza um servidor SMTP gratuito |
| **TLS/STARTTLS** | Criptografia da conexão. O Gmail exige isso (porta 587) |
| **JavaMailSender** | Interface do Spring que abstrai toda a complexidade do JavaMail |
| **SimpleMailMessage** | Classe para e-mails simples (texto puro). Para HTML, use `MimeMessage` |
| **Senha de App** | Token de autenticação do Google que substitui sua senha real |
| **@RequestBody** | Annotation do Spring que converte o JSON do corpo da requisição para o DTO |
| **DTO** | Data Transfer Object — objeto simples para transportar dados entre camadas |
| **ResponseEntity** | Classe que representa a resposta HTTP completa (status + corpo) |
| **Jackson** | Biblioteca que converte JSON ↔ objetos Java automaticamente |
| **Auto-configuration** | Mecanismo do Spring Boot que cria beans automaticamente com base nas dependências e properties |

---

## 📁 Estrutura Final do Projeto

```
notificacao_ms/
├── controller/
│   └── NotificacaoController.java    ← endpoint POST
├── service/
│   └── EmailService.java             ← lógica de envio
├── dto/
│   └── EmailDTO.java                 ← dados do e-mail
```

---

## 🔥 Extras (para evoluir depois)

### E-mail com HTML
Use `MimeMessage` + `MimeMessageHelper` em vez de `SimpleMailMessage`:

```java
MimeMessage mime = mailSender.createMimeMessage();
MimeMessageHelper helper = new MimeMessageHelper(mime, true);
helper.setTo(destinatario);
helper.setSubject(assunto);
helper.setText("<h1>Olá!</h1><p>Conteúdo HTML</p>", true); // true = é HTML
mailSender.send(mime);
```

### E-mail com Anexo
Via `MimeMessageHelper`:

```java
helper.addAttachment("arquivo.pdf", new File("/caminho/do/arquivo.pdf"));
```

### Templates de E-mail
Use **Thymeleaf** como template engine para criar e-mails bonitos com HTML dinâmico.

---

## ⚡ Limites do Gmail (conta pessoal gratuita)

- **500 e-mails/dia**
- Suficiente para projetos acadêmicos e pequenos projetos
