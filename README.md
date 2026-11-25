# Carteira de Ações

Aplicação web para acompanhar carteiras de ações, posições, cotações e operações de compra e venda. O projeto é dividido em uma API Spring Boot e uma interface Angular.

## Visão geral

```text
Navegador → Angular (frontend) → API REST (backend) → PostgreSQL
                                      ├→ Brapi / Twelve Data (ações)
                                      ├→ PTAX / Banco Central (USD/BRL)
                                      └→ BrasilAPI / CVM (corretoras)
```

- Usuários autenticados criam carteiras, consultam posições e registram compras e vendas.
- Administradores gerenciam usuários, corretoras e o catálogo de ações.
- A API é a fonte de verdade dos cálculos financeiros; o frontend apenas apresenta os dados recebidos.
- A autenticação usa JWT e os papéis disponíveis são `USER` e `ADMIN`.

## Estrutura

| Diretório | Responsabilidade |
| --- | --- |
| [`Carteira-acoes-backend`](Carteira-acoes-backend/) | API REST, regras de negócio, persistência, autenticação e integrações externas |
| [`Carteira-acoes-frontend`](Carteira-acoes-frontend/) | Aplicação Angular, páginas, serviços HTTP, guards e componentes compartilhados |
| [`deploy`](deploy/) | Docker Compose para subir frontend, backend e PostgreSQL |

## Pré-requisitos

- JDK 21;
- Node.js 22 e npm;
- PostgreSQL 17 para execução local do backend;
- Docker e Docker Compose, caso use o ambiente conteinerizado.

## Executar localmente

### Backend

```bash
cd Carteira-acoes-backend
cp .env.example .env
# preencha DB_PASSWORD, JWT_SECRET, APP_ADMIN_SENHA e as chaves dos provedores
set -a && source .env && set +a
./mvnw spring-boot:run
```

A API fica em `http://localhost:8080`. A documentação interativa está em [`/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html) e o contrato OpenAPI em [`/v3/api-docs`](http://localhost:8080/v3/api-docs).

### Frontend

Em outro terminal:

```bash
cd Carteira-acoes-frontend
npm ci
npm start
```

A interface fica em `http://localhost:4200`. No desenvolvimento, as chamadas usam `/api`; configure o proxy do ambiente de desenvolvimento ou execute com Docker Compose para que elas alcancem o backend.

### Docker Compose

Com `.env` preenchido em `Carteira-acoes-backend` e `deploy/.env` configurado:

```bash
cd deploy
docker compose up --build -d
```

A aplicação fica em `http://localhost` e a API em `http://localhost:8080`. Para parar sem remover o banco, use `docker compose down`; `docker compose down -v` remove o volume do PostgreSQL.

## Testes e build

```bash
cd Carteira-acoes-backend && ./mvnw clean verify
cd ../Carteira-acoes-frontend && npm test
cd ../Carteira-acoes-frontend && npm run build
```

O backend usa H2 e o perfil `test`; `clean verify` também gera o relatório JaCoCo e valida os mínimos configurados no Maven. O frontend usa Vitest por meio do Angular CLI.

## Fluxo de teste no Postman

Crie um environment chamado `Carteira Ações - local`:

```text
baseUrl = http://localhost:8080/api
email = ana.postman@example.com
senha = SenhaPostmanA1
token =
usuarioId =
corretoraId =
carteiraId =
acaoId =
operacaoId =
```

Nas requisições protegidas, use a aba **Authorization** com tipo **Bearer Token** e valor `{{token}}`. Execute nesta ordem.

### 1. Cadastrar e autenticar

`POST {{baseUrl}}/auth/cadastro`

```json
{"nome":"Ana Postman","email":"{{email}}","senha":"{{senha}}"}
```

Na aba **Tests**:

```javascript
pm.test('usuário criado', () => pm.response.to.have.status(201));
pm.environment.set('usuarioId', pm.response.json().id);
```

`POST {{baseUrl}}/auth/login`

```json
{"login":"{{email}}","password":"{{senha}}"}
```

Na aba **Tests** do login:

```javascript
pm.test('login realizado', () => pm.response.to.have.status(200));
pm.environment.set('token', pm.response.json().token);
```

Depois, valide `GET {{baseUrl}}/auth/me` com o Bearer Token.

### 2. Criar dados de apoio

Criar corretora (ADMIN): `POST {{baseUrl}}/corretoras`

```json
{"cnpj":"02332886000104"}
```

```javascript
pm.test('corretora criada', () => pm.response.to.have.status(201));
pm.environment.set('corretoraId', pm.response.json().id);
```

Criar carteira: `POST {{baseUrl}}/carteiras`

```json
{"nome":"Longo prazo","usuarioId":{{usuarioId}},"corretoraId":{{corretoraId}}}
```

Guarde o `id` retornado em `carteiraId`. O usuário da carteira deve ser o autenticado.

Criar ação (ADMIN): `POST {{baseUrl}}/acoes`

```json
{"ticker":"PETR4","mercado":"BRASIL"}
```

Essa etapa depende de `BRAPI_*` ou `TWELVE_DATA_*`; guarde o `id` retornado em `acaoId`.

### 3. Exercitar compra, posição e venda

`POST {{baseUrl}}/operacoes/compras`

```json
{"carteiraId":{{carteiraId}},"acaoId":{{acaoId}},"quantidade":10,"precoUnitario":25.50,"corretagem":0,"impostos":0,"valorAdicional":0}
```

Valide `GET /carteiras/{{carteiraId}}/acoes`, `GET /carteiras/{{carteiraId}}/resumo` e `GET /operacoes/carteiras/{{carteiraId}}`.

`POST {{baseUrl}}/operacoes/vendas`

```json
{"carteiraId":{{carteiraId}},"acaoId":{{acaoId}},"quantidade":2,"precoUnitario":27.80,"corretagem":0,"impostos":0,"valorAdicional":0}
```

Para testar erros, remova o token e espere `401`; envie `quantidade: 0` e espere `400`.

## Documentação específica

- [README do backend](Carteira-acoes-backend/README.md)
- [README do frontend](Carteira-acoes-frontend/README.md)

Não use credenciais reais nos environments exportados do Postman ou nos commits.
