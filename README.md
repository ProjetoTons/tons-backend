# Tons Personalizados — Backend

Backend do sistema de comunicação unificada da **Tons Personalizados**, uma gráfica de sublimação e personalização. O projeto resolve a fragmentação na comunicação entre a empresa e seus clientes, centralizando portfólio, pedidos, notificações e gestão interna em uma plataforma única.

> **Projeto Integrador** — Faculdade, 2026

---

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Arquitetura](#arquitetura)
- [Módulos do Domínio](#módulos-do-domínio)
- [Stack Tecnológica](#stack-tecnológica)
- [Estrutura do Repositório](#estrutura-do-repositório)
- [Pré-requisitos](#pré-requisitos)
- [Como Executar](#como-executar)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Endpoints](#endpoints)
- [Equipe](#equipe)

---

## Sobre o Projeto

A **Tons Personalizados** é uma gráfica especializada em sublimação e personalização (canecas, camisetas, quadros, etc.) que atende via WhatsApp e presencialmente. O sistema visa:

- **Vitrine digital** de produtos com categorias e favoritos
- **Gestão de pedidos** com Kanban, status em tempo real e histórico
- **Notificações automáticas** via WhatsApp (Evolution API) e e-mail
- **Painéis por nível de acesso** (dono, funcionário, cliente)
- **Dashboard de insights** com métricas de produção e vendas
- **Formulário de feedback** pós-entrega

### Requisitos Funcionais

| #   | Requisito                            | Módulo        |
| --- | ------------------------------------ | ------------- |
| 1   | Portfólio / Vitrine de Produtos      | `produtos`    |
| 2   | Integração WhatsApp                  | `notificacao` |
| 3   | Painel de gerenciamento por nível    | `usuarios`    |
| 4   | Painel de status do pedido (cliente) | `pedidos`     |
| 5   | Dashboard de insights                | `pedidos`     |
| 6   | Formulário de feedback               | `notificacao` |
| 7   | Histórico de pedidos                 | `pedidos`     |
| 8   | Favoritos                            | `produtos`    |

---

## Arquitetura

O backend é uma **aplicação monolítica Spring Boot** organizada em **arquitetura por camadas** (Package-by-Layer). Todos os módulos de domínio compartilham o mesmo contexto Spring, o mesmo DataSource e a mesma porta.

```
                         ┌──────────┐
                         │  Client  │
                         │ (React)  │
                         └────┬─────┘
                              │
                              │ HTTP/REST
                              │
                       ┌──────▼───────┐
                       │ Tons Backend │
                       │  (monolito)  │
                       │   :8080      │
                       └──────┬───────┘
                              │
              ┌───────────────┼─────────────────┐
              │               │                 │
       ┌──────▼─────┐  ┌──────▼──────┐  ┌──────▼────────┐
       │   MySQL    │  │   SMTP      │  │ Evolution API │
       │  (única    │  │  (Gmail)    │  │  (WhatsApp)   │
       │   DB)      │  │             │  │               │
       └────────────┘  └─────────────┘  └───────────────┘
```

> **Histórico:** o projeto começou como 4 microsserviços (`usuarios-ms`, `pedidos-ms`, `produtos-ms`, `notificação-ms`) e foi consolidado em um monolito modular para simplificar deploy e operação. Ver [_bmad-output/planning-artifacts/06-arquitetura/plano-migracao-microservicos-para-monolito.md](../_bmad-output/planning-artifacts/06-arquitetura/plano-migracao-microservicos-para-monolito.md).

---

## Módulos do Domínio

Os módulos não correspondem a artefatos Maven separados — são agrupamentos lógicos dentro do monolito.

### `usuarios`

Autenticação, autorização e dados de usuários.

| Domínio       | Responsabilidade                              |
| ------------- | --------------------------------------------- |
| `auth`        | Login, JWT, controle de acesso                |
| `cliente`     | CRUD de clientes, perfil, endereço            |
| `funcionario` | CRUD de funcionários, cargos, níveis          |
| `empresa`     | Cadastro de empresas e endereços vinculados   |

**Roles:** `DONO`, `FUNCIONARIO`, `CLIENTE`.

### `produtos`

Catálogo, categorias e favoritos.

### `pedidos`

Ciclo de vida dos pedidos, Kanban, dashboard, histórico.

**Status:** `RECEBIDO` → `EM_PRODUÇÃO` → `PRONTO` → `ENTREGUE`.

### `notificacao`

Envio de e-mails (SMTP) e integração com WhatsApp via Evolution API. Templates e formulário de feedback.

---

## Stack Tecnológica

| Componente     | Tecnologia                  | Versão     |
| -------------- | --------------------------- | ---------- |
| Linguagem      | Java                        | 21 (LTS)   |
| Framework      | Spring Boot                 | 4.0.3      |
| Web            | Spring Web MVC              | —          |
| Segurança      | Spring Security + JJWT      | 0.11.5     |
| ORM            | Spring Data JPA + Hibernate | —          |
| Banco          | MySQL                       | 8+         |
| Sessão         | Spring Session JDBC         | —          |
| E-mail         | Spring Boot Starter Mail    | —          |
| Documentação   | springdoc-openapi-ui        | 2.8.8      |
| Observabilidade| Spring Boot Actuator        | —          |
| Utilitários    | Lombok                      | —          |
| Build          | Maven Wrapper               | 3.9.x      |
| WhatsApp       | Evolution API               | —          |

---

## Estrutura do Repositório

```
tons-backend/
├── pom.xml                          # POM único do monolito
├── mvnw, mvnw.cmd, .mvn/            # Maven Wrapper
├── azure-pipelines.yml              # CI (build + sync)
├── docs/                            # Guias técnicos
│   └── guia-javamail-springboot.md
├── src/
│   ├── main/
│   │   ├── java/br/com/tonspersonalizados/
│   │   │   ├── TonsApplication.java # Entrypoint Spring Boot
│   │   │   ├── config/              # Security, JWT, Swagger, CORS
│   │   │   ├── controller/          # Endpoints REST (todos os módulos)
│   │   │   ├── service/             # Regras de negócio
│   │   │   ├── repository/          # Spring Data JPA
│   │   │   ├── entity/              # @Entity JPA
│   │   │   ├── dto/                 # Request/Response DTOs
│   │   │   └── exception/           # Exceções de domínio
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/br/com/tonspersonalizados/
└── README.md
```

---

## Pré-requisitos

- **Java 21** (LTS)
- **MySQL 8+** com banco `tonsDb` criado
- **Maven 3.9+** (opcional — o wrapper `./mvnw` já vai instalar quando houver internet)

---

## Como Executar

### 1. Subir o MySQL

Crie o banco usado pela aplicação:

```sql
CREATE DATABASE tonsDb;
```

### 2. Configurar credenciais

Edite [src/main/resources/application.properties](src/main/resources/application.properties) (ou exporte variáveis de ambiente equivalentes) ajustando:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `jwt.secret` (mínimo 32 caracteres)
- `spring.mail.username` / `spring.mail.password` (se for usar e-mail)

> ⚠️ **Não commit credenciais em produção.** Mover para variáveis de ambiente é recomendado.

### 3. Build e execução

```bash
./mvnw clean compile          # compila
./mvnw test                   # roda testes
./mvnw spring-boot:run        # sobe a aplicação em :8080
```

Ou empacotar e executar o JAR:

```bash
./mvnw clean package -DskipTests
java -jar target/tons-backend-0.0.1-SNAPSHOT.jar
```

### 4. Documentação OpenAPI / Swagger UI

Após subir, acesse:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## Variáveis de Ambiente

Configurações principais em [src/main/resources/application.properties](src/main/resources/application.properties):

| Chave                          | Descrição                              | Padrão                              |
| ------------------------------ | -------------------------------------- | ----------------------------------- |
| `spring.datasource.url`        | URL JDBC do MySQL                      | `jdbc:mysql://localhost:3306/tonsDb`|
| `spring.datasource.username`   | Usuário do banco                       | `root`                              |
| `spring.datasource.password`   | Senha do banco                         | —                                   |
| `spring.jpa.hibernate.ddl-auto`| Estratégia DDL                         | `update`                            |
| `jwt.validity`                 | Expiração do token (ms)                | `3600000` (1h)                      |
| `jwt.secret`                   | Chave de assinatura JWT                | —                                   |
| `spring.mail.host`             | Host SMTP                              | `smtp.gmail.com`                    |
| `spring.mail.port`             | Porta SMTP                             | `587`                               |
| `spring.mail.username`         | Usuário SMTP                           | —                                   |
| `spring.mail.password`         | Senha de aplicativo SMTP               | —                                   |

---

## Endpoints

URLs preservadas dos microsserviços originais — agora servidas pelo monolito em `:8080`.

### Usuários (`/usuarios`)

| Método | Rota                          | Descrição                  |
| ------ | ----------------------------- | -------------------------- |
| POST   | `/usuarios`                   | Cadastrar usuário          |
| POST   | `/usuarios/login`             | Login (retorna JWT)        |
| POST   | `/usuarios/funcionario`       | Cadastrar funcionário      |
| GET    | `/usuarios/{nome}`            | Buscar por nome            |
| PUT    | `/usuarios/{id}`              | Atualizar usuário          |
| DELETE | `/usuarios/{id}`              | Remover usuário            |
| POST   | `/usuarios/{id}/endereco`     | Cadastrar endereço         |
| GET    | `/usuarios/{id}/endereco`     | Buscar endereço            |
| PUT    | `/usuarios/{id}/endereco`     | Atualizar endereço         |
| DELETE | `/usuarios/{id}/endereco`     | Remover endereço           |

### Empresas (`/empresas`)

| Método | Rota                         | Descrição               |
| ------ | ---------------------------- | ----------------------- |
| POST   | `/empresas`                  | Cadastrar empresa       |
| GET    | `/empresas`                  | Listar empresas         |
| POST   | `/empresas/{id}/endereco`    | Cadastrar endereço      |
| GET    | `/empresas/{id}/endereco`    | Buscar endereço         |
| PUT    | `/empresas/{id}/endereco`    | Atualizar endereço      |
| DELETE | `/empresas/{id}/endereco`    | Remover endereço        |

### Notificações (`/notificacao`)

| Método | Rota                          | Descrição           |
| ------ | ----------------------------- | ------------------- |
| POST   | `/notificacao/enviar-email`   | Enviar e-mail       |

> Endpoints de produtos e pedidos serão implementados nas próximas sprints.

---

## CI/CD

Pipeline em [azure-pipelines.yml](azure-pipelines.yml):

1. Instala JDK 21
2. Roda `./mvnw clean verify`
3. Sincroniza repositório com Azure DevOps

---

## Equipe

| Nome                            | Papel           |
| ------------------------------- | --------------- |
| Dennis Wilson Serrano Medrano   | Tech Lead       |
| Davi                            | Desenvolvedor   |
| Gustavo                         | Desenvolvedor   |
| Rafael                          | Desenvolvedor   |

---

## Licença

Projeto acadêmico — Projeto Integrador 2026.
