# Corte Brabo — Sistema de Gestão para Barbearia

Sistema completo de gestão para barbearia, com painel web para funcionários cadastrarem clientes, marcarem agendamentos, gerenciarem serviços e equipe.

> **Backend** (este repositório): Java 17 + Spring Boot 3 + MySQL
> **Frontend**: React + Vite + TypeScript + TailwindCSS ([repositório separado](#frontend))

---

## Sumário

- [Visão geral](#visão-geral)
- [Stack](#stack)
- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Como rodar localmente](#como-rodar-localmente)
- [Banco de dados e migrations](#banco-de-dados-e-migrations)
- [Configuração](#configuração)
- [Endpoints da API](#endpoints-da-api)
- [Permissões por cargo](#permissões-por-cargo)
- [Frontend](#frontend)
- [Roadmap](#roadmap)
- [Licença](#licença)

---

## Visão geral

O Corte Brabo nasceu de uma necessidade real: dar pro pessoal da barbearia uma ferramenta simples pra:

- Cadastrar cliente em 5 segundos no balcão (só nome e telefone, sem senha — cliente não loga)
- Marcar agendamento amarrando cliente + barbeiro(s) + serviço(s) + horário
- Acompanhar a fila do dia com status (pendente → confirmado → concluído / cancelado)
- Manter a tabela de preços atualizada e visível

Não é um sistema de auto-agendamento via app — é uma ferramenta de **gestão interna**. Só funcionário loga, cliente é apenas um cadastro.

---

## Stack

| Camada | Tecnologia |
|---|---|
| **Linguagem** | Java 17 |
| **Framework** | Spring Boot 3.3 |
| **Persistência** | Spring Data JPA + Hibernate 6 |
| **Banco** | MySQL 8+ |
| **Migrations** | Flyway 10 |
| **Segurança** | Spring Security + JWT (JJWT 0.12) |
| **Mapping** | MapStruct 1.6 |
| **Boilerplate** | Lombok |
| **Build** | Maven |

---

## Funcionalidades

### Autenticação e usuários
- Login com JWT (telefone + senha)
- Cliente **não loga** — bloqueado explicitamente no `UserDetailsService`
- Hash de senha com BCrypt
- Bootstrap automático de admin no primeiro boot (config via properties)

### Cadastros
- **Clientes**: nome + telefone (telefone único)
- **Funcionários**: nome + telefone + senha + cargo (BARBER ou ADM)
- **Serviços**: nome + preço + descrição

### Agendamentos
- Vínculo com cliente, múltiplos barbeiros e múltiplos serviços
- Status: `PENDENTE`, `CONFIRMADO`, `CONCLUIDO`, `CANCELADO`
- Cliente só pode ter **1 agendamento ativo** (pendente/confirmado) por vez
- Mudança de status via endpoint dedicado (PATCH)
- Audit timestamps automáticos (`createdAt` / `updatedAt`)

### Tratamento de erros
- Handler global de exceções com respostas HTTP apropriadas:
  - `404` Not Found
  - `400` Bad Request (validação de payload, com map de erros por campo)
  - `401` Unauthorized (credenciais inválidas)
  - `403` Forbidden (sem permissão de role)
  - `409` Conflict (telefone duplicado, regras de negócio)

---

## Arquitetura

```
┌──────────────────────────────────────────────────────────┐
│                     Cliente HTTP (React)                  │
└────────────────────────┬─────────────────────────────────┘
                         │ JWT no header
                         ▼
┌──────────────────────────────────────────────────────────┐
│                     SecurityConfig                        │
│  ┌────────────────────────────────────────────────────┐  │
│  │  JwtAuthenticationFilter (extrai e valida token)   │  │
│  └────────────────────────────────────────────────────┘  │
└────────────────────────┬─────────────────────────────────┘
                         ▼
┌──────────────────────────────────────────────────────────┐
│                     Controllers (REST)                    │
│   @PreAuthorize por endpoint e por método                 │
└────────────────────────┬─────────────────────────────────┘
                         ▼
┌──────────────────────────────────────────────────────────┐
│                     Services (regras)                     │
│   Validações de domínio, checks de role no service-side   │
└────────────────────────┬─────────────────────────────────┘
                         ▼
┌──────────────────────────────────────────────────────────┐
│   Repositories (Spring Data JPA) ──► Hibernate ──► MySQL │
└──────────────────────────────────────────────────────────┘
```

**Segurança em 3 camadas:**
1. `SecurityConfig` valida o JWT em toda request
2. `@PreAuthorize` por método nos controllers (`hasRole('ADM')`, etc)
3. Lógica de domínio no service (ex: barbeiro só pode mudar status do PRÓPRIO agendamento)

---

## Estrutura do projeto

```
src/main/java/com/corteBrabo/barbershopApi/
├── BarbershopApiApplication.java
├── config/
│   ├── AdminSeeder.java          # cria admin no primeiro boot
│   ├── CorsConfig.java
│   ├── SecurityConfig.java       # JWT, roles, cadeia de filtros
│   └── ServiceSeeder.java        # popula serviços padrão
├── controller/
│   ├── AuthController.java       # POST /auth/login
│   ├── ScheduleController.java   # CRUD de agendamentos
│   ├── ServiceController.java    # CRUD de serviços
│   └── UserController.java       # CRUD de pessoas (clientes + staff)
├── database/
│   ├── model/                    # entidades JPA
│   │   ├── Schedule.java
│   │   ├── ScheduleStatus.java
│   │   ├── Service.java
│   │   ├── User.java             # implements UserDetails
│   │   └── UserRole.java
│   └── repository/               # interfaces Spring Data
├── dto/                          # DTOs de request/response
├── exception/
│   └── NotFoundException.java
├── handler/
│   └── GlobalExceptionHandler.java
├── mapper/                       # interfaces MapStruct
├── security/
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtService.java
└── service/                      # regras de negócio
    ├── AuthService.java
    ├── ScheduleService.java
    ├── ServiceService.java
    └── UserService.java

src/main/resources/
├── application.properties
└── db/migration/                 # migrations Flyway
    ├── V1__init_schema.sql
    ├── V2__alter_agendamentoIdName.sql
    ├── V3__rename_scheduleId_to_snake_case.sql
    ├── V4__add_unique_telefone.sql
    ├── V5__schedule_refactor_and_audit.sql
    └── V6__add_user_password.sql
```

---

## Como rodar localmente

### Pré-requisitos

- **Java 17+** (`java -version`)
- **Maven 3.8+** (ou use o `mvnw` que vai junto)
- **MySQL 8+** rodando local na porta `3306`
- Banco vazio chamado `barbershop` (o Flyway cria o schema)

### Setup do banco

No MySQL:

```sql
CREATE DATABASE barbershop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Atualize as credenciais no `application.properties` se forem diferentes das defaults.

### Subir a aplicação

```bash
./mvnw spring-boot:run
```

Sobe em **http://localhost:8080**.

No primeiro boot, o `AdminSeeder` cria automaticamente um admin com as credenciais configuradas em `app.admin.bootstrap-*` (defaults: telefone `11000000000`, senha `admin123`).

**No log você vai ver:**

```
==================================================
BOOTSTRAP ADMIN CRIADO
  Telefone: 11000000000
  Senha:    admin123
==================================================
```

Use essas credenciais pra logar no frontend.

### Build de produção

```bash
./mvnw clean package
java -jar target/barbershopApi-0.0.1-SNAPSHOT.jar
```

---

## Banco de dados e migrations

Todas as migrations ficam em `src/main/resources/db/migration/` e rodam automaticamente no boot via Flyway.

| Versão | O que faz |
|---|---|
| **V1** | Cria `user`, `service`, `schedule`, `schedule_barbers`, `schedule_services` |
| **V2** | Renomeia `agendamento_id` → `scheduleId` |
| **V3** | Padroniza `scheduleId` → `schedule_id` (snake_case) |
| **V4** | Adiciona constraint `UNIQUE` em `user.telefone` |
| **V5** | Permite múltiplos agendamentos por cliente + audit timestamps |
| **V6** | Adiciona `user.password` e `user.updated_at` |

### Modelo de dados

```
user (id, name, telefone UQ, password, role, date, updated_at)
  │
  ├──< schedule (scheduleId, client_id FK, status, date, created_at, updated_at)
  │           │
  │           ├──< schedule_barbers (schedule_id, barber_id) ──> user
  │           └──< schedule_services (schedule_id, service_id) ──> service
  │
  service (service_id, service_name, price, description)
```

---

## Configuração

Toda configuração fica em `src/main/resources/application.properties`. Variáveis importantes:

```properties
# Banco
spring.datasource.url=jdbc:mysql://localhost:3306/barbershop
spring.datasource.username=root
spring.datasource.password=hawaiian11

# JWT
app.jwt.secret=<chave secreta de >= 32 bytes>
app.jwt.expiration-ms=86400000

# CORS (domínios do frontend autorizados)
app.cors.allowed-origins=http://localhost:5173,http://localhost:3000

# Bootstrap admin (cria no primeiro boot; remova após criar admins pela UI)
app.admin.bootstrap-telefone=11000000000
app.admin.bootstrap-password=admin123
app.admin.bootstrap-name=Admin
```

> **Atenção:** em produção, mova segredos pra variáveis de ambiente.

---

## Endpoints da API

### Auth (público)
| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/auth/login` | Login. Retorna `{ token, userId, name, role }` |

### Usuários (autenticado)
| Método | Rota | Cargo |
|---|---|---|
| `GET` | `/user/me` | qualquer logado |
| `POST` | `/user/client` | ADM, BARBER |
| `POST` | `/user/staff` | ADM |
| `GET` | `/user?role=BARBER` | ADM, BARBER |
| `GET` | `/user/{id}` | ADM, BARBER |
| `PUT` | `/user/{id}` | ADM ou próprio |
| `DELETE` | `/user/{id}` | ADM |

### Serviços (catálogo)
| Método | Rota | Cargo |
|---|---|---|
| `GET` | `/service` | público |
| `GET` | `/service/{id}` | público |
| `POST` | `/service` | ADM |
| `PUT` | `/service/{id}` | ADM |
| `DELETE` | `/service/{id}` | ADM |

### Agendamentos
| Método | Rota | Cargo |
|---|---|---|
| `POST` | `/schedule` | ADM, BARBER |
| `GET` | `/schedule?status=PENDENTE` | qualquer logado (lista filtrada por role) |
| `GET` | `/schedule/{id}` | ADM ou barbeiro do agendamento |
| `PUT` | `/schedule/{id}` | ADM |
| `PATCH` | `/schedule/{id}/status?status=CONFIRMADO` | ADM ou barbeiro do agendamento |
| `DELETE` | `/schedule/{id}` | ADM |

### Headers de autenticação

```
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

---

## Permissões por cargo

| Ação | ADM | BARBER | CLIENT |
|---|:---:|:---:|:---:|
| Login no sistema | ✅ | ✅ | ❌ (não loga) |
| Cadastrar cliente | ✅ | ✅ | — |
| Cadastrar funcionário | ✅ | ❌ | — |
| Listar pessoas | ✅ | ✅ | — |
| Editar próprio perfil | ✅ | ✅ | — |
| Editar/deletar qualquer pessoa | ✅ | ❌ | — |
| CRUD de serviços (preços) | ✅ | só visualizar | só visualizar |
| Criar agendamento | ✅ | ✅ | — |
| Ver agendamentos | todos | apenas os próprios | — |
| Mudar status | ✅ | só os próprios | — |
| Deletar agendamento | ✅ | ❌ | — |

---

## Frontend

O cliente web fica em repositório separado:

**[corte-brabo-frontend](https://github.com/SEU-USUARIO/corte-brabo-frontend)** *(ajustar URL)*

Stack: React 18 + Vite + TypeScript + TailwindCSS.

Rodar localmente:

```bash
cd ../frontendBarberShop
npm install
npm run dev
```

Abre em **http://localhost:5173**.

---

## Roadmap

Próximas melhorias planejadas:

- [ ] Refresh tokens (hoje token expira em 24h)
- [ ] Validação de conflito de horário (impedir 2 agendamentos pro mesmo barbeiro no mesmo horário)
- [ ] Campo de duração nos serviços (pra calcular conflito de horário automaticamente)
- [ ] Paginação nas listagens
- [ ] Histórico/auditoria de mudanças de status
- [ ] Notificação pro cliente (SMS/WhatsApp) na confirmação
- [ ] Relatórios financeiros (faturamento por barbeiro / por período)
- [ ] Testes automatizados (unitários + integração)
- [ ] Dockerfile + docker-compose
- [ ] Pipeline de CI/CD

---

## Licença

Projeto pessoal. Use à vontade.
