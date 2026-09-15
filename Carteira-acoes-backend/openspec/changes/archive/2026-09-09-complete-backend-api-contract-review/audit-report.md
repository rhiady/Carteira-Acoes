# Backend audit evidence

This document is the living evidence for change `complete-backend-api-contract-review`.
An item marked `inventoried` has been located and linked but is not considered fully reviewed until
the corresponding task in `tasks.md` is complete.

## Baseline — 2026-09-08

- Scope: backend only. No frontend, Docker, Nginx, or deploy file is in the implementation scope.
- Worktree: already contained unstaged and untracked work from earlier initiatives; no staged diff
  was present. Those changes are treated as input and must not be reset or overwritten.
- `./mvnw test`: PASS, 80 tests, 0 failures, 0 errors, 0 skipped.
- `./mvnw clean package`: PASS; executable Spring Boot jar generated.
- Test runtime: Java 21, Spring Boot 4.1.1, `test` profile, H2 2.4.240, Flyway V1–V11.
- Production runtime contract: PostgreSQL datasource configured through `DB_URL`, `DB_USERNAME`,
  and `DB_PASSWORD`; Flyway owns schema evolution. Values of credentials were not copied here.
- Baseline warnings to audit: very verbose SQL logging, H2 version newer than Flyway's verified
  range, deprecated API usage in `FeignErrorDecoder`, and Open Session in View behavior.

## Complete class inventory

Reconciliation command: `rg --files src/main/java src/test/java src/main/resources`. Maven compiled
91 production sources and 21 test sources. The package inventory below accounts for every source.

| Responsibility | Production classes | Status |
|---|---|---|
| Bootstrap (1) | `CarteiraAcoesBackendApplication` | reviewed |
| HTTP resources (6) | `AcaoResource`, `AuthController`, `CarteiraResource`, `CorretoraResource`, `OperacaoResource`, `UsuarioResource` | reviewed |
| Services (11) | `AcaoService`, `AuthService`, `CarteiraService`, `CorretoraService`, `CotacaoAtualizacaoService`, `CurrencyConversionService`, `CurrentUserService`, `OperacaoService`, `PosicaoFinanceiraCalculator`, `TaxaUsdBrl`, `UsuarioService` | reviewed |
| Repositories (7) | `AcaoRepository`, `CarteiraAcaoRepository`, `CarteiraRepository`, `CorretoraRepository`, `HistoricoCotacaoRepository`, `OperacaoRepository`, `UsuarioRepository` | reviewed |
| Entities/base (8) | `Acao`, `AuditableEntity`, `Carteira`, `CarteiraAcao`, `Corretora`, `HistoricoCotacao`, `Operacao`, `Usuario` | reviewed |
| Domain enums (3) | `Mercado`, `Moeda`, `TipoOperacao` | reviewed |
| API DTOs (21) | `AcaoRequest`, `AcaoResponse`, `AtualizacaoCotacoesResponse`, `CarteiraAcaoResponse`, `CarteiraRequest`, `CarteiraResponse`, `CarteiraResumoResponse`, `CompraRequest`, `CorretoraRequest`, `CorretoraResponse`, `ErroResponse`, `HistoricoCotacaoResponse`, `LoginRequest`, `LoginResponse`, `OperacaoResponse`, `PosicaoAcaoResponse`, `ResumoPorMoedaResponse`, `UsuarioRequest`, `UsuarioResponse`, `UsuarioUpdateRequest`, `VendaRequest` | reviewed |
| Integration DTOs (6) | `BrapiQuoteResponse`, `BrasilApiCepResponse`, `BrasilApiCnpjResponse`, `BrasilApiCvmBrokerResponse`, `PtaxQuoteResponse`, `TwelveDataQuoteResponse` | reviewed |
| Mappers (7) | `AcaoMapper`, `CarteiraAcaoMapper`, `CarteiraMapper`, `CorretoraMapper`, `HistoricoCotacaoMapper`, `OperacaoMapper`, `UsuarioMapper` | reviewed |
| Exceptions (2) | `ApiException`, `GlobalExceptionHandler` | reviewed |
| Config (4) | `FeignConfig`, `PaginationRequestInterceptor`, `SecurityConfig`, `WebConfig` | reviewed |
| External clients (5) | `BrapiClient`, `BrasilApiClient`, `FeignErrorDecoder`, `PtaxClient`, `TwelveDataClient` | reviewed |
| Integration adapters/support (6) | `BrapiCotacaoAdapter`, `Cotacao`, `CotacaoAdapter`, `DefaultCotacaoAdapter`, `ExternalCallLogger`, `TwelveDataCotacaoAdapter` | reviewed |
| Facades (3) | `CepFacade`, `CnpjFacade`, `CvmFacade` | reviewed |
| Utilities (1) | `PageableSupport` | reviewed |

The 21 test classes are: `CarteiraAcoesBackendApplicationTests`, `FeignErrorDecoderTest`,
`WebConfigIntegrationTest`, `OperacaoTest`, `GlobalExceptionHandlerTest`, `CnpjFacadeTest`,
`CepFacadeTest`, `CvmFacadeTest`, `CotacaoAdapterDetailsTest`, `UsuarioMapperTest`, `AuthControllerIntegrationTest`,
`CatalogoResourceIntegrationTest`, `CorretoraResourceIntegrationTest`,
`OperacaoResourcePersistenceIntegrationTest`, `UserOwnershipIntegrationTest`,
`AcaoServiceCotacaoTest`, `CarteiraResumoServiceTest`, `CorretoraServiceCvmTest`,
`CurrencyConversionServiceTest`, `OperacaoServiceIntegrationTest`, and
`PosicaoFinanceiraCalculatorTest`.

There are no standalone validator classes: Bean Validation annotations and
`CompraRequest.isStockIdentifierValid()` provide request validation.

## Persistence and configuration inventory

| Kind | Items | Status |
|---|---|---|
| Flyway (11) | V1 usuario; V2 corretora; V3 acao; V4 carteira; V5 carteira_acao; V6 operacao; V7 indexes; V8 carteira-corretora; V9 optional corretora details; V10 historico_cotacao; V11 historico cascade | reviewed |
| Runtime configs (3) | `application.properties`, `application-dev.properties`, `application-test.properties` | reviewed |
| Error boundary | `ApiException` and `GlobalExceptionHandler`; standard `ErroResponse` | reviewed |
| Authentication | BCrypt password encoder plus HTTP-session user id managed by `AuthController`/`CurrentUserService` | reviewed |
| OpenAPI | springdoc generated contract plus endpoint-level annotations | reviewed |

## Endpoint matrix

All 26 controller mappings are represented. `Pageable` means Spring's `page`, `size`, and `sort`
query contract. Unless explicitly annotated, successful object/page responses currently return 200.

| HTTP endpoint | Input and validation | Service → persistence/domain | Success response | Error/security/OpenAPI/test evidence |
|---|---|---|---|---|
| POST `/usuarios` | `@Valid UsuarioRequest` | `UsuarioService.criar` → `UsuarioRepository`, `UsuarioMapper`, BCrypt | 201 `UsuarioResponse`, Location | 400/409; public registration; integration coverage indirect; OpenAPI inferred |
| GET `/usuarios` | `Pageable` | `UsuarioService.listar` → authenticated `Usuario` | 200 `Page<UsuarioResponse>` with one identity | 401; session-scoped; explicit OpenAPI; ownership integration test |
| GET `/usuarios/{id}` | `Long id` | `UsuarioService.buscarPorId` → `CurrentUserService`, `UsuarioRepository` | 200 `UsuarioResponse` | 401/404 with cross-user obscured; explicit OpenAPI; ownership integration test |
| PUT `/usuarios/{id}` | `Long id`, `@Valid UsuarioUpdateRequest` | `UsuarioService.atualizar` → `CurrentUserService`, `UsuarioRepository`, BCrypt | 200 `UsuarioResponse` | 400/401/404/409; own account only; explicit OpenAPI; password preservation/replacement tests |
| POST `/auth/login` | `@Valid LoginRequest` | `AuthService.login` → `UsuarioRepository`, BCrypt | 200 `LoginResponse` and session | 400/401; explicit OpenAPI; `AuthControllerIntegrationTest` |
| POST `/corretoras` | `@Valid CorretoraRequest`, masked or 14-digit CNPJ | `CorretoraService.criar` → CNPJ/CVM facades and `CorretoraRepository` | 201 `CorretoraResponse`, Location | 400/409/422/503; explicit OpenAPI; broker/facade tests |
| GET `/corretoras` | `Pageable` | `CorretoraService.listar` → `CorretoraRepository` | 200 `Page<CorretoraResponse>` | shared pagination review in task 2.9; explicit OpenAPI; catalog tests |
| GET `/corretoras/{id}` | `Long id` | `CorretoraService.buscarPorId` → `CorretoraRepository` | 200 `CorretoraResponse` | 404; explicit OpenAPI; integration tests |
| GET `/corretoras/cnpj/{cnpj}` | validated masked or 14-digit CNPJ | `CorretoraService.buscarPorCnpj` → `CorretoraRepository` | 200 `CorretoraResponse` | normalized; malformed 400; absent 404; explicit OpenAPI; integration test |
| POST `/carteiras` | `@Valid CarteiraRequest` | `CarteiraService.criar` → session user, broker and portfolio repositories | 201 `CarteiraResponse`, Location | 400/401/404; session owner overrides forged `usuarioId`; explicit OpenAPI; ownership test |
| GET `/carteiras` | `Pageable` | `CarteiraService.listar` → `CarteiraRepository.findByUsuarioId` | 200 `Page<CarteiraResponse>` | 401 and owner-scoped; explicit OpenAPI; ownership tests |
| GET `/carteiras/{id}` | `Long id` | `CarteiraService.buscarPorId` → `CarteiraRepository`, `CurrentUserService` | 200 `CarteiraResponse` | 401/404 owner-obscured; explicit OpenAPI; ownership tests |
| GET `/usuarios/{usuarioId}/carteiras` | `Long usuarioId`, `Pageable` | `CarteiraService.listarPorUsuario` → `CurrentUserService`, `CarteiraRepository` | 200 `Page<CarteiraResponse>` | 401/404 on mismatched identity; explicit OpenAPI; ownership tests |
| GET `/carteiras/{id}/acoes` | `Long id`, `Pageable` | `CarteiraService.listarPosicoesAtivas` → position/operation repositories and calculator | 200 `Page<PosicaoAcaoResponse>` | 401/404 owner-obscured; ownership/finance tests; summary annotation |
| GET `/carteiras/{id}/resumo` | `Long id` | `CarteiraService.resumo` → position/operation repositories and calculator | 200 `CarteiraResumoResponse` | discovered extra route; 401/404; `CarteiraResumoServiceTest`; summary annotation |
| POST `/acoes` | validated ticker and `Mercado` | `AcaoService.criar` → market adapter, stock/history repositories | 201 `AcaoResponse`, Location | 400/404/409/502/503; explicit OpenAPI; service/integration tests |
| GET `/acoes` | `Pageable` | `AcaoService.listar` → `AcaoRepository`, optional PTAX enrichment | 200 `Page<AcaoResponse>` | shared pagination task 2.9; explicit OpenAPI; catalog tests |
| GET `/acoes/{id}` | `Long id` | `AcaoService.buscarPorId` → `AcaoRepository`, optional PTAX | 200 `AcaoResponse` | 404; explicit OpenAPI; catalog tests |
| GET `/acoes/ticker/{ticker}` | validated ticker | `AcaoService.buscarPorTicker` → `AcaoRepository`, optional PTAX | 200 `AcaoResponse` | normalized; malformed 400; absent 404; explicit OpenAPI |
| POST `/acoes/{id}/cotacao` | `Long id` | `AcaoService.atualizarCotacao` → adapter and stock/history repositories in one transaction | 200 `AcaoResponse` | 404/502/503; explicit OpenAPI; rollback/history tests |
| POST `/acoes/atualizar-cotacoes` | none | `AcaoService.atualizarTodasCotacoes` → isolated `REQUIRES_NEW` updates | 200 `AtualizacaoCotacoesResponse` | failed items counted/logged without stopping valid items; explicit OpenAPI; batch test |
| GET `/acoes/{id}/historico` | `Long id`, optional inclusive `inicio`/`fim`, default size 100 `dataHora,asc` | `AcaoService.historico` → `HistoricoCotacaoRepository` | 200 `Page<HistoricoCotacaoResponse>` | reversed period 400; absent action 404; explicit OpenAPI; shared sort task 2.9 |
| POST `/operacoes/compras` | positive IDs/quantity/price/costs; exactly `acaoId` or ticker+market | `OperacaoService.comprar` → portfolio/stock/position/operation repositories | 201 `OperacaoResponse`, canonical Location | 400/401/404/409/502/503; transaction and owner first; explicit OpenAPI/tests |
| POST `/operacoes/vendas` | positive IDs/quantity/price and nonnegative costs | `OperacaoService.vender` → portfolio/stock/position/operation repositories | 201 `OperacaoResponse`, canonical Location | 400/401/404/409/422/502/503; oversell/rollback/ownership tests; explicit OpenAPI |
| GET `/operacoes/{id}` | `Long id` | `OperacaoService.buscarPorId` → `OperacaoRepository`, owner check | 200 `OperacaoResponse` | 401/404 owner-obscured; explicit OpenAPI; ownership tests |
| GET `/operacoes/carteiras/{carteiraId}` | `Long carteiraId`, `Pageable` | `OperacaoService.historico` → owner check, `OperacaoRepository` | 200 `Page<OperacaoResponse>` | 401/404 owner-obscured; explicit OpenAPI; persistence/ownership tests |

## Findings register

Findings are promoted here only after their behavior and impact are verified.

| Severity | File/class | Finding and impact | Resolution/evidence |
|---|---|---|---|
| CRITICAL | `UsuarioResource` / `UsuarioService` | Profile listing, lookup, and update accepted no authenticated identity, allowing account enumeration and cross-user password/data changes by ID. | Session identity is now required; list is scoped to self; foreign IDs return the same 404 as absent users; focused IDOR tests pass. |
| LOW | `AuthController` | A successful login reused an existing HTTP session id, retaining a session-fixation risk. | Existing sessions now rotate their id before storing the authenticated user; login contract remains email/password and 200 response. |
| MEDIUM | `CorretoraRequest` / `CorretoraService` | Malformed CNPJ could reach domain/external lookup and the CNPJ path returned an inconsistent result. | Body and path now validate accepted formats; service defense returns standardized 400 before persistence/external calls. |
| MEDIUM | `CnpjFacade` | An empty successful provider response was dereferenced later and became HTTP 500. | Empty payload is translated to `503 EXTERNAL_API_UNAVAILABLE`; regression test added. |
| MEDIUM | `OperacaoResource` | 201 Location for purchases/sales was built from the command path, pointing to nonexistent `/operacoes/compras/{id}` or `/operacoes/vendas/{id}`. | Location now targets canonical `/operacoes/{id}`; both commands have HTTP regression assertions. |
| MEDIUM | `OperacaoService` | A fallback market quote was used without validating null, positive price, or timestamp, allowing a 500 or invalid financial mutation. | Shared quote validation runs before position/operation mutation; invalid fallback rolls back with 502 and no persisted state. |
| MEDIUM | `GlobalExceptionHandler` | Malformed JSON, invalid path/query conversions, and optimistic-lock conflicts fell through to generic HTTP 500 responses. | Known malformed-input failures now return standardized 400, concurrent modifications return a safe 409, and 16 focused handler/HTTP tests pass. |
| LOW | `AcaoRequest` / `AcaoService` | Arbitrary ticker text could reach external providers and persistence; reversed history dates returned 422. | Conservative stock-symbol format is validated defensively; reversed periods now return standardized 400. |
| LOW | `BrapiCotacaoAdapter` | An empty successful body caused a null dereference. | Empty body maps to the existing safe ticker-not-found response; regression test added. |
| MEDIUM | `BrapiCotacaoAdapter` | Brazilian quotes used the application clock instead of the market timestamp, making stored history look newer than its source. | The adapter now requires and parses Brapi `regularMarketTime`, normalizes it to UTC, and rejects absent, invalid, or nonpositive quote data. |
| MEDIUM | `CotacaoAtualizacaoService` | A stale provider response could overwrite a newer quote and exact repeated observations created duplicate history rows. | Updates are monotonic; stale/conflicting observations fail without mutation, while an identical timestamp/price is idempotent. |
| LOW | `FeignErrorDecoder` / `CepFacade` | HTTP 500 was retried despite the bounded retry contract, and an empty CEP body escaped untranslated. | Retry remains limited to 502/503/504; 500 maps to safe 502 and empty CEP responses map to safe 503. |
| INFO | stock/quote/history contracts | Individual quote persistence, UTC timestamps, history creation, inclusive filters, external errors, batch isolation/counts, PTAX enrichment nullability, responses, and OpenAPI were audited. | 27 focused tests pass; sort whitelist remains centralized in task 2.9. |
| INFO | operation contracts | Identifier exclusivity, validation, owner-first access, purchase/sale atomicity, oversell, by-ID/history isolation, response fields, and statuses were audited. | 24 focused operation/ownership tests pass; detailed formulas/concurrency remain tracked by tasks 3.x. |
| INFO | discovered route | `GET /carteiras/{id}/resumo` was the sole mapping absent from the proposal's known-route list. | Added to the 26-row matrix; ownership, finance service, OpenAPI, and tests are linked above. |
| INFO | portfolio contracts | Creation owner assignment, global/user list isolation, by-ID lookup, positions, summary, missing resources, and forged IDs were traced through service/repositories and documented. | Session identity is authoritative even when request `usuarioId` is forged; 16 focused portfolio/ownership tests pass. Position formulas remain tracked by tasks 2.4 and 3.x. |
| INFO | broker contracts | CNPJ normalization, duplicate 409, active CVM requirement, optional external fields, timeout/error translation, transaction boundary, response mapping, and OpenAPI were verified. | 15 focused tests pass; shared page/sort mechanics remain tracked by task 2.9. |
| INFO | user/auth contracts | BCrypt storage, optional-password preservation, encoded replacement, safe responses, duplicate email 409, equivalent invalid-login 401, request validation, and session creation were verified. | 22 focused user/auth/ownership/CORS tests pass; user routes now have explicit OpenAPI success/error annotations. |
| INFO | baseline | Existing suite and package are green before this audit. | 80 tests; clean package PASS |
| LOW | `application.properties` / `application-dev.properties` | OSIV depended on the framework default and development emitted verbose SQL, increasing accidental lazy-loading and persistence-detail leakage risk. | OSIV is explicitly disabled; dev SQL output/DEBUG was disabled; context regression, full suite, package, and PostgreSQL startup pass. |

Final severity totals: **CRITICAL 1**, **HIGH 0**, **MEDIUM 7**, **LOW 5**, **INFO 7**.
Every corrected finding above links to focused tests or runtime evidence; there are no unresolved
CRITICAL or HIGH findings.

## Pagination and public sorting contract

All paginated endpoints expose the standard Spring page body (`content`, `number`, `size`,
`totalElements`, `totalPages`, `first`, `last`, and `empty`). Raw requests require `page >= 0`,
`1 <= size <= 100`, and each sort value in `field[,asc|desc]` form. Validation occurs before the
repository query; persistence paths are never accepted as public field names.

| Flow | Default order | Public sort fields |
|---|---|---|
| users | `id,asc` | `id`, `nome`, `email`, `createdAt`, `updatedAt` |
| brokers | `id,asc` | `id`, `cnpj`, `razaoSocial`, `nomeFantasia`, `cidade`, `uf`, `createdAt`, `updatedAt` |
| portfolios | `id,asc` | `id`, `nome`, `createdAt`, `updatedAt` |
| active positions | `id,desc` | `id`, `quantidade`, `ticker` (translated to `acao.ticker`) |
| stocks | `id,asc` | `id`, `ticker`, `nomeEmpresa`, `mercado`, `moeda`, `cotacaoAtual`, `dataHoraCotacao`, `createdAt`, `updatedAt` |
| quote history | `dataHora,asc` | `id`, `cotacao`, `dataHora` |
| operations | `dataHora,desc; id,desc` | `id`, `ticker`, `tipo`, `quantidade`, `precoUnitario`, `valorBruto`, `valorLiquido`, `dataHora`, `createdAt` |

Regression evidence covers invalid negative page, zero/oversized size, unsupported field, invalid
direction, complete page metadata, position ticker translation in both directions, and the focused
portfolio/operation/catalog suites (28 tests passing).

## Position and portfolio-summary field contract

`GET /carteiras/{id}/acoes` returns only rows with persisted `quantidade > 0`, checks the session
owner before reading positions, sorts in PostgreSQL before pagination, and derives financial fields
from the position's chronological operation history (`dataHora,asc; id,asc`). Decimal operands are
`BigDecimal`; persisted quantities, prices, costs, quotes, and operation totals use `DECIMAL(19,4)`,
while divisions use scale 8 with `HALF_UP`.

| `PosicaoAcaoResponse` field | Origin / formula | Type, nullability, currency |
|---|---|---|
| `carteiraId`, `acaoId` | persisted foreign-key identities | `Long`, non-null |
| `ticker`, `nomeEmpresa` | `Acao` catalog | `String`, non-null |
| `mercado`, `moeda` | `Acao`; BRASIL→BRL, EUA→USD | enum, non-null |
| `quantidade` | open `CarteiraAcao.quantidade` | decimal(19,4), non-null, shares |
| `cotacaoAtual` | latest valid `Acao.cotacaoAtual` | decimal(19,4), nullable, position currency/share |
| `dataHoraCotacao` | provider timestamp normalized to UTC | `OffsetDateTime`, nullable with quote |
| `valorAtual` | `quantidade aberta * cotacaoAtual` | decimal, nullable with quote, position currency |
| `precoMedio` | remaining acquisition cost / open quantity | decimal scale 8, non-null, position currency/share |
| `cotacaoEmBrl` | USD quote * authoritative USD/BRL rate | decimal, nullable; only EUA with quote/rate, BRL/share |
| `valorAtualEmBrl` | USD current value * the same rate | decimal, nullable; only EUA with quote/rate, BRL |
| `taxaUsdBrl`, `dataHoraTaxaUsdBrl` | PTAX rate and its reference instant | decimal/time, nullable; emitted together with conversion |
| `precoMedioVenda` | sum gross sale value / quantity sold | decimal scale 8, nullable until first sale, position currency/share |
| `lucroPrejuizoRealizado` | accumulated net sale receipts - released acquisition cost | decimal, non-null, position currency |
| `lucroPrejuizoNaoRealizado` | current value - remaining acquisition cost | decimal, nullable with quote, position currency |
| `lucroPrejuizo` | realized + unrealized | decimal, nullable with quote, position currency |

Public sorting is limited to `id`, `quantidade`, and `ticker`; default is `id,desc`. Standard page
metadata and the shared `page >= 0`, `1 <= size <= 100` contract apply. Unsupported fields and
directions return standardized 400 responses.

`GET /carteiras/{id}/resumo` checks the same owner and returns persisted portfolio identity/name,
the count of active positions, and an alphabetically ordered list by `moeda`. Each
`ResumoPorMoedaResponse.valorAtual` and `lucroPrejuizo` is the sum of the corresponding position
field without cross-currency addition. If any active position in a currency lacks a quote-dependent
value, that currency aggregate is `null` rather than a partial or fabricated total. Missing quotes
do not trigger PTAX calls. Focused position, summary, pagination, and ownership evidence passes (19
tests, including explicit missing-quote nullability).

## Financial formula and invariant matrix

Mechanical search `rg -n '\b(float|double|Float|Double)\b' src/main/java src/test/java` returns no
matches. All critical calculations use `BigDecimal`. Database operands are `DECIMAL(19,4)`;
products retain exact decimal representation and divisions use scale 8 / `HALF_UP`.

| Result / response field | Operands and source | Unit / currency | Verified invariant |
|---|---|---|---|
| operation `valorBruto` | persisted quantity × unit price | position currency | exact product |
| purchase `valorLiquido` / open cost | gross + brokerage + taxes + additional cost | position currency | costs increase acquisition basis |
| sale `valorLiquido` / receipt | gross - brokerage - taxes - additional cost | position currency | costs reduce sale proceeds |
| open `precoMedio` | remaining chronological acquisition cost / open quantity | currency/share, scale 8 | 10@30→30; +10@40→35; +5@50→38 |
| released cost on sale | sold quantity × current acquisition average | position currency | partial sale preserves remaining average |
| `lucroPrejuizoRealizado` | cumulative net receipts - cumulative released cost | position currency | profit and loss sales accumulate chronologically |
| `precoMedioVenda` | cumulative gross sale value / cumulative sold quantity | currency/share, scale 8 | quantity-weighted; `null` before a sale |
| `valorAtual` | open quantity × current quote | position currency | `null` when quote is missing; zero when quantity is zero and quote exists |
| `lucroPrejuizoNaoRealizado` | current value - remaining acquisition cost | position currency | above/below/equal quote cases; nullable with quote |
| `lucroPrejuizo` | realized + unrealized | position currency | realized history survives zero/reopen; nullable with quote |
| USD `cotacaoEmBrl` | USD quote × PTAX USD/BRL | BRL/share | source USD value preserved with rate and UTC reference time |
| USD `valorAtualEmBrl` | USD current value × same PTAX rate | BRL | never included in native USD/BRL mixed totals |

Known-value tests additionally prove partial sale + purchase gives 38.75 average, a fully closed
position retains its realized result, reopening starts the new open cost at 50, oversell and quote
failures leave no partial state, and purchase/sale mutations are transactional. `CarteiraAcao` uses
an optimistic `@Version`; a synchronized two-transaction regression allows exactly one of two
incompatible 8-of-10 updates to commit, leaving quantity 2. The handler exposes the losing conflict
as safe HTTP 409. Financial/domain/integration evidence: 28 focused tests pass.

## Persistence model and repository audit

Flyway V1–V11 define seven tables, their foreign keys/unique/check constraints, and eight explicit
indexes. The seven JPA entities were reconciled field-by-field with the final schema:

| Entity | Schema correspondence | Relationships / lifecycle |
|---|---|---|
| `Usuario` | required 255-char name/email/password; unique email; UTC audit timestamps | one-to-many portfolios with JPA orphan lifecycle |
| `Corretora` | required 14-char CNPJ and legal name; V9 optional address/details; exact lengths | portfolios reference broker; no database cascade |
| `Acao` | required catalog enums/strings; nullable `DECIMAL(19,4)` quote and offset timestamp | referenced by positions, operations, and cascading quote history |
| `Carteira` | required name, owner and broker foreign keys; UTC audit timestamps | lazy owner/broker; JPA lifecycle owns positions, not operation history |
| `CarteiraAcao` | unique portfolio/stock pair; nonnegative `DECIMAL(19,4)` quantity; non-null version | lazy parents; optimistic `@Version` matches schema default |
| `Operacao` | exact `DECIMAL(19,4)` operands/totals, required enum and offset timestamps | immutable history references portfolio and stock; no cascade |
| `HistoricoCotacao` | positive `DECIMAL(19,4)` quote and required offset timestamp | lazy stock; database delete cascade matches V11 |

JPA annotations now explicitly preserve migration lengths, nullability, enum lengths, and decimal
precision instead of depending on provider defaults. H2 Flyway recreation plus Hibernate
`ddl-auto=validate` passes after the alignment. No database defect was found, so no V12 migration
was created and no applied migration was edited.

All repository methods were reviewed. Public sorts are translated before repository access;
financial queries validate portfolio ownership first; chronological calculations use
`dataHora,asc; id,asc`; quote filters are inclusive; the unique position key prevents duplicates;
and optimistic versioning prevents lost updates. Entity graphs load stock data for both paged and
summary position queries, and owner/broker data for portfolio pages, eliminating the demonstrated
per-row lazy-load risk without widening every association to eager. Focused schema, repository,
ownership, financial, and concurrency tests pass.

## PostgreSQL and integration audit

An isolated PostgreSQL 16 instance was recreated from an empty database. Spring Boot applied
Flyway V1–V11 successfully, Hibernate `ddl-auto=validate` accepted the resulting schema, all seven
repositories initialized, and `/v3/api-docs` returned 200. The resulting public tables were
`usuario`, `corretora`, `acao`, `carteira`, `carteira_acao`, `operacao`, and
`historico_cotacao`; all Flyway history rows reported success. The matching H2 suites also recreate
V1–V11 and validate Hibernate metadata, with no dialect-dependent functional divergence found.

All five external clients, three facades, three quote adapters/support classes, and PTAX conversion
flow were reviewed. Feign uses 2 s connect and 3 s read timeouts with a maximum of two attempts only
for 502/503/504. Authentication, rate-limit, not-found, server, empty-body, invalid-field, numeric,
and timestamp failures translate to safe API errors or an explicitly absent optional PTAX
conversion. Logs contain provider, operation, public lookup identifier, duration, and error code;
tokens, authorization headers, response bodies, and exception messages are not logged.

Valid market instants are normalized to UTC from Brapi ISO offsets and Twelve Data epoch seconds.
PTAX offset-less timestamps are interpreted in `America/Sao_Paulo` and then normalized to UTC.
Entities and DTOs use `OffsetDateTime`, audit/operation clocks use UTC, Hibernate's JDBC timezone and
Jackson timezone are explicitly UTC, and PostgreSQL columns retain timezone semantics. Quote
updates are transactional, monotonic, and idempotent: stale/conflicting observations preserve the
prior stock/history, exact repeats do not duplicate history, and each valid newer individual or
batch observation persists price, source timestamp, and one history row. Batch failures are
isolated and counted while successes commit independently.

History uses inclusive start/end filters, decimal(19,4) values, stable `dataHora,id` ordering,
validated pagination/sorting, and an empty page for an interval without observations. Focused
client/facade/adapter/quote/history tests pass, including explicit prior-state, partial-batch,
duplicate, stale, UTC, inclusive-boundary, precision, pagination, and empty-result assertions.

## Architecture, security, configuration, and coverage audit

All six HTTP controllers were traced to services and none accesses a repository directly. Write
flows are owned by transactional services; repositories remain persistence-only interfaces. The
concrete extractions introduced during the audit (`PosicaoFinanceiraCalculator`,
`CotacaoAtualizacaoService`, and `PageableSupport`) isolate formulas, independent batch
transactions, and public sort translation respectively. No additional split or rewrite had a
demonstrated correctness or testability benefit. Mechanical searches found no TODO/FIXME markers,
console printing, native SQL, dynamic query construction, unused production type, or critical
float/double calculation. The larger service methods orchestrate already-tested domain workflows;
duplicating or relocating them purely for size would add churn without verified benefit.

The security review covered session authentication, session-id rotation, owner-first lookup,
cross-user ID substitution, request DTO whitelisting, Bean Validation, repository query shape,
error sanitization, response schemas, external-call logs, and configuration placeholders. The
application contains no native/dynamic SQL, binds request DTOs rather than entities, omits password
hashes from response DTOs/OpenAPI, logs neither provider payloads nor credentials, and contains no
real hardcoded secret. Financial resources obscure foreign ownership as not found. Public catalog
routes remain public by their existing contract; adding roles/OAuth is outside this change.

Configuration preserves `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`, keeps Flyway as schema owner,
uses PostgreSQL in `dev` and isolated H2 only in `test`, and retains configurable credentialed
CORS origins. Open Session in View is now explicitly disabled after repository entity graphs and
service-side mapping proved that web-layer lazy loading is unnecessary. Development SQL output and
Hibernate SQL DEBUG/TRACE logging were disabled to avoid leaking persistence details or values.
The application context regression confirms the OSIV setting while Flyway/Hibernate validation
continues to pass.

Coverage was reconciled by risk area: all controllers have MockMvc integration coverage; services
cover user/auth, broker validation, portfolio summaries, quote updates, operations and currency;
repositories are exercised against Flyway-created H2 schemas and the isolated PostgreSQL run;
financial invariants include cost basis, realized/unrealized result, reopening and concurrency;
handlers cover validation, malformed input, integrity, unsupported method and optimistic conflict;
ownership covers users, portfolios and operations; external clients/adapters cover status, empty,
invalid and partial-failure paths. Every behavioral defect in the findings register has a focused
regression. Simple DTO accessors and declarative repository method names are intentionally covered
through integration behavior rather than low-value isolated tests.

Final full-suite evidence on 2026-09-09: `./mvnw test` passed with 109 tests, 0 failures,
0 errors, and 0 skipped. The sandbox emits a non-fatal Spring Cloud network-interface warning,
while every application context and assertion still completes successfully.

`./mvnw clean package` then passed from a clean target, compiling 91 production and 21 test
sources, rerunning all 109 tests, and producing the executable Spring Boot jar. That exact jar was
started temporarily on port 8081 against PostgreSQL 17.11: Hikari connected, Flyway successfully
validated V1–V11, the schema was current at V11, Hibernate initialized, Tomcat started, and
`/v3/api-docs` returned 200. The temporary process was stopped gracefully afterward.

Runtime smoke evidence against the healthy deployment: OpenAPI 200; a public stock page with valid
pagination/sort 200; negative page 400; non-public sort field 400; absent stock 404; protected user
listing without a session 401; and unsupported method 405. Conflict, authenticated ownership, and
state-changing success paths are covered by the 109-test isolated suite rather than mutating the
deployment dataset.

Final mechanical reconciliation reports 91 production Java sources, 21 test classes, 109 test
methods, six controllers, 26 mappings (including two fully-qualified `PostMapping` annotations),
seven JPA entities plus the auditable base class, seven repositories, five external clients, and
11 Flyway migrations. These totals match the class, endpoint, persistence, integration, and test
inventories above.

Final worktree review found no staged files and `git diff --check` reported no whitespace errors.
Implementation changes for this audit remain within backend source, tests, runtime properties, and
this OpenSpec change. Frontend and Nginx are absent from the diff. The Dockerfile, `.dockerignore`,
README, `.env.example`, deployment-alignment change, archived OpenSpec moves, and rotated-log state
were already present in the captured baseline and were not edited by this audit session; they remain
unstaged and are not claimed as part of this change.

## Pause checkpoint — 2026-09-08 22:15 America/Sao_Paulo

The user requested a deliberate pause before the daily usage window ended. OpenSpec reports
**11/45 tasks complete** and **34 remaining**. No Maven or server process is intentionally left
running, and no file is staged.

Completed and persisted:

- Baseline plus exhaustive class/resource/migration inventory and the 26-route endpoint matrix.
- User/login audit: password encoding and omission, password preservation/replacement, generic 401,
  session rotation, and self-only user lookup/list/update.
- Broker audit: CNPJ format/normalization, empty external response handling, CVM validation,
  statuses, tests, and OpenAPI.
- Portfolio audit: authenticated owner assignment, scoped lists/lookups, forged-ID regression,
  errors, tests, and OpenAPI.
- Stock/quote/history audit: ticker format, empty Brapi body, UTC quote/history persistence,
  reversed-period 400, partial batch behavior/logging, tests, and OpenAPI.
- Operation contract audit: exclusive identifiers, positive inputs, ownership/rollback, validated
  fallback quotes, canonical 201 Location, responses, tests, and OpenAPI.
- The additional `GET /carteiras/{id}/resumo` route is represented in matrix, ownership tests, and
  OpenAPI.

Latest focused evidence:

- User/auth/ownership/CORS: 22 tests PASS.
- Broker/CNPJ/CVM/error decoder: 15 tests PASS.
- Portfolio/ownership/summary: 16 tests PASS.
- Stock/quote/catalog: 27 tests PASS.
- Purchase/sale/persistence/ownership: 24 tests PASS.
- Spotless was applied after each change set. It also mechanically formatted already-modified
  `CarteiraService`, `OperacaoService`, and `OperacaoResourcePersistenceIntegrationTest`; no
  semantic rollback or unrelated source rewrite was performed.

Exact resume point:

1. Continue task 2.8 in `GlobalExceptionHandler`: verify standardized 400 for malformed JSON,
   invalid path/query types and method validation, and 409 for optimistic-lock conflicts. The
   handler for `HandlerMethodValidationException` has already been added.
2. Implement task 2.9 with centralized raw `page`/`size`/`sort` validation plus per-endpoint public
   sort whitelists translated before repository pagination. Do not rely on Spring silently clamping
   invalid values.
3. Continue task 2.4 and the 3.x financial matrix, especially missing quote/null semantics,
   zero/reopened positions, multiple sales, costs, rounding, and concurrency.
4. Run the complete suite and package only after the remaining corrections; the full 80-test suite
   and clean package passed at baseline, but have not yet been rerun after the latest edits.

Worktree caution remains active: many pre-existing archived OpenSpec, README, environment,
configuration, Docker, and backend changes belong to earlier initiatives. Resume with explicit
paths and never reset or stage broadly.
