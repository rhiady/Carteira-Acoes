# Carteira de Ações — Backend

API REST para cadastrar usuários, carteiras e corretoras, além de registrar compras e vendas. Administradores importam ações pelo ticker para o catálogo; usuários registram compras apenas de ações já cadastradas. Os dados cadastrais de uma corretora são importados pelo CNPJ e a corretora precisa constar na CVM.

## Arquitetura e domínios

O projeto usa Spring Boot, Spring Data JPA, Flyway, OpenFeign e PostgreSQL no perfil de desenvolvimento. A camada HTTP (`resources`) valida e recebe as requisições, os serviços aplicam as regras de negócio, os repositórios persistem os dados e os adaptadores/clients consultam provedores externos.

```text
Cliente HTTP → Resource → Service → Repository → Banco de dados
                         └→ Client/Adapter → APIs externas
```

Domínios principais:

- **Usuário e carteira:** um usuário pode ter carteiras para organizar as posições.
- **Ação e cotação:** um administrador cadastra a ação pelo ticker e mercado; nome da empresa e preço são buscados externamente. Compras referenciam o identificador da ação cadastrada.
- **Corretora:** o CNPJ identifica a instituição; razão social e endereço vêm da consulta cadastral e o registro é validado na CVM.
- **Operação e posição:** compras e vendas atualizam o histórico e as posições da carteira.

> A API ainda não possui autenticação/autorização. Não a exponha publicamente sem adicionar esse controle.

## Pré-requisitos

- JDK 21;
- PostgreSQL para executar com o perfil `dev`;
- uma chave da Alpha Vantage para ativos dos EUA e, quando necessário, um token da Brapi;
- Bash ou outro terminal capaz de exportar variáveis de ambiente.

O Maven Wrapper já está no repositório; não é necessário instalar Maven globalmente.

## Configuração local

Crie o banco local, por exemplo:

```sql
CREATE DATABASE cotacao;
```

Copie `.env.example` para `.env` e use valores reais somente no seu ambiente local. O Spring Boot **não lê um arquivo `.env` sozinho** neste projeto; exporte as variáveis antes de iniciar a aplicação.

```bash
cp .env.example .env
set -a
source .env
set +a
./mvnw spring-boot:run
```

Exemplo seguro de `.env` (não versione chaves ou senhas):

```dotenv
SPRING_PROFILES_ACTIVE=dev

DB_URL=jdbc:postgresql://localhost:5432/cotacao
DB_USERNAME=postgres
DB_PASSWORD=<sua-senha-local>

# A aplicação atual acrescenta /quote/{ticker} a esta base.
BRAPI_URL=https://brapi.dev/api
BRAPI_TOKEN=<seu-token-brapi>

TWELVE_DATA_URL=https://api.twelvedata.com
TWELVE_DATA_API_KEY=<sua-chave-twelve-data>

# Referência oficial USD/BRL; esta é a URL pública padrão da PTAX/BCB.
PTAX_URL=https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata

BRASIL_API_URL=https://brasilapi.com.br/api

# Origens do frontend autorizadas a acessar a API no navegador.
# Use uma lista separada por vírgula e configure a origem implantada em produção.
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
```

O perfil `dev` usa PostgreSQL e aplica as migrations do Flyway. O perfil `test` usa H2 em memória e é selecionado ao executar os testes. `SPRING_PROFILES_ACTIVE` tem precedência sobre o perfil definido em arquivo de propriedades.

## Executar e testar

```bash
# Executa a suíte automatizada com o perfil de testes
./mvnw test

# Sobe a API localmente (porta 8080)
set -a && source .env && set +a
./mvnw spring-boot:run
```

Com a aplicação em execução:

- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Console H2 (somente no perfil `test`): `http://localhost:8080/h2-console`

### Conexão do frontend

A URL base local da API é `http://localhost:8080` e todos os endpoints de negócio usam o prefixo `/api`: por exemplo, `GET http://localhost:8080/api/usuarios`.

## Execução com Docker Compose

Com os repositórios backend e frontend como diretórios irmãos, execute a partir de `deploy`:

```bash
docker compose up --build -d
```

O frontend fica disponível em `http://localhost:4200` e a API em `http://localhost:8080`. O PostgreSQL é acessível apenas pela rede interna do Compose e persiste os dados no volume nomeado `postgres_data`.

Para acompanhar os logs ou encerrar os serviços preservando os dados:

```bash
docker compose logs -f
docker compose down
```

Use `docker compose down -v` somente quando quiser remover intencionalmente o volume e recriar o banco vazio.

Para o navegador liberar as chamadas, a origem exata em que o frontend está em execução deve constar em `CORS_ALLOWED_ORIGINS`. O padrão local permite os servidores de desenvolvimento `http://localhost:5173` (Vite) e `http://localhost:3000`. Para outra porta, hostname ou para produção, exporte a variável com as origens necessárias, separadas por vírgula, antes de iniciar a API:

```bash
export CORS_ALLOWED_ORIGINS="http://localhost:4200,https://app.exemplo.com"
./mvnw spring-boot:run
```

Origens não configuradas não recebem permissão CORS. Não use `*`, especialmente quando credenciais forem enviadas pelo navegador.

## API HTTP

As listagens aceitam paginação do Spring Data, por exemplo `?page=0&size=20&sort=id,desc`. A resposta paginada contém `content`, `totalElements`, `totalPages`, `size` e `number`.

| Recurso | Operações |
| --- | --- |
| Autenticação | `POST /api/auth/cadastro`, `POST /api/auth/login`, `GET /api/auth/me` |
| Usuários | `POST /api/usuarios`, `GET /api/usuarios`, `GET /api/usuarios/{id}`, `PUT /api/usuarios/{id}`, `PUT /api/usuarios/{id}/papel`, `PUT /api/usuarios/{id}/senha` |
| Carteiras | `POST /api/carteiras`, `GET /api/carteiras`, `GET /api/carteiras/{id}`, `GET /api/usuarios/{usuarioId}/carteiras`, `GET /api/carteiras/{id}/acoes` |
| Ações | `POST /api/acoes`, `GET /api/acoes`, `GET /api/acoes/{id}`, `GET /api/acoes/ticker/{ticker}`, `POST /api/acoes/{id}/cotacao` |
| Corretoras | `POST /api/corretoras`, `GET /api/corretoras`, `GET /api/corretoras/{id}`, `GET /api/corretoras/cnpj/{cnpj}` |
| Operações | `POST /api/operacoes/compras`, `POST /api/operacoes/vendas`, `GET /api/operacoes/{id}`, `GET /api/operacoes/carteiras/{carteiraId}` |

### Corpos de criação

```json
POST /api/auth/cadastro
{"nome":"Ana Silva","email":"ana@example.com","senha":"uma-senha-segura"}
```

```json
POST /api/usuarios
{"login":"ana@example.com","password":"uma-senha-segura","papel":"USER"}
```

```json
POST /api/carteiras
{"nome":"Longo prazo","usuarioId":1}
```

```json
POST /api/corretoras
{"cnpj":"00.000.000/0001-00"}
```

Não envie nome, endereço ou CEP da corretora: a API os consulta pelo CNPJ. O cadastro falha se a corretora não for localizada na base da CVM.

```json
POST /api/acoes
{"ticker":"PETR4","mercado":"BRASIL"}
```

Somente administradores podem cadastrar ação. Informe apenas ticker e mercado (`BRASIL` ou `EUA`): a API busca nome, cotação e moeda no provedor de mercado, cria o histórico inicial e responde `201 Created`; nenhuma compra ou posição é criada. Use o `id` retornado para registrar compras de usuários.

### Valores em BRL para ações dos EUA

As respostas de ações do mercado `EUA` preservam `cotacaoAtual` e `moeda` em USD e podem incluir os campos opcionais `cotacaoEmBrl`, `taxaUsdBrl` e `dataHoraTaxaUsdBrl`. Nas posições, `valorAtual` também continua em USD e os equivalentes opcionais são `cotacaoEmBrl` e `valorAtualEmBrl`. Esses campos não são retornados para ações brasileiras.

Os equivalentes usam a cotação de venda USD/BRL mais recente publicada pela [PTAX do Banco Central](https://www.bcb.gov.br/estabilidadefinanceira/historicocotacoes), com precisão decimal. Em fins de semana ou feriados, a referência pode ser de um dia útil anterior. Se a PTAX estiver indisponível ou devolver uma taxa inválida, a resposta mantém os valores originais em USD e omite todos os campos de conversão; nenhuma taxa estimada é usada.

```json
POST /api/operacoes/compras
{"carteiraId":1,"acaoId":1,"quantidade":10,"precoUnitario":25.50,"corretagem":0,"impostos":0,"valorAdicional":0}

POST /api/operacoes/vendas
{"carteiraId":1,"acaoId":1,"quantidade":5,"precoUnitario":27.80,"corretagem":0,"impostos":0,"valorAdicional":0}
```

Em uma compra, informe `acaoId` de uma ação já cadastrada pelo administrador. `ticker` e `mercado` não são aceitos nesse endpoint; cadastre o ativo primeiro em `POST /api/acoes`. Em compra ou venda, `precoUnitario` é opcional: quando informado, representa o preço efetivamente negociado; quando ausente, a API busca a cotação atual. Os campos numéricos de custo são opcionais quando o contrato os permite. A quantidade deve ser maior que zero; em compra ou venda, `precoUnitario`, quando informado, também deve ser maior que zero.

Para uma operação, envie somente `carteiraId`, `acaoId`, `quantidade` e, opcionalmente, `precoUnitario` e custos. `precoUnitario: 0` é inválido; use valor igual ou superior a `0.0001` ou omita o campo para usar a cotação externa. Uma `acaoId` ausente é erro de validação e uma inexistente retorna erro de recurso não encontrado.

### Erros

Falhas de validação retornam `400`, conflitos de integridade retornam `409` e erros inesperados retornam `500`. Regras de negócio retornam o status específico da regra. O formato é:

```json
{
  "timestamp": "2026-08-23T12:00:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Dados da requisição são inválidos.",
  "path": "/api/acoes"
}
```

Use a Swagger UI como fonte interativa dos schemas e das respostas detalhadas da versão em execução.

## Integrações externas

| Provedor | Uso na API | Configuração | Limites e falhas |
| --- | --- | --- | --- |
| [Brapi](https://brapi.dev/docs) | Dados e cotação de ações brasileiras | `BRAPI_URL`, `BRAPI_TOKEN` | O acesso varia por ticker, token e plano. A Brapi informa limites pelos headers e pode retornar `429`; reduza tentativas e respeite `Retry-After` quando disponível. |
| [Twelve Data](https://twelvedata.com/docs/discovery) | Cotação, nome e instante de ações dos EUA via `GET /quote` | `TWELVE_DATA_URL`, `TWELVE_DATA_API_KEY` | A cota depende da chave e do plano; uma consulta consome um crédito por símbolo. O serviço retorna `429` ao atingir o limite; aguarde antes de tentar novamente, sem loops de retry. |
| [PTAX / Banco Central](https://www.bcb.gov.br/estabilidadefinanceira/historicocotacoes) | Taxa de venda USD/BRL de referência para equivalentes em BRL de ações EUA | `PTAX_URL` (opcional; a URL pública é o padrão) | Não usa chave. Quando não houver publicação no dia, a API usa a taxa mais recente retornada; em falhas ou dados inválidos, não há conversão. |
| [BrasilAPI](https://brasilapi.com.br/docs) | Dados de CNPJ e lista de corretoras da CVM | `BRASIL_API_URL` | Não usa chave nesta integração. Faça consultas pontuais, trate indisponibilidade/retorno ausente e não faça varreduras ou loops sobre o serviço. |

O código atual da Brapi usa a rota legada `GET /api/quote/{ticker}` com token como parâmetro de consulta. A documentação atual do provedor apresenta rotas `v2` e recomenda enviar o token no header `Authorization`. Antes de atualizar a dependência/adapter, mantenha `BRAPI_URL` na base esperada pelo cliente atual; ao migrar para v2, ajuste o client e a configuração juntos.

Erros de provedores são tratados como falha de importação/atualização: confira a conectividade, a chave/token, o ticker/CNPJ e a disponibilidade do provedor antes de repetir a operação. Não registre tokens, senhas ou payloads sensíveis em logs.

## Segurança de configuração

- `.env` deve permanecer fora do Git; use apenas `.env.example` como modelo.
- Nunca inclua senhas, tokens ou chaves reais em documentação, issues ou commits.
- Revogue e substitua imediatamente qualquer credencial que tenha sido publicada por engano.
