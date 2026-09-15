# 1. Visão Geral

O projeto consiste no desenvolvimento de um sistema web para acompanhamento de investimentos em ações.

O sistema permitirá:

- cadastrar usuários;
- cadastrar e validar corretoras;
- cadastrar ações brasileiras e americanas;
- criar carteiras;
- registrar compras;
- registrar vendas;
- consultar cotações em APIs externas;
- acompanhar posições atuais;
- consultar histórico de operações.

O sistema será utilizado exclusivamente para acompanhamento de investimentos.

Não haverá:

- execução real de ordens;
- movimentação financeira;
- controle de saldo monetário;
- integração bancária.

---

# 2. Escopo dos Ativos

O sistema trabalhará exclusivamente com ações.

Mercados suportados:

```text
BRASIL
EUA
```

Não fazem parte do escopo:

```text
FIIs
ETFs
criptomoedas
opções
renda fixa
fundos de investimento
```

---

# 3. Tecnologias

## Backend

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- Spring Cloud OpenFeign
- Flyway
- Lombok
- Springdoc OpenAPI
- Maven

## Frontend

- Angular
- TypeScript
- Angular Router
- Reactive Forms
- HttpClient

## Banco de dados

Principal:

```text
PostgreSQL ou MySQL
```

Testes:

```text
H2
```

## Testes

- JUnit 5
- Mockito
- Spring Boot Test

---

# 4. Arquitetura

Fluxo interno:

```text
Resource
   ↓
Service
   ↓
Repository
   ↓
Database
```

Fluxo para integrações:

```text
Resource
   ↓
Service
   ↓
Facade / Adapter
   ↓
Feign Client
   ↓
API Externa
```

---

# 5. Estrutura do Projeto

```text
src/main/java/com
│
├── ApiExternaBackendApplication.java
│
├── config
│   ├── FeignConfig.java
│   └── FeignErrorDecoder.java
│
├── domains
│   ├── Usuario.java
│   ├── Acao.java
│   ├── Carteira.java
│   ├── CarteiraAcao.java
│   ├── Corretora.java
│   ├── Operacao.java
│   │
│   ├── dtos
│   │   ├── UsuarioRequestDTO.java
│   │   ├── UsuarioResponseDTO.java
│   │   ├── AcaoRequestDTO.java
│   │   ├── AcaoResponseDTO.java
│   │   ├── CarteiraRequestDTO.java
│   │   ├── CarteiraResponseDTO.java
│   │   ├── CarteiraAcaoResponseDTO.java
│   │   ├── CorretoraRequestDTO.java
│   │   ├── CorretoraResponseDTO.java
│   │   ├── CompraRequestDTO.java
│   │   ├── VendaRequestDTO.java
│   │   └── OperacaoResponseDTO.java
│   │
│   └── enums
│       ├── TipoOperacao.java
│       ├── Mercado.java
│       └── Moeda.java
│
├── infra
│   ├── adapter
│   │   ├── CotacaoAdapter.java
│   │   ├── BrapiAdapter.java
│   │   └── AlphaVantageAdapter.java
│   │
│   ├── client
│   │   ├── alphavantage
│   │   ├── brapi
│   │   ├── cep
│   │   ├── cnpj
│   │   └── cvm
│   │
│   ├── converters
│   │   └── TipoOperacaoConverter.java
│   │
│   └── facade
│       ├── CepFacade.java
│       ├── CnpjFacade.java
│       └── CvmFacade.java
│
├── mappers
│   ├── UsuarioMapper.java
│   ├── AcaoMapper.java
│   ├── CarteiraMapper.java
│   ├── CarteiraAcaoMapper.java
│   ├── CorretoraMapper.java
│   └── OperacaoMapper.java
│
├── repositories
│   ├── UsuarioRepository.java
│   ├── AcaoRepository.java
│   ├── CarteiraRepository.java
│   ├── CarteiraAcaoRepository.java
│   ├── CorretoraRepository.java
│   └── OperacaoRepository.java
│
├── resources
│   ├── UsuarioResource.java
│   ├── AcaoResource.java
│   ├── CarteiraResource.java
│   ├── CorretoraResource.java
│   ├── OperacaoResource.java
│   └── exceptions
│       └── GlobalExceptionHandler.java
│
└── services
    ├── UsuarioService.java
    ├── AcaoService.java
    ├── CarteiraService.java
    ├── CorretoraService.java
    └── OperacaoService.java
```

---

# 6. Modelo de Domínio

```text
Usuario
   │
   │ 1:N
   ▼
Carteira
   │
   ├── 1:N ── CarteiraAcao ── N:1 ── Acao
   │
   └── 1:N ── Operacao ───── N:1 ── Acao
```

`Corretora` será mantida como entidade independente de validação cadastral.

---

# 7. Relacionamentos Bidirecionais

Serão utilizados relacionamentos bidirecionais quando houver necessidade de navegação nos dois sentidos.

```text
Usuario ↔ Carteira
Carteira ↔ CarteiraAcao
Acao ↔ CarteiraAcao
Carteira ↔ Operacao
Acao ↔ Operacao
```

O lado `@ManyToOne` será responsável pela FK.

Exemplo:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```

No lado inverso:

```java
@OneToMany(mappedBy = "usuario")
private List<Carteira> carteiras;
```

---

# 8. Serialização

Entidades JPA não deverão ser retornadas diretamente nos Resources.

Fluxo obrigatório:

```text
Entity
   ↓
Mapper
   ↓
ResponseDTO
```

Isso evita recursão causada pelos relacionamentos bidirecionais.

Também deverá ser evitado o uso indiscriminado de:

```text
@Data
```

nas entidades.

Relacionamentos não deverão participar automaticamente de:

```text
equals
hashCode
toString
```

---

# 9. Padrão de IDs

Todos os IDs deverão utilizar:

```java
Long
```

Exemplo:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

Aplicável a:

```text
Usuario
Carteira
CarteiraAcao
Acao
Operacao
Corretora
```

IDs nos DTOs também serão `Long`.

---

# 10. Padrão Financeiro

Todos os valores financeiros deverão utilizar:

```java
BigDecimal
```

Nunca utilizar:

```text
double
float
```

Aplicável a:

```text
quantidade
cotacaoAtual
precoUnitario
valorBruto
corretagem
impostos
valorAdicional
valorLiquido
```

---

# 11. Valores Zerados

Valores financeiros opcionais deverão assumir:

```java
BigDecimal.ZERO
```

Exemplo:

```java
if (corretagem == null) {
    corretagem = BigDecimal.ZERO;
}
```

---

# 12. Precisão no Banco

Padrão:

```java
@Column(
    nullable = false,
    precision = 19,
    scale = 4
)
private BigDecimal precoUnitario;
```

Equivalente:

```sql
DECIMAL(19,4)
```

O mesmo padrão será utilizado para valores financeiros.

---

# 13. Comparação de BigDecimal

Utilizar:

```java
valor.compareTo(BigDecimal.ZERO)
```

Exemplo:

```java
if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
    throw new QuantidadeInvalidaException();
}
```

---

# 14. Usuario

Campos:

```text
id : Long
nome
email
senha
createdAt
updatedAt
```

Relacionamento:

```text
Usuario 1 ─── N Carteira
```

---

# 15. Regras de Usuario

- nome obrigatório;
- e-mail obrigatório;
- e-mail válido;
- e-mail único;
- senha obrigatória;
- senha nunca deverá aparecer no response.

Constraint:

```text
UNIQUE(email)
```

Nesta versão não haverá:

```text
Spring Security
JWT
Role
UserDetails
Login
Autorização
```

As classes serão apenas preparadas para futura evolução.

---

# 16. Carteira

Campos:

```text
id : Long
nome
usuario
createdAt
updatedAt
```

Toda carteira deverá possuir usuário.

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```

---

# 17. CarteiraAcao

Representa a posição atual da ação.

Campos:

```text
id : Long
carteira
acao
quantidade : BigDecimal
version : Long
createdAt
updatedAt
```

Responsabilidade:

```text
CarteiraAcao
→ estado atual
```

---

# 18. Unicidade de CarteiraAcao

Uma ação somente poderá aparecer uma vez em cada carteira.

Constraint:

```sql
UNIQUE (carteira_id, acao_id)
```

Novas compras deverão atualizar o registro existente.

---

# 19. Quantidade Não Negativa

A quantidade nunca poderá ficar abaixo de:

```java
BigDecimal.ZERO
```

Java:

```java
if (novaQuantidade.compareTo(BigDecimal.ZERO) < 0) {
    throw new QuantidadeAcaoInsuficienteException();
}
```

Banco:

```sql
CHECK (quantidade >= 0)
```

quando suportado pelo banco principal escolhido.

---

# 20. Controle de Concorrência

`CarteiraAcao` utilizará optimistic locking.

```java
@Version
private Long version;
```

Objetivo:

evitar atualização perdida caso duas operações tentem modificar simultaneamente a mesma posição.

---

# 21. Acao

Campos:

```text
id : Long
ticker
nomeEmpresa
mercado
moeda
cotacaoAtual : BigDecimal
dataHoraCotacao
createdAt
updatedAt
```

---

# 22. Mercado

```java
public enum Mercado {
    BRASIL,
    EUA
}
```

---

# 23. Moeda

```java
public enum Moeda {
    BRL,
    USD
}
```

A moeda será definida automaticamente.

```text
BRASIL → BRL
EUA → USD
```

O usuário não deverá informar livremente a moeda.

Não será permitido:

```text
BRASIL + USD
EUA + BRL
```

---

# 24. Regras de Acao

- ticker obrigatório;
- ticker uppercase;
- ticker único;
- ticker precisa existir externamente;
- mercado obrigatório;
- moeda determinada pelo mercado;
- cotação deverá possuir data/hora.

Constraint:

```text
UNIQUE(ticker)
```

---

# 25. Corretora

Campos:

```text
id : Long

cnpj
razaoSocial
nomeFantasia
email
telefone

cep
logradouro
numero
complemento
bairro
cidade
uf

situacaoCadastral

registroCvm
dataValidacaoCvm

createdAt
updatedAt
```

---

# 26. Regra Obrigatória da CVM

Uma corretora somente poderá ser persistida se possuir registro válido na fonte da CVM utilizada pelo projeto.

Fluxo obrigatório:

```text
CNPJ válido
+
empresa encontrada
+
CEP válido
+
CVM válida
=
Corretora persistida
```

Caso não seja validada na CVM:

```text
NÃO PERSISTIR
```

Essa regra torna obrigatória a validação prevista no requisito acadêmico de consulta à CVM ou fonte pública equivalente.

---

# 27. validadaNaCvm

Não será necessário manter:

```text
validadaNaCvm : boolean
```

A existência da corretora no banco já significará que ela passou pela validação.

Quando disponíveis, poderão ser armazenados:

```text
registroCvm
dataValidacaoCvm
```

---

# 28. Fluxo da Corretora

```text
POST /corretoras
       ↓
CorretoraResource
       ↓
CorretoraService
       ↓
validar duplicidade
       ↓
CnpjFacade
       ↓
CepFacade
       ↓
CvmFacade
       ↓
possui CVM?
    ↙       ↘
  SIM       NÃO
   ↓         ↓
salvar     erro 422
```

---

# 29. Operacao

Representa o histórico de compras e vendas.

Campos:

```text
id : Long

carteira
acao
tipo

quantidade : BigDecimal
precoUnitario : BigDecimal

valorBruto : BigDecimal
corretagem : BigDecimal
impostos : BigDecimal
valorAdicional : BigDecimal
valorLiquido : BigDecimal

dataHora
createdAt
```

---

# 30. TipoOperacao

```java
public enum TipoOperacao {
    COMPRA,
    VENDA
}
```

---

# 31. Operacao x CarteiraAcao

Responsabilidades:

```text
Operacao
→ histórico
```

```text
CarteiraAcao
→ posição atual
```

Exemplo:

```text
COMPRA 10 PETR4
COMPRA  8 PETR4
VENDA   3 PETR4
```

Histórico:

```text
3 operações
```

Posição atual:

```text
15 PETR4
```

---

# 32. Compra sem Controle Financeiro

A compra não dependerá de saldo monetário.

O sistema não fará:

```text
débito
aporte
saque
controle de saldo
limite financeiro
```

---

# 33. Compra com Preço Informado

Em uma compra, `precoUnitario` será opcional.

Caso seja informado:

```text
usar preço informado
```

Não será necessário consultar API para determinar o preço da operação.

Exemplo:

```json
{
  "carteiraId": 1,
  "acaoId": 5,
  "quantidade": 10,
  "precoUnitario": 31.85
}
```

---

# 34. Compra sem Preço Informado

Caso o preço não seja informado:

```text
COMPRA
   ↓
precoUnitario == null
   ↓
CotacaoAdapter
   ↓
API externa
   ↓
preço
```

Esse preço será utilizado na operação.

---

# 35. Falha da API durante Compra

## Compra com preço informado

```text
API não é obrigatória para determinar o preço
```

A compra poderá ser registrada.

## Compra sem preço

```text
API obrigatória
```

Se a consulta falhar:

```text
NÃO REGISTRAR
```

Nunca utilizar preço:

```text
0
fictício
estimado
default
```

---

# 36. CompraRequestDTO

```text
carteiraId : Long
acaoId : Long
quantidade : BigDecimal
precoUnitario : BigDecimal opcional
```

---

# 37. Atualização na Compra

```text
novaQuantidade =
quantidadeAtual + quantidadeComprada
```

Caso `CarteiraAcao` não exista:

```text
criar posição
```

Caso já exista:

```text
atualizar posição
```

---

# 38. Venda

A venda somente poderá ocorrer se a ação já tiver sido comprada naquela carteira.

Pré-requisitos:

```text
CarteiraAcao existente
quantidade > 0
quantidadeVendida <= quantidadeAtual
```

---

# 39. Preço da Venda

Na venda, o preço sempre será obtido através da API.

```text
VENDA
 ↓
CotacaoAdapter
 ↓
API externa
 ↓
precoUnitario
```

O usuário não enviará preço no request.

---

# 40. Falha da API na Venda

A API será obrigatória.

Se a cotação falhar:

```text
NÃO REGISTRAR VENDA
```

Também não deverá ocorrer alteração de `CarteiraAcao`.

---

# 41. VendaRequestDTO

```text
carteiraId : Long
acaoId : Long
quantidade : BigDecimal
corretagem : BigDecimal opcional
impostos : BigDecimal opcional
valorAdicional : BigDecimal opcional
```

---

# 42. Venda Superior à Posição

Exemplo:

```text
posição = 5
venda = 7
```

Resultado:

```text
422 Unprocessable Entity
```

---

# 43. Venda Total

```text
posição = 10
venda = 10
```

Resultado:

```java
CarteiraAcao.quantidade = BigDecimal.ZERO;
```

O registro continuará no banco.

---

# 44. Posições Zeradas

Uma posição zerada deverá permanecer persistida.

Entretanto, consultas de posições ativas deverão retornar apenas:

```text
quantidade > 0
```

---

# 45. Corretagem

Opcional.

Default:

```java
BigDecimal.ZERO
```

Regra:

```text
corretagem >= 0
```

---

# 46. Impostos

Opcional.

Default:

```java
BigDecimal.ZERO
```

Regra:

```text
impostos >= 0
```

---

# 47. Valor Adicional

Representa uma entrada positiva.

Não é custo.

Default:

```java
BigDecimal.ZERO
```

Regra:

```text
valorAdicional >= 0
```

---

# 48. Valor Bruto

```text
valorBruto =
quantidade × precoUnitario
```

---

# 49. Valor Líquido

```text
valorLiquido =
valorBruto
+ valorAdicional
- corretagem
- impostos
```

---

# 50. PrePersist

`Operacao` utilizará:

```java
@PrePersist
```

Responsabilidades:

```text
corretagem null → BigDecimal.ZERO
impostos null → BigDecimal.ZERO
valorAdicional null → BigDecimal.ZERO
dataHora null → agora
calcular valorBruto
calcular valorLiquido
```

---

# 51. PreUpdate

Também existirá:

```java
@PreUpdate
```

para recalcular valores derivados quando necessário.

Os lifecycle hooks não poderão acessar:

```text
Repository
API externa
Service
```

---

# 52. Histórico Imutável

Não existirão inicialmente:

```http
PUT /operacoes/{id}
PATCH /operacoes/{id}
```

Operações já registradas serão consideradas histórico.

Uma eventual correção futura deverá utilizar:

```text
cancelamento
estorno
```

e não edição direta.

---

# 53. Transactional

Compra e venda deverão ser processadas em:

```java
@Transactional
```

Operação e posição deverão ser atualizadas juntas.

```text
Operacao
+
CarteiraAcao
=
mesma transação
```

Qualquer falha:

```text
ROLLBACK
```

---

# 54. Cascades

Não utilizar `CascadeType.ALL` indiscriminadamente.

Principalmente em relações com:

```text
Acao
```

Exemplo:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "acao_id", nullable = false)
private Acao acao;
```

sem cascade.

Excluir uma operação ou posição nunca deverá excluir a ação global.

---

# 55. Timezone

O backend deverá utilizar UTC como referência.

Aplicável a:

```text
dataHora
createdAt
updatedAt
dataHoraCotacao
dataValidacaoCvm
```

Configuração:

```properties
spring.jackson.time-zone=UTC
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

O Angular será responsável por converter para apresentação quando necessário.

---

# 56. OpenFeign Timeout

Configuração inicial:

```properties
spring.cloud.openfeign.client.config.default.connectTimeout=3000
spring.cloud.openfeign.client.config.default.readTimeout=5000
```

Nenhuma integração poderá aguardar indefinidamente.

---

# 57. Retry

Retry deverá ser limitado.

Poderá ser utilizado em falhas transitórias:

```text
timeout
502
503
504
```

Não realizar retry automático para:

```text
400
401
403
404
```

`429` deverá ser tratado especificamente, evitando novas chamadas imediatas.

---

# 58. FeignErrorDecoder

Será criado:

```text
FeignErrorDecoder
```

Mapeamento:

```text
404
→ ExternalResourceNotFoundException

401 / 403
→ ExternalApiAuthenticationException

429
→ ExternalApiRateLimitException

500 / 502 / 503 / 504
→ ExternalApiUnavailableException

outros
→ ExternalApiException
```

Services não deverão trabalhar diretamente com `FeignException`.

---

# 59. CotacaoAdapter

Contrato:

```text
CotacaoAdapter
```

Implementações:

```text
BrapiAdapter
AlphaVantageAdapter
```

Fluxo:

```text
Service
 ↓
CotacaoAdapter
 ↓
Adapter
 ↓
Feign Client
 ↓
API
```

---

# 60. Facades

```text
CnpjFacade
CepFacade
CvmFacade
```

Fluxo:

```text
Service
 ↓
Facade
 ↓
Feign Client
 ↓
API
```

---

# 61. N+1

Relacionamentos deverão utilizar preferencialmente:

```java
FetchType.LAZY
```

Não utilizar `EAGER` para resolver problemas de consulta.

Quando necessário utilizar:

```text
@EntityGraph
JOIN FETCH
DTO Query
Projection
```

---

# 62. Histórico Ordenado

Histórico deverá ser retornado por padrão:

```text
dataHora DESC
```

Operações mais recentes primeiro.

---

# 63. Paginação

Listagens deverão utilizar `Pageable`.

Exemplos:

```http
GET /acoes?page=0&size=20
GET /corretoras?page=0&size=20
GET /carteiras/{id}/operacoes?page=0&size=20
```

Histórico:

```text
dataHora DESC
```

---

# 64. Bean Validation

DTOs utilizarão, quando aplicável:

```text
@NotNull
@NotBlank
@Email
@Positive
@DecimalMin
```

Exemplo:

```java
@DecimalMin(value = "0.0001")
private BigDecimal quantidade;
```

---

# 65. Integridade no Banco

As principais regras também serão protegidas no banco.

Constraints:

```text
UNIQUE usuario.email

UNIQUE corretora.cnpj

UNIQUE acao.ticker

UNIQUE carteira_acao(carteira_id, acao_id)
```

---

# 66. Foreign Keys

```text
carteira.usuario_id NOT NULL

carteira_acao.carteira_id NOT NULL
carteira_acao.acao_id NOT NULL

operacao.carteira_id NOT NULL
operacao.acao_id NOT NULL
```

---

# 67. Índices

```text
INDEX usuario(email)

INDEX corretora(cnpj)

INDEX acao(ticker)

INDEX carteira(usuario_id)

INDEX carteira_acao(carteira_id)

INDEX operacao(
    carteira_id,
    acao_id,
    data_hora
)
```

---

# 68. Flyway

Schema controlado exclusivamente por Flyway.

Diretório:

```text
src/main/resources/db/migration
```

Migrations iniciais:

```text
V1__create_table_usuario.sql
V2__create_table_corretora.sql
V3__create_table_acao.sql
V4__create_table_carteira.sql
V5__create_table_carteira_acao.sql
V6__create_table_operacao.sql
V7__create_indexes.sql
```

Migrations já aplicadas não deverão ser alteradas.

---

# 69. Hibernate

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
```

Responsabilidades:

```text
Flyway
→ schema

Hibernate
→ validação dos mappings
```

---

# 70. Profiles

Profiles:

```text
dev
test
```

Arquivos:

```text
application.properties
application-dev.properties
application-test.properties
```

---

# 71. application.properties

Somente configurações compartilhadas.

Exemplo:

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true

spring.jackson.time-zone=UTC
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

---

# 72. Profile dev

Responsável por:

```text
banco local
Feign
URLs externas
logs
```

Credenciais virão das variáveis de ambiente.

---

# 73. Profile test

Responsável por testes.

Poderá utilizar:

```text
H2
```

Integrações deverão ser mockadas em testes unitários.

Futuramente poderá ser utilizado:

```text
Testcontainers
```

---

# 74. .env

Variáveis locais:

```dotenv
DB_URL=
DB_USERNAME=
DB_PASSWORD=

BRAPI_URL=
BRAPI_TOKEN=

ALPHA_VANTAGE_URL=
ALPHA_VANTAGE_API_KEY=

CEP_API_URL=
CNPJ_API_URL=
CVM_API_URL=
```

O `.env` não será versionado.

---

# 75. .env.example

Será versionado sem valores sensíveis.

---

# 76. Logs das Integrações

Logs poderão conter:

```text
provider
operation
ticker/CNPJ
status
durationMs
erro
```

Exemplo:

```text
provider=BRAPI
operation=GET_QUOTE
ticker=PETR4
status=200
durationMs=245
```

---

# 77. Dados Proibidos nos Logs

Nunca registrar:

```text
API key
token
senha
Authorization header
credencial do banco
```

---

# 78. README

O README deverá documentar cada integração.

Para cada API:

```text
nome
finalidade
base URL
autenticação
variáveis
rate limit
limitações
disponibilidade
erros esperados
```

Isso atende explicitamente à exigência do trabalho de documentar as APIs externas e suas limitações.

---

# 79. Endpoints — Usuario

```http
POST /usuarios
GET /usuarios
GET /usuarios/{id}
```

---

# 80. Endpoints — Carteira

```http
POST /carteiras
GET /carteiras
GET /carteiras/{id}

GET /usuarios/{usuarioId}/carteiras

GET /carteiras/{id}/acoes
```

---

# 81. Endpoints — Corretora

```http
POST /corretoras
GET /corretoras
GET /corretoras/{id}
GET /corretoras/cnpj/{cnpj}
```

---

# 82. Endpoints — Acao

```http
POST /acoes
GET /acoes
GET /acoes/{id}
GET /acoes/ticker/{ticker}
PUT /acoes/{id}/atualizar-cotacao
```

---

# 83. Endpoints — Operacao

```http
POST /operacoes/compras
POST /operacoes/vendas

GET /operacoes/{id}

GET /carteiras/{carteiraId}/operacoes
```

Não haverá atualização genérica de operação.

---

# 84. Exceções

Principais:

```text
UsuarioNotFoundException
EmailDuplicadoException

CarteiraNotFoundException
CarteiraAcaoNotFoundException

AcaoNotFoundException
TickerDuplicadoException
TickerNaoEncontradoException

CorretoraNotFoundException
CnpjDuplicadoException
CnpjNaoEncontradoException
CepNaoEncontradoException
CorretoraNaoRegistradaCvmException

OperacaoNotFoundException
QuantidadeInvalidaException
QuantidadeAcaoInsuficienteException
CustoInvalidoException
ValorAdicionalInvalidoException

ExternalResourceNotFoundException
ExternalApiException
ExternalApiUnavailableException
ExternalApiRateLimitException
ExternalApiAuthenticationException
```

---

# 85. GlobalExceptionHandler

Todas as exceções deverão resultar em respostas padronizadas.

Exemplo:

```json
{
  "timestamp": "2026-08-21T13:00:00Z",
  "status": 422,
  "error": "QUANTIDADE_INSUFICIENTE",
  "message": "A carteira não possui quantidade suficiente da ação.",
  "path": "/operacoes/vendas"
}
```

---

# 86. Fluxo Final — Compra com Preço

```text
Request
 ↓
validar carteira
 ↓
validar ação
 ↓
validar quantidade
 ↓
preço informado
 ↓
usar preço
 ↓
criar Operacao
 ↓
buscar/criar CarteiraAcao
 ↓
somar quantidade
 ↓
commit
```

---

# 87. Fluxo Final — Compra sem Preço

```text
Request
 ↓
validar carteira
 ↓
validar ação
 ↓
validar quantidade
 ↓
CotacaoAdapter
 ↓
API
   ↙    ↘
OK     FALHA
↓        ↓
preço   abortar
↓
criar Operacao
↓
atualizar CarteiraAcao
↓
commit
```

---

# 88. Fluxo Final — Venda

```text
Request
 ↓
buscar carteira
 ↓
buscar ação
 ↓
buscar CarteiraAcao
 ↓
validar quantidade
 ↓
CotacaoAdapter
 ↓
API
   ↙    ↘
OK     FALHA
↓        ↓
preço   abortar
↓
criar Operacao
↓
reduzir CarteiraAcao
↓
commit
```

---

# 89. Fórmulas Oficiais

## Compra

```text
novaQuantidade =
quantidadeAtual + quantidadeComprada
```

## Venda

```text
novaQuantidade =
quantidadeAtual - quantidadeVendida
```

## Valor bruto

```text
valorBruto =
quantidade × precoUnitario
```

## Valor líquido

```text
valorLiquido =
valorBruto
+ valorAdicional
- corretagem
- impostos
```

## Valor atual da posição

```text
valorAtual =
CarteiraAcao.quantidade
×
Acao.cotacaoAtual
```

---

# 90. Regras Técnicas Oficiais

```text
IDs
→ Long
```

```text
dinheiro
→ BigDecimal
```

```text
zero financeiro
→ BigDecimal.ZERO
```

```text
persistência financeira
→ DECIMAL(19,4)
```

```text
concorrência de posição
→ @Version
```

```text
compra/venda
→ @Transactional
```

```text
relacionamentos
→ LAZY
```

```text
schema
→ Flyway
```

```text
configuração
→ .env
```

```text
integração
→ Facade / Adapter → OpenFeign
```

```text
timezone interno
→ UTC
```

```text
histórico
→ dataHora DESC
```

---

# 91. Modelo Final

```text
Usuario
   │
   │ 1:N
   ▼
Carteira
   │
   ├──────────────┐
   │              │
   ▼              ▼
CarteiraAcao    Operacao
   │              │
   └──────┐ ┌─────┘
          ▼ ▼
          Acao
```

Relacionamentos:

```text
Usuario ↔ Carteira

Carteira ↔ CarteiraAcao
Acao ↔ CarteiraAcao

Carteira ↔ Operacao
Acao ↔ Operacao
```

---

# 92. Responsabilidade Final de Cada Entidade

```text
Usuario
→ proprietário das carteiras
```

```text
Carteira
→ agrupador das posições e operações
```

```text
CarteiraAcao
→ estado atual da posição
```

```text
Operacao
→ histórico imutável de compras e vendas
```

```text
Acao
→ ativo financeiro e cotação atual
```

```text
Corretora
→ instituição previamente validada na CVM
```

---

# 93. Critério Final de Consistência

Nenhuma operação deverá alterar parcialmente o sistema.

```text
validar
 ↓
obter preço quando necessário
 ↓
criar Operacao
 ↓
atualizar CarteiraAcao
 ↓
commit
```

Caso qualquer etapa falhe:

```text
nenhuma alteração persistida
```

A posição atual e o histórico deverão permanecer sempre consistentes.