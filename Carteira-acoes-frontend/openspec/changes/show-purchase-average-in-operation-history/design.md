## Context

See `proposal.md` and the delta spec for behavior. `Operacao` does not contain a cost average, while the existing position resource publishes the authoritative `precoMedio` for each currently held asset.

## Goals / Non-Goals

**Goals:**

- Present the published cost average beside purchase records without changing the historical operation payload.
- Keep operation history usable when the supplementary position lookup is unavailable.

**Non-Goals:**

- Do not reconstruct a weighted average from paginated purchases.
- Do not show a purchase average on sales or substitute `precoMedioVenda`.

## Decisions

### Reuse current positions as the source of truth

Load the existing complete position list with the history request and index it by asset identifier. This reuses the backend-authoritative `precoMedio` and preserves its currency. Recalculating from the history was rejected because the response is paginated and would not be authoritative.

### Treat positions as supplementary context

If position loading fails, retain the successful operation history and render `—` for purchase averages. Failing the entire history for contextual data was rejected because each operation remains valid and readable.

## Risks / Trade-offs

- [An asset was fully sold and has no current position] → render `—`; no historical average is inferred.
- [Position lookup adds a concurrent request] → use the existing aggregate position helper and no new endpoint.

## Migration Plan

1. Deploy the history view and focused tests together.
2. Roll back by reverting the history-page position lookup and presentation; no API or data migration is required.
