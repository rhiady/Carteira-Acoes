## Context

See `proposal.md` and the delta specs. `CompraPage` currently supports two asset locators: a selected `acaoId` or manually entered ticker and market. The published backend change rejects the latter. The frontend already has typed action data, paginated action listing, a contextual position path and one purchase service boundary.

## Goals / Non-Goals

**Goals:**

- Make one registered action selection the only source of a purchase `acaoId`.
- Preserve contextual purchases by translating an existing selected position directly to its `acaoId`.
- Reuse the action listing, page envelope, Angular Material form controls, existing error handling and the current purchase layout.
- Cover the serialized request and both contextual and catalog-selection flows with focused tests.

**Non-Goals:**

- No action creation, ticker lookup at submission time, quote-refresh behavior, backend endpoint, financial calculation or new dependency.
- No change to sale payloads or to the wallet/ownership authorization model.

## Decisions

### Use the published action list as the non-contextual selector

Load registered actions through the existing paginated action resource and bind the selected item's existing `id` as `acaoId`. This makes an invalid legacy locator impossible to serialize through the form. A free-text ticker lookup was rejected because it reintroduces a value that the purchase contract no longer accepts.

### Preserve contextual positions without a second selection model

When the route identifies a current position, keep its asset selected and use the published `acaoId`; its price suggestion remains unchanged. This avoids duplicate action lookups and keeps the existing fast repeat-purchase journey. Requiring catalog reselection for every contextual action was rejected because it loses valid route context without improving the request contract.

### Remove legacy DTO fields at the contract boundary

Delete `ticker` and `mercado` from the purchase request type and build the request from the selected action's ID plus existing order values. Type-level removal gives callers and tests a compile-time signal instead of relying only on UI validation.

## Risks / Trade-offs

- [The action list is paginated and may not include every action in its first response] → use the existing pagination/listing pattern so a person can reach available registered actions; do not fabricate lookup results.
- [A preselected contextual action is deleted or unavailable] → keep the form blocked and show the existing safe backend/UI error when validation or submission rejects it.
- [Older callers compile against ticker/market purchase fields] → update every `CompraRequest` caller and contract fixture in the same change; the breaking DTO is deliberate.

## Migration Plan

1. Deploy the DTO, selector and focused test changes together with the backend contract that requires `acaoId`.
2. Monitor purchase validation errors for stale deployed clients; no data migration is needed.
3. Roll back by reverting the frontend release only if the backend restores support for ticker/market; otherwise legacy clients remain incompatible by backend design.
