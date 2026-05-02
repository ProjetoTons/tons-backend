# Abordagem A — Foto via URL externa

## Conceito

O backend Spring **nunca recebe o arquivo**. Ele apenas armazena uma string (`fotoUrl`) apontando para onde a imagem está hospedada. O upload do arquivo em si é responsabilidade do frontend (ou de um endpoint dedicado de "gerar URL de upload").

```
[Frontend] ──upload do arquivo──> [S3 / Cloudinary / Imgur / etc]
                                         │
                                         └── retorna URL pública
                                                  │
[Frontend] ──POST /usuarios/funcionario {fotoUrl}──> [Spring Backend]
                                                            │
                                                            └── salva fotoUrl no banco
```

---

## Padrões de fluxo possíveis

### Padrão 1 — Frontend envia direto para o storage (recomendado)

O frontend recebe credenciais do storage (ou usa um SDK público como Cloudinary unsigned upload) e faz o upload diretamente. Backend só recebe a URL final.

- **Vantagens:** backend não vê o arquivo; sem custo de banda no servidor; sem timeout.
- **Desvantagens:** acoplamento do frontend com o storage.

### Padrão 2 — Backend gera "presigned URL"

Frontend pede ao backend uma URL temporária assinada → faz `PUT` direto no S3 → manda URL final pro backend salvar.

- **Vantagens:** controle de quem pode subir; backend não vê o arquivo.
- **Desvantagens:** mais complexo; precisa SDK AWS no backend.

### Padrão 3 — Confiar em qualquer URL

Para projetos acadêmicos / MVPs: frontend hospeda a imagem onde quiser (Imgur, ImgBB, link de drive público) e manda só a URL pro backend. Backend só valida formato.

- **Vantagens:** zero infra, zero credencial.
- **Desvantagens:** sem controle sobre disponibilidade/permanência da imagem.

---

## Mudanças no código

### 1. Entidade `Usuario`

```java
@Column(name = "foto_url", length = 500)
private String fotoUrl;

public String getFotoUrl() { return fotoUrl; }
public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
```

### 2. DTO de entrada — `FuncionarioRequestDto`

```java
@Size(max = 500)
@Pattern(regexp = "^https?://.+", message = "fotoUrl deve ser uma URL HTTP/HTTPS válida")
private String fotoUrl;

public String getFotoUrl() { return fotoUrl; }
```

> Não marcamos `@NotNull` se a foto for opcional.

### 3. DTO de saída — `FuncionarioResponseDto`

```java
private String fotoUrl;

public String getFotoUrl() { return fotoUrl; }
public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
```

### 4. Service — `UsuarioService`

Em `cadastrarFuncionario(...)`:

```java
funcionario.setFotoUrl(funcionarioDto.getFotoUrl());
```

Em `atualizarFuncionario(...)`:

```java
funcionarioExistente.setFotoUrl(funcionarioDto.getFotoUrl());
```

Em `listarFuncionarios()`, no `map` para DTO:

```java
dto.setFotoUrl(funcionario.getFotoUrl());
```

### 5. Banco de dados

Como o projeto usa JPA com `ddl-auto`, o Hibernate cria a coluna automaticamente. Caso esteja em `validate` ou `none`:

```sql
ALTER TABLE usuarios ADD COLUMN foto_url VARCHAR(500);
```

---

## Endpoint resultante

**`POST /usuarios/funcionario`** com body:

```json
{
  "nome": "João Silva",
  "email": "joao@empresa.com",
  "telefone": "11999999999",
  "senha": "senha123",
  "dataNascimento": "1990-05-15",
  "acessos": [1, 2],
  "fotoUrl": "https://res.cloudinary.com/demo/image/upload/v1234/avatar.jpg"
}
```

**`GET /usuarios/funcionario`** retorna a `fotoUrl` em cada item da lista — o frontend usa direto em `<img src="{fotoUrl}">`.

---

## Validações de segurança

1. **Validar protocolo:** aceitar apenas `https://` em produção (evita conteúdo misto).
2. **Allowlist de domínios** (opcional): só aceitar URLs de domínios confiáveis (ex.: `*.cloudinary.com`, `*.amazonaws.com`).
3. **Tamanho máximo da string:** 500 caracteres é razoável.
4. **Não fazer fetch da URL no backend** sem proteção contra **SSRF** (Server-Side Request Forgery) — se você só armazena a string, está seguro.

---

## Quando usar

- Aplicação que vai pra **produção/cloud** com volume real.
- Você já tem ou pretende contratar um storage externo (S3, Cloudinary, GCS).
- Performance e escalabilidade importam.

## Quando NÃO usar

- Projeto acadêmico/local sem infra externa configurada.
- Sem internet garantida no ambiente de execução.
- Você quer demonstrar `MultipartFile` e manipulação de arquivos no Spring.
