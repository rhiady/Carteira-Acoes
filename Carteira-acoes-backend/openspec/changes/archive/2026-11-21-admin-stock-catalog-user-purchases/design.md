## Context

See `proposal.md` for motivation and the delta specs for the public contract. The system already has the correct aggregate for the administrative flow: `POST /api/acoes` is admin-protected, imports ticker data and its current quote, and persists no portfolio data. In contrast, `CompraRequest` currently accepts either `acaoId` or `ticker`/`mercado`; `OperacaoService` calls `AcaoService.resolverOuCriar` for the latter, creating a global action inside a personal purchase.

## Goals / Non-Goals

**Goals:**

- Make the existing administrative action-creation endpoint the sole entry point that imports a ticker.
- Make the purchase contract unambiguous: a purchase references one persisted `acaoId`.
- Preserve manual prices, quote fallback, ownership enforcement, financial calculations and operation atomicity.

**Non-Goals:**

- No `CarteiraAdmin`, administrative inventory, stock quantity, or administrative financial operation.
- No database migration: `Acao`, `Carteira`, `CarteiraAcao` and `Operacao` persist the same relationships.
- No change to sale behavior, quote-provider selection, or existing actions and operations.

## Decisions

### Use `Acao` as the unowned administrative catalog

`POST /api/acoes` remains the administrator's action-discovery flow. Its existing provider import and validation are reused; a successful request stores one global `Acao` and quotation history, but does not touch a portfolio or operation.

An administrative wallet or a second action entity was rejected because neither represents financial ownership in this flow and both would duplicate catalog data and rules.

### Make `acaoId` mandatory in `CompraRequest`

Remove `ticker`, `mercado`, the exclusive-or identifier validation, and the ticker-oriented convenience path from the purchase request. `OperacaoService.comprar` resolves the required ID through the existing `AcaoService.obterEntidade`; it must no longer call `resolverOuCriar`. This makes attempts to send legacy ticker fields fail validation or be rejected by the JSON contract rather than silently importing an asset.

Keeping a role-specific ticker branch was rejected: it leaves two meanings for a purchase endpoint and makes authorization part of catalog creation harder to audit. The admin uses the explicit catalog endpoint, then any permitted purchase uses the returned `acaoId`.

### Retain the existing price source behavior

The action's saved quote is presented by clients as the suggested initial value. The backend continues to persist an explicitly supplied `precoUnitario` unchanged; when it is absent, it obtains and validates a current quote through the existing adapter before recording the purchase. This preserves the current reliability behavior rather than treating an older stored catalog quote as a guaranteed execution price.

### Remove the now-dead import helper only after callers are migrated

After production and test callers no longer use it, remove `AcaoService.resolverOuCriar` and its test coverage. `AcaoService.criar` remains the shared, transactional catalog-creation path.

## Risks / Trade-offs

- [Existing clients post ticker/mercado to purchases] → This is an intentional breaking change; document it in OpenAPI/README and have clients first call the admin catalog flow, then submit `acaoId`.
- [A client relies on unknown JSON properties being ignored] → Validate the purchase payload so ticker/mercado cannot bypass the contract; add resource-level integration coverage.
- [Quote provider is unavailable when a user omits price] → Preserve the current 503/502 outcome and allow explicit valid manual price to avoid the provider dependency.
- [A catalog action is deleted while clients select it] → Reuse the existing reference/integrity validation; a purchase resolves the ID in its transaction and fails without creating partial data if unavailable.

## Migration Plan

1. Deploy the backend contract and documentation together with frontend selection by `acaoId`.
2. Existing catalog actions, portfolios, positions and operations require no migration.
3. Before deployment, notify clients that ticker/market purchases are no longer accepted; admins must create the catalog action first.
4. Roll back by restoring the previous application artifact. No data cleanup or schema rollback is required.
