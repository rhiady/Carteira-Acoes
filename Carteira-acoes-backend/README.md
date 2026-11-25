# Carteira de Ações — Backend

API REST responsável por autenticação, usuários, carteiras, ações, corretoras, operações e posições financeiras.

## Arquitetura

```text
HTTP → resources → services → repositories → PostgreSQL
                         └→ clients/adapters → provedores externos
```

O backend usa Spring Boot 4, Java 21, Spring Data JPA, Flyway, Spring Security Resource Server/JWT, OpenFeign e Springdoc OpenAPI. O perfil `dev` usa PostgreSQL; os testes usam H2 em memória.

## Domínios

- `AuthResource`: cadastro, login, sessão atual e alteração de senha.
- `UsuarioResource`: administração de usuários, situação e papel.
- `CarteiraResource`: carteiras, posições e resumo por moeda.
- `AcaoResource`: catálogo, cotação, histórico e atualização em lote.
- `CorretoraResource`: cadastro por CNPJ e dados validados na CVM.
- `OperacaoResource`: compras, vendas e histórico.

## Pré-requisitos

- JDK 21;
- PostgreSQL;
- Bash ou terminal equivalente;
- credenciais dos provedores de cotação quando cadastrar/atualizar ações.

O Maven Wrapper (`./mvnw`) já está incluído.

## Configuração local

Crie um banco chamado `cotacao` no PostgreSQL. Depois:

```bash
cp .env.example .env
set -a && source .env && set +a
./mvnw spring-boot:run
```

O Spring não carrega `.env` automaticamente; as variáveis precisam ser exportadas. Os valores mínimos do perfil `dev` são:

```dotenv
SPRING_PROFILES_ACTIVE=dev
DB_URL=jdbc:postgresql://localhost:5432/cotacao
DB_USERNAME=postgres
DB_PASSWORD=<senha-local>
JWT_SECRET=<segredo-com-pelo-menos-32-bytes>
APP_ADMIN_SENHA=<senha-com-maiuscula-e-minuscula>
```

O administrador inicial usa `APP_ADMIN_EMAIL` (padrão `admin@carteira.local`) e `APP_ADMIN_SENHA`. Nunca versione `.env` nem credenciais reais.

## Executar e validar

```bash
./mvnw spring-boot:run
./mvnw test
./mvnw clean verify
```

O `verify` gera `target/site/jacoco/` e exige pelo menos 85% de cobertura de linhas e 85% de ramos, conforme o `pom.xml`.

Com a aplicação em execução:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`;
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`;
- H2 Console (perfil `test`): `http://localhost:8080/h2-console`.

## Autenticação e autorização

Envie `Authorization: Bearer <jwt>` nos endpoints protegidos.

- Públicos: `POST /api/auth/login`, `POST /api/auth/cadastro`, consultas `GET` de ações/corretoras, Swagger e preflight CORS.
- `ADMIN`: criar/excluir ações, atualizar cotações, criar/excluir corretoras e administrar usuários.
- Autenticado: carteiras, operações, sessão própria e consultas protegidas.

Carteiras e operações são filtradas pelo usuário autenticado. O JWT é stateless; não há sessão de servidor.

## Endpoints

Todas as rotas de negócio usam o prefixo `/api`. Listagens aceitam `page`, `size` e `sort`.

| Recurso | Endpoints principais |
| --- | --- |
| Auth | `POST /auth/cadastro`, `POST /auth/login`, `GET /auth/me`, `PUT /auth/senha` |
| Usuários (ADMIN) | `POST /usuarios`, `GET /usuarios`, `GET /usuarios/{id}`, `PUT /usuarios/{id}`, `PUT /usuarios/{id}/senha`, `PUT /usuarios/{id}/papel` |
| Carteiras | `POST/GET /carteiras`, `GET/PUT/DELETE /carteiras/{id}`, `GET /carteiras/{id}/acoes`, `GET /carteiras/{id}/resumo`, `GET /usuarios/{usuarioId}/carteiras` |
| Ações | `POST/GET /acoes`, `GET /acoes/{id}`, `GET /acoes/ticker/{ticker}`, `DELETE /acoes/{id}`, `POST /acoes/{id}/cotacao`, `POST /acoes/atualizar-cotacoes`, `GET /acoes/{id}/historico` |
| Corretoras | `POST/GET /corretoras`, `GET /corretoras/{id}`, `GET /corretoras/cnpj/{cnpj}`, `DELETE /corretoras/{id}` |
| Operações | `POST /operacoes/compras`, `POST /operacoes/vendas`, `GET /operacoes/{id}`, `GET /operacoes/carteiras/{carteiraId}` |

### Corpos mais usados

```json
POST /api/auth/cadastro
{"nome":"Ana Silva","email":"ana@example.com","senha":"SenhaSeguraA1"}
```

```json
POST /api/auth/login
{"login":"ana@example.com","password":"SenhaSeguraA1"}
```

```json
POST /api/carteiras
{"nome":"Longo prazo","usuarioId":1,"corretoraId":1}
```

```json
POST /api/acoes
{"ticker":"PETR4","mercado":"BRASIL"}
```

```json
POST /api/operacoes/compras
{"carteiraId":1,"acaoId":1,"quantidade":10,"precoUnitario":25.50,"corretagem":0,"impostos":0,"valorAdicional":0}
```

`precoUnitario` é opcional em compra/venda; quando omitido, a API busca a cotação atual. `quantidade` deve ser positiva e os custos não podem ser negativos. O endpoint de operação aceita `acaoId`, não ticker.

O cadastro de uma ação busca nome, preço e moeda no provedor externo. Ações brasileiras usam BRL e ações dos EUA usam USD. Para ações dos EUA, respostas podem incluir equivalentes em BRL calculados com a PTAX de venda mais recente; se a PTAX falhar, os valores em USD são preservados.

## Respostas e erros

Listagens retornam a página do Spring Data (`content`, `totalElements`, `totalPages`, `size`, `number`). Erros seguem o DTO da API, por exemplo:

```json
{
  "timestamp": "2026-01-01T12:00:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Dados da requisição são inválidos.",
  "path": "/api/acoes"
}
```

Em geral: `400` validação, `401` sem autenticação, `403` sem papel suficiente, `404` recurso ausente, `409` conflito de integridade, `502/503` falha de provedor e `500` erro inesperado.

## Integrações externas

| Provedor | Uso | Variáveis |
| --- | --- | --- |
| Brapi | Ações brasileiras | `BRAPI_URL`, `BRAPI_TOKEN` |
| Twelve Data | Ações dos EUA | `TWELVE_DATA_URL`, `TWELVE_DATA_API_KEY` |
| PTAX/BCB | Conversão USD/BRL | `PTAX_URL` |
| BrasilAPI/CVM | CNPJ, endereço e validação de corretora | URLs configuradas no `.env.example` |

Falhas externas não devem ser mascaradas com valores inventados. Confira credenciais, ticker/CNPJ, conectividade e limites do provedor antes de repetir a operação.

## Docker Compose

A partir de `deploy/`:

```bash
docker compose up --build -d
docker compose logs -f backend
docker compose down
```

O Compose publica o backend em `http://localhost:8080`, o frontend em `http://localhost` e mantém o PostgreSQL no volume nomeado `carteira-acoes-deploy-data`.

Para o navegador, configure `CORS_ALLOWED_ORIGINS` com as origens reais, separadas por vírgula. Evite `*` quando houver credenciais.
