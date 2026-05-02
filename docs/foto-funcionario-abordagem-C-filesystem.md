# Abordagem C — Foto no filesystem do servidor

## Conceito

Recebe via multipart, salva o arquivo em uma pasta no servidor (ex.: `./uploads/funcionarios/`) e armazena no banco apenas o **caminho/nome** do arquivo.

```
[Frontend] ──POST multipart──> [Spring]
                                  │
                                  ├── gera nome único (UUID.jpg)
                                  ├── salva em ./uploads/funcionarios/abc-123.jpg
                                  └── salva caminho no banco

[Frontend] ──GET /usuarios/funcionario/{id}/foto──> [Spring]
                                                       │
                                                       └── lê arquivo do disco e retorna bytes
```

---

## Mudanças no código

### 1. Entidade `Usuario`

```java
@Column(name = "foto_path", length = 500)
private String fotoPath;

@Column(name = "foto_content_type", length = 100)
private String fotoContentType;

public String getFotoPath() { return fotoPath; }
public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }
public String getFotoContentType() { return fotoContentType; }
public void setFotoContentType(String fotoContentType) { this.fotoContentType = fotoContentType; }
```

### 2. `application.properties`

```properties
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=10MB

# Diretório de upload (relativo ao processo do servidor)
app.upload.dir=./uploads/funcionarios
```

### 3. Service — `UsuarioService`

```java
@Value("${app.upload.dir}")
private String uploadDir;

private static final List<String> TIPOS_PERMITIDOS =
        List.of("image/jpeg", "image/png", "image/webp");
private static final long TAMANHO_MAX = 5 * 1024 * 1024;

public void salvarFoto(Long id, MultipartFile file) throws IOException {
    if (file.isEmpty()) {
        throw new IllegalArgumentException("Arquivo vazio");
    }
    if (file.getSize() > TAMANHO_MAX) {
        throw new IllegalArgumentException("Arquivo maior que 5MB");
    }
    if (!TIPOS_PERMITIDOS.contains(file.getContentType())) {
        throw new IllegalArgumentException("Tipo de arquivo não permitido");
    }

    String extensao = obterExtensao(file.getContentType()); // ".jpg", ".png", etc
    String nomeArquivo = UUID.randomUUID() + extensao;
    Path destino = Paths.get(uploadDir, nomeArquivo).toAbsolutePath().normalize();

    // proteção básica contra path traversal
    Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    if (!destino.startsWith(baseDir)) {
        throw new SecurityException("Caminho inválido");
    }

    Files.createDirectories(destino.getParent());
    Files.write(destino, file.getBytes());

    Usuario u = buscarPorId(id);

    // remove a foto antiga se existir
    if (u.getFotoPath() != null) {
        Files.deleteIfExists(Paths.get(u.getFotoPath()));
    }

    u.setFotoPath(destino.toString());
    u.setFotoContentType(file.getContentType());
    usuarioRepository.save(u);
}

public byte[] lerFoto(Long id) throws IOException {
    Usuario u = buscarPorId(id);
    if (u.getFotoPath() == null) return null;
    return Files.readAllBytes(Paths.get(u.getFotoPath()));
}

private String obterExtensao(String contentType) {
    return switch (contentType) {
        case "image/png" -> ".png";
        case "image/webp" -> ".webp";
        default -> ".jpg";
    };
}
```

### 4. Endpoint de upload (`UsuarioController`)

```java
@PostMapping(value = "/funcionario/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<Void> uploadFoto(
        @PathVariable Long id,
        @RequestParam("file") MultipartFile file) throws IOException {
    usuarioService.salvarFoto(id, file);
    return ResponseEntity.noContent().build();
}
```

### 5. Endpoint para servir a foto

```java
@GetMapping("/funcionario/{id}/foto")
public ResponseEntity<byte[]> getFoto(@PathVariable Long id) throws IOException {
    Usuario u = usuarioService.buscarPorId(id);
    byte[] bytes = usuarioService.lerFoto(id);
    if (bytes == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(u.getFotoContentType()))
        .body(bytes);
}
```

### 6. Banco de dados

```sql
ALTER TABLE usuarios ADD COLUMN foto_path VARCHAR(500);
ALTER TABLE usuarios ADD COLUMN foto_content_type VARCHAR(100);
```

### 7. `.gitignore`

```
/uploads/
```

---

## Como o frontend consome

Idêntico à Abordagem B:

```javascript
const formData = new FormData();
formData.append("file", arquivoSelecionado);
await fetch(`/usuarios/funcionario/${id}/foto`, { method: "POST", body: formData });
```

```html
<img src="http://localhost:8080/usuarios/funcionario/123/foto" />
```

---

## Vantagens

- **Banco fica leve** (só caminho de string).
- **Backup do banco rápido**.
- **Performance melhor** que BLOB (filesystem é otimizado para arquivos).
- Sem dependência externa.

## Desvantagens

- **Arquivos somem em deploy efêmero** (Docker sem volume, Heroku, Render free tier).
- **Não escala horizontalmente**: 2 instâncias do backend → cada uma com suas próprias fotos.
- **Backup precisa ser separado** (banco + pasta).
- **Risco de path traversal** se a validação for fraca.
- **Inconsistência possível**: arquivo gravado mas falha o commit no banco (ou vice-versa).

---

## Validações de segurança

1. **Path traversal**: sempre validar que o caminho final está dentro do `baseDir`.
2. **Nome aleatório**: nunca usar `file.getOriginalFilename()` direto — usar UUID.
3. **Allowlist de Content-Type**: idem Abordagem B.
4. **Limite de tamanho** no `application.properties` e no service.
5. **Permissões do diretório**: o usuário do processo Java precisa de write; nada além.
6. **Não servir o diretório como estático** sem auth — sempre passar pelo endpoint controlado.

---

## Quando usar

- Servidor com **disco persistente** garantido (VM dedicada, bare metal, container com volume mapeado).
- Aplicação **single-instance** (um único backend rodando).
- Quer evitar BLOBs no banco mas não quer storage externo.

## Quando NÃO usar

- Deploy em **PaaS efêmero** (Heroku, Render free, Vercel).
- Necessidade de **escalar horizontalmente** (múltiplas instâncias).
- Ambiente **serverless** ou containers sem volume.
- Quando consistência transacional foto+dados é crítica.
