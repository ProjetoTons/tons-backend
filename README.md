# Tons Personalizados — Backend

Backend do sistema de comunicação unificada da **Tons Personalizados**, uma gráfica de sublimação e personalização. O projeto resolve a fragmentação na comunicação entre a empresa e seus clientes, centralizando portfólio, pedidos, notificações e gestão interna em uma plataforma única.

> **Projeto Integrador** — Faculdade, 2026

---

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Arquitetura](#arquitetura)
- [Microserviços](#microserviços)
- [Stack Tecnológica](#stack-tecnológica)
- [Estrutura do Repositório](#estrutura-do-repositório)
- [Pré-requisitos](#pré-requisitos)
- [Como Executar](#como-executar)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Endpoints por Serviço](#endpoints-por-serviço)
- [Equipe](#equipe)

---

## Sobre o Projeto

A **Tons Personalizados** é uma gráfica especializada em sublimação e personalização (canecas, camisetas, quadros, etc.) que atende via WhatsApp e presencialmente. O sistema visa:

- **Vitrine digital** de produtos com categorias e favoritos
- **Gestão de pedidos** com Kanban, status em tempo real e histórico
- **Notificações automáticas** via WhatsApp (Evolution API)
- **Painéis por nível de acesso** (dono, funcionário, cliente)
- **Dashboard de insights** com métricas de produção e vendas
- **Formulário de feedback** pós-entrega

### Requisitos Funcionais

| # | Requisito | Microserviço |
|---|-----------|-------------|
| 1 | Portfólio / Vitrine de Produtos | `produtos-ms` |
| 2 | Integração WhatsApp | `notificação-ms` |
| 3 | Painel de gerenciamento por área/nível | `usuarios-ms` |
| 4 | Painel de status do pedido (cliente) | `pedidos-ms` |
| 5 | Dashboard de insights | `pedidos-ms` |
| 6 | Formulário de feedback | `notificação-ms` |
| 7 | Histórico de pedidos | `pedidos-ms` |
| 8 | Favoritos | `produtos-ms` |

---

## Arquitetura

O backend segue uma arquitetura de **microserviços**, onde cada serviço é uma aplicação Spring Boot independente com seu próprio contexto de domínio.

```
                         ┌──────────┐
                         │  Client  │
                         │ (React)  │
                         └────┬─────┘
                              │
                         ┌────▼─────┐
                         │   API    │
                         │ Gateway  │
                         └────┬─────┘
                              │
            ┌─────────────────┼─────────────────┐
            │                 │                  │
   ┌────────▼──────┐  ┌──────▼───────┐  ┌──────▼────────┐
   │  usuarios-ms  │  │ produtos-ms  │  │  pedidos-ms   │
   │   :8081       │  │   :8082      │  │   :8083       │
   └────────┬──────┘  └──────┬───────┘  └──────┬────────┘
            │                │                  │
            └────────────────┼──────────────────┘
                             │
                      ┌──────▼───────┐     ┌────────────────┐
                      │    MySQL     │     │ notificação-ms │
                      │   (Banco)   │     │     :8084       │
                      └──────────────┘     └───────┬────────┘
                                                   │
                                           ┌───────▼────────┐
                                           │ Evolution API  │
                                           │  (WhatsApp)    │
                                           └────────────────┘
```

### Comunicação entre serviços

- **Síncrona:** HTTP/REST interno entre microserviços quando necessário
- **Roteamento:** API Gateway — roteia por prefixo de URL para cada serviço

---

## Microserviços

### 1. `usuarios-ms` — Porta 8081

Gerencia autenticação, autorização e dados de usuários.

| Domínio | Responsabilidade |
|---------|-----------------|
| `auth` | Login, registro, JWT, refresh token |
| `cliente` | CRUD de clientes, perfil, endereço |
| `funcionario` | CRUD de funcionários, cargos, níveis de acesso |

**Roles:** `DONO`, `FUNCIONARIO`, `CLIENTE`

### 2. `produtos-ms` — Porta 8082

Gerencia o catálogo de produtos, categorias e favoritos.

| Domínio | Responsabilidade |
|---------|-----------------|
| `catalogo` | CRUD de produtos, imagens, preços, vitrine pública |
| `categoria` | Categorias e subcategorias (canecas, camisetas, quadros, etc.) |
| `favorito` | Lista de favoritos por cliente |
| `upload` | Upload e armazenamento de imagens |

### 3. `pedidos-ms` — Porta 8083

Gerencia todo o ciclo de vida dos pedidos.

| Domínio | Responsabilidade |
|---------|-----------------|
| `order` | CRUD de pedidos, mudança de status |
| `kanban` | Visualização Kanban para gestão interna |
| `dashboard` | Métricas, gráficos, insights de produção e vendas |
| `historico` | Histórico de pedidos por cliente e por período |
| `calendario` | Calendário de entregas e produção |
| `ordemservico` | Ordem de serviço digital (impressão para produção) |

**Status do pedido:** `RECEBIDO` → `EM_PRODUÇÃO` → `PRONTO` → `ENTREGUE`

### 4. `notificação-ms` — Porta 8084

Gerencia notificações e comunicação com o cliente.

| Domínio | Responsabilidade |
|---------|-----------------|
| `whatsapp` | Integração com Evolution API, envio de mensagens |
| `template` | Templates de mensagens (confirmação, status, promoção) |
| `fila` | Fila de mensagens para envio assíncrono |
| `feedback` | Formulário de satisfação pós-entrega |

---

## Stack Tecnológica

| Componente | Tecnologia | Versão |
|------------|-----------|--------|
| Linguagem | Java | 21 (LTS) |
| Framework | Spring Boot | 4.0.3 |
| ORM | Spring Data JPA + Hibernate | — |
| Banco de Dados | MySQL | 8+ |
| Sessão | Spring Session JDBC | — |
| Utilitários | Lombok | — |
| Build | Maven | — |
| API Gateway | Spring Cloud Gateway | — |
| WhatsApp | Evolution API | — |
| Containerização | Docker + Docker Compose | — |

---

## Estrutura do Repositório

```
tons-backend/
├── api-gateway/                    # API Gateway (Spring Cloud Gateway)
│
├── usuarios-ms/                    # Microserviço de Usuários (:8081)
│   └── src/main/java/br/com/tonspersonalizados/usuarios_ms/
│       ├── config/                 # Configurações (Security, CORS, etc.)
│       ├── auth/                   # Autenticação e autorização
│       │   ├── controller/
│       │   ├── service/
│       │   └── dto/
│       ├── cliente/                # Gestão de clientes
│       │   ├── controller/
│       │   ├── service/
│       │   ├── repository/
│       │   ├── model/
│       │   └── dto/
│       └── funcionario/            # Gestão de funcionários
│           ├── controller/
│           ├── service/
│           ├── repository/
│           ├── model/
│           └── dto/
│
├── produtos-ms/                    # Microserviço de Produtos (:8082)
│   └── src/main/java/br/com/tonspersonalizados/produtos_ms/
│       ├── catalogo/               # Catálogo e vitrine
│       ├── categoria/              # Categorias de produtos
│       ├── favorito/               # Favoritos do cliente
│       └── upload/                 # Upload de imagens
│
├── pedidos-ms/                     # Microserviço de Pedidos (:8083)
│   └── src/main/java/br/com/tonspersonalizados/pedidos_ms/
│       ├── order/                  # CRUD de pedidos
│       ├── kanban/                 # Painel Kanban
│       ├── dashboard/              # Métricas e insights
│       ├── historico/              # Histórico de pedidos
│       ├── calendario/             # Calendário de entregas
│       └── ordemservico/           # Ordem de serviço digital
│
├── notificação-ms/                 # Microserviço de Notificações (:8084)
│   └── src/main/java/br/com/tonspersonalizados/notificacao_ms/
│       ├── whatsapp/               # Integração Evolution API
│       ├── template/               # Templates de mensagem
│       ├── fila/                   # Fila de envio assíncrono
│       └── feedback/               # Feedback pós-entrega
│
├── docker-compose.yml              # Orquestração dos serviços
└── README.md
```

> **Nota:** Cada domínio segue o padrão `controller/ → service/ → repository/ → model/ → dto/`, sendo criados conforme a implementação avançar.

---

## Pré-requisitos

- **Java 21** (LTS)
- **Maven 3.9+**
- **MySQL 8+**
- **Docker** e **Docker Compose** (opcional, para ambiente containerizado)

---

## Como Executar

### Executar um serviço individualmente

```bash
cd usuarios-ms
./mvnw spring-boot:run
```

### Executar todos com Docker Compose

```bash
docker-compose up --build
```

### Build de todos os serviços

```bash
# Em cada diretório de serviço:
./mvnw clean package -DskipTests
```

---

## Variáveis de Ambiente

Cada serviço possui seu `application.properties` em `src/main/resources/`. Configurações típicas:

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `spring.datasource.url` | URL do banco MySQL | `jdbc:mysql://localhost:3306/tons_usuarios` |
| `spring.datasource.username` | Usuário do banco | `root` |
| `spring.datasource.password` | Senha do banco | `tons123` |
| `server.port` | Porta do serviço | `8081` |

### Bancos por serviço

| Serviço | Banco | Porta |
|---------|-------|-------|
| `usuarios-ms` | `tons_usuarios` | 8081 |
| `produtos-ms` | `tons_produtos` | 8082 |
| `pedidos-ms` | `tons_pedidos` | 8083 |
| `notificação-ms` | `tons_notificacoes` | 8084 |

---

## Endpoints por Serviço

> Endpoints planejados — serão implementados durante as sprints.

### usuarios-ms (`/api/users`)
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/users/auth/login` | Login |
| POST | `/api/users/auth/register` | Registro |
| GET | `/api/users/clientes` | Listar clientes |
| GET | `/api/users/clientes/{id}` | Buscar cliente |
| GET | `/api/users/funcionarios` | Listar funcionários |

### produtos-ms (`/api/products`)
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/products/catalogo` | Listar vitrine |
| GET | `/api/products/catalogo/{id}` | Detalhe do produto |
| GET | `/api/products/categorias` | Listar categorias |
| POST | `/api/products/favoritos` | Adicionar favorito |
| GET | `/api/products/favoritos` | Listar favoritos |

### pedidos-ms (`/api/orders`)
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/orders` | Criar pedido |
| GET | `/api/orders/{id}` | Detalhe do pedido |
| PATCH | `/api/orders/{id}/status` | Atualizar status |
| GET | `/api/orders/kanban` | Visualização Kanban |
| GET | `/api/orders/dashboard` | Métricas |
| GET | `/api/orders/historico` | Histórico |

### notificação-ms (`/api/notifications`)
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/notifications/send` | Enviar notificação |
| GET | `/api/notifications/templates` | Listar templates |
| POST | `/api/notifications/feedback` | Enviar feedback |

---

## Equipe

| Nome | Papel |
|------|-------|
| Dennis Wilson Serrano Medrano | Tech Lead |
| Davi | Desenvolvedor |
| Gustavo | Desenvolvedor |
| Rafael | Desenvolvedor |

---

## Licença

Projeto acadêmico — Projeto Integrador 2026.
