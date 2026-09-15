## Context

See `proposal.md` for motivation. `CarteiraResource` receives `Pageable` directly for `GET /carteiras/{id}/acoes` and passes it to `CarteiraService.listarPosicoesAtivas`. The service then calls `CarteiraAcaoRepository.findByCarteiraIdAndQuantidadeGreaterThan(...)`, so Spring Data applies sort fields against `CarteiraAcao`. `CarteiraAcao` has `id`, `quantidade`, `carteira` and `acao`; `ticker` is exposed in `PosicaoAcaoResponse`, but the persisted field lives under the associated `Acao`.

## Goals / Non-Goals

**Goals:**
- Preserve the public API field `sort=ticker,asc|desc`.
- Translate public sort fields to safe persistence paths before invoking the repository.
- Allow only `ticker`, `quantidade` and `id` for position sorting.
- Return a controlled 400 error for unsupported sort fields.

**Non-Goals:**
- Change the endpoint response shape.
- Rename frontend sort parameters to JPA property paths.
- Add broad dynamic sorting over arbitrary entity fields.
- Change position financial calculations.

## Decisions

- Sort translation will happen at the service boundary before the repository call. This keeps `CarteiraResource` thin and keeps persistence details out of the controller contract.
- The whitelist will map public field names to persistence fields: `ticker` to the action ticker path, `quantidade` to the position quantity, and `id` to the position id.
- Invalid sort fields will raise the project's standard validation-style API exception so the global handler returns HTTP 400 rather than an unhandled server error.
- The default no-sort behavior will remain compatible with the current endpoint default of `id,desc`; implementation may inject that default when no explicit sort arrives.

## Risks / Trade-offs

- Overlap with active change `fix-operations-report-contract` on the same `portfolio-catalog` requirement -> merge deltas carefully when syncing specs.
- Using an association sort path can change generated SQL -> cover with integration tests against H2 and inspect that `sort=ticker` succeeds.
- Multiple sort orders in one request can expose unexpected combinations -> validate every requested order against the whitelist before translating.

## Migration Plan

- Add a small sort translation helper for active positions.
- Update `CarteiraService.listarPosicoesAtivas` to use the translated `Pageable`.
- Add integration tests for ticker ascending, ticker descending, pagination plus ticker, invalid sort and no explicit sort.
- Run the focused backend tests and OpenSpec validation before marking the change complete.
