## Context

See `proposal.md` for motivation. The application already separates resources, services, repositories, DTO mappers and quote adapters. `Acao` stores only the latest quote; `Operacao` stores immutable purchase/sale facts and `CarteiraAcao` is the current-quantity projection. Existing monetary columns use `BigDecimal` and migrations V1–V9 establish the current schema.

The new reads require derived values based on operations and latest quotes, while quote updates must retain every successful observation. A portfolio can contain BRL and USD positions, and no exchange-rate service exists.

## Goals / Non-Goals

**Goals:**

- Persist quote observations consistently with successful current-quote updates.
- Produce deterministic financial position and per-currency summary views from immutable operations.
- Isolate a global refresh failure to the affected stock and retain the current quote on failure.
- Preserve existing pagination, decimal precision and standardized API errors.

**Non-Goals:**

- Currency conversion, a cross-currency grand total, tax reporting, realized-result persistence, operation editing or deletion.
- Backfilling historical quotes that were not obtained by the application.
- Introducing a direct stock-creation API or client-provided sale price.

## Decisions

### Quote history is an append-only entity

Introduce `HistoricoCotacao` with `id`, mandatory lazy `Acao`, `cotacao` (`DECIMAL(19,4)`) and UTC `dataHora`, plus an index suitable for action and timestamp queries. On an imported stock or every successful refresh, the service will update `Acao.cotacaoAtual`/`dataHoraCotacao` and append the history record in the same transaction. A failed or invalid provider result changes neither record.

The alternative of deriving chart history from operations or retaining only the current quote was rejected: neither represents real market observations over time.

### Global refresh has one isolated unit of work per stock

The global endpoint will enumerate registered stocks, then invoke a separate transactional refresh operation for each. It will catch and classify exceptions per stock, count success/failure, and return a UTC timestamp after all attempts. The orchestration itself will not wrap all stocks in one transaction, so a failed provider call or database write cannot roll back successful updates. Processing is sequential in this version to avoid provider rate bursts and contention.

An all-or-nothing transaction was rejected because it conflicts with the required partial-success response. Parallel refresh was deferred because it would need explicit rate-limit and concurrency controls.

### Derived financial state is calculated from ordered operations

A dedicated calculation component in the service layer will consume operations for one `(carteira, acao)` ordered by `dataHora` then `id`, maintaining `quantidadeAtual`, `custoRestante`, `resultadoRealizado`, quantity/revenue sold, and the current average acquisition cost. For purchases, it adds quantity and gross value plus all costs to the remaining cost. For sales, it captures `quantidade × custoMedioVigente` as cost base, subtracts it from remaining cost, and adds net sale proceeds minus that cost base to realized result. It then derives average sale price, unrealized result using the latest quote, and total result.

The calculation will be performed once per active position returned in a page, with repository queries designed to fetch the relevant operations without an N+1 association traversal. Values are DTO-only rather than columns on `CarteiraAcao`, preventing cache invalidation and divergence from operation history. Persisting running averages was rejected because correcting historical data or concurrent operation behavior would require delicate synchronization.

### DTO contracts retain currency boundaries

`PosicaoAcaoResponse` will replace the narrow position response for `GET /carteiras/{id}/acoes` and include action identity, market, `Moeda`, quantity, latest quote metadata, current value, average sale price and the three result values. `CarteiraResumoResponse` will contain `carteiraId`, `nome`, `quantidadeAtivos` and `resumosPorMoeda`; each `ResumoPorMoedaResponse` carries `moeda`, `valorAtual` and `lucroPrejuizo`. Grouping is performed only after each position is calculated.

Returning one scalar total was rejected because it would silently add BRL and USD. Adding exchange conversion was rejected because no reliable rate/source or conversion-date policy is in scope.

### Operation amounts use direction-aware cash semantics

The operation creation service will normalize absent costs to zero. It will keep `valorBruto` as quantity times unit price. For a purchase, total acquisition cost is gross plus brokerage, taxes and additional cost; for a sale, net proceeds are gross less those costs. The calculation component will use these directional amounts, and request validation continues to reject negative costs and invalid stock identifiers.

Treating all operations with the previous single arithmetic expression was rejected because it cannot correctly model purchase cost basis and sale proceeds.

## Risks / Trade-offs

- [Large portfolios require many operation rows to derive values] → query only page positions and their relevant operation histories, add indexes for portfolio/action/time, and profile before adding a read model.
- [Legacy operations may have stored values using earlier cash semantics] → recompute cost/proceeds from immutable quantity, unit price and cost fields, and cover migration compatibility with tests.
- [Identical operation timestamps produce ambiguous ordering] → use operation ID as a stable secondary chronological order.
- [Individual refresh failures reduce data freshness] → retain the last known quote, record no false history point, and expose failure count.
- [A quote timestamp can be earlier than a prior provider response] → persist the provider timestamp as the observation time; chart consumers can sort deterministically by timestamp and ID.

## Migration Plan

1. Add a versioned Flyway migration for `historico_cotacao`, foreign key, decimal and UTC timestamp columns, and query indexes; do not alter prior migrations.
2. Add the entity, repository, DTOs and quote-update path, then test the individual and global update contracts.
3. Add operation-history query support and the calculation component, followed by enriched positions and currency-segmented summary endpoints.
4. Update operation amount handling and validate purchase/sale/result edge cases, including zero positions and costs.
5. Deploy the application with the additive migration. Existing stocks begin accumulating history only after subsequent successful quote retrievals.

Rollback deploys the prior application version. The additive migration and accumulated history remain intact; no applied Flyway migration is removed or edited.
