# Abordagem B — Foto como BLOB no banco de dados

## Conceito

O frontend envia o arquivo via `multipart/form-data`. O Spring recebe como `MultipartFile`, converte para `byte[]` e salva direto na tabela `usuarios` como **BLOB**.

```
[Frontend] ──POST multipart {nome, email, foto: <arquivo>}──> [Spring]
                                                                  │
                                                                  ├── lê bytes do arquivo
                                                                  ├── valida tamanho/tipo
                                                                  └── salva no banco (LONGBLOB)

[Frontend] ──GET /usuarios/funcionario/{id}/foto──> [Spring]
                                                       │
                                                       └── retorna bytes + Content-Type
```

---

## Mudanças no código

### 1. Entidade `Usuario`

```java
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;

@Lob
@Basic(fetch = FetchType.LAZY)  // não carrega o BLOB em SELECTs comuns
@Column(name = "foto", columnDefinition = "LONGBLOB")
@JsonIgnore  // não serializar bytes em JSON
private byte[] foto;

@Column(name = "foto_content_type", length = 100)
private String fotoContentType;

public byte[] getFoto() { return foto; }
public void setFoto(byte[] foto) { this.foto = foto; }
public String getFotoContentType() { return fotoContentType; }
public void setFotoContentType(String fotoContentType) { this.fotoContentType = fotoContentType; }
```

### 2. `application.properties`

```properties
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=10MB
```

### 3. Endpoint de upload (`UsuarioController`)

```java
@PostMapping(value = "/funcionario/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@Operation(summary = "Upload da foto de um funcionário")
public ResponseEntity<Void> uploadFoto(
        @PathVariable Long id,
        @RequestParam("file") MultipartFile file) throws IOException {
    usuarioService.salvarFoto(id, file);
    return ResponseEntity.noContent().build();
}
```

### 4. Endpoint para servir a foto

```java
@GetMapping("/funcionario/{id}/foto")
@Operation(summary = "Retorna a foto de um funcionário")
public ResponseEntity<byte[]> getFoto(@PathVariable Long id) {
    Usuario u = usuarioService.buscarPorId(id);
    if (u.getFoto() == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(u.getFotoContentType()))
        .body(u.getFoto());
}
```

### 5. Service — `UsuarioService`

```java
private static final List<String> TIPOS_PERMITIDOS =
        List.of("image/jpeg", "image/png", "image/webp");
private static final long TAMANHO_MAX = 5 * 1024 * 1024; // 5 MB

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

    Usuario u = buscarPorId(id);
    u.setFoto(file.getBytes());
    u.setFotoContentType(file.getContentType());
    usuarioRepository.save(u);
}
```

### 6. Banco de dados

Caso o `ddl-auto` não esteja criando automaticamente:

```sql
ALTER TABLE usuarios ADD COLUMN foto LONGBLOB;
ALTER TABLE usuarios ADD COLUMN foto_content_type VARCHAR(100);
```

---

## Como o frontend consome

### Upload

```javascript
const formData = new FormData();
formData.append("file", arquivoSelecionado);

await fetch(`/usuarios/funcionario/${id}/foto`, {
    method: "POST",
    body: formData,
    headers: { "Authorization": `Bearer ${token}` }
});
```

### Exibir

```html
<img src="http://localhost:8080/usuarios/funcionario/123/foto" />
```

---

## Vantagens

- Tudo dentro do Spring + banco — **zero infra externa**.
- Backup do banco já inclui as imagens.
- Transacional: se falhar o salvamento, nada é gravado.
- Funciona offline e em qualquer máquina.

## Desvantagens

- **Banco infla rápido**: 100 funcionários × foto de 2MB = 200MB no banco.
- **Listagens lentas** se não usar `FetchType.LAZY` no campo BLOB.
- **Sem CDN**: cada visualização da foto bate no servidor.
- Backup/restore do banco fica pesado.
- Não recomendado para >1000 imagens ou imagens grandes.

---

## Validações de segurança

1. **Limite de tamanho** no `application.properties` (`max-file-size`) e no service.
2. **Allowlist de Content-Type**: aceitar apenas `image/jpeg`, `image/png`, `image/webp`.
3. **Validar magic bytes** (opcional avançado): biblioteca como `Apache Tika` para evitar arquivo malicioso renomeado para `.jpg`.
4. **Autenticação obrigatória** no endpoint de upload (não deixar em `URLS_PERMITIDAS`).
5. **Autorização**: só admin/RH pode fazer upload de foto de funcionário.

---

## Quando usar

- Projeto acadêmico / TCC / PI — apresentação simples.
- Aplicação interna com poucos usuários (< 100).
- Ambiente sem internet ou sem orçamento para storage externo.
- Necessidade de transação atômica (foto + dados gravados juntos).

## Quando NÃO usar

- Aplicações com >1000 usuários ou alto tráfego.
- Imagens grandes (>5MB) ou em alta resolução.
- Deploys em múltiplas instâncias com banco compartilhado pequeno.
