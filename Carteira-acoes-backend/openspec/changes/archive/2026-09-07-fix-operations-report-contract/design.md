## Context

See `proposal.md` for motivation. The backend already returns operations by carteira as a paginated response and already calculates position indicators from operation history. The current contract is underspecified for the Operations screen because operation items identify the action but do not expose the display fields needed by a financial report.

## Goals / Non-Goals

**Goals:**
- Make the operations endpoint a stable report contract: paginated response with operation items under `content`.
- Enrich operation responses with action display fields so the frontend does not need one request per `acaoId`.
- Represent "no average sale price exists" as `null`, not `0`.
- Keep `lucroPrejuizo` as the backend-owned value used by the frontend to choose red, green or neutral styling.

**Non-Goals:**
- Add a graph of operations.
- Change the persistence model for operations or positions.
- Replace pagination with a raw array response.
- Move profit/loss calculations into the frontend.

## Decisions

- Keep `GET /operacoes/carteiras/{carteiraId}` paginated. This preserves the existing API shape and makes the frontend fix explicit: read `response.content` for the rows.
- Enrich `OperacaoResponse` from backend data already reachable through the operation's action relationship. This avoids N+1 calls in the frontend and keeps the report screen based on one backend contract.
- Return `null` for `precoMedioVenda` when there is no sale history. Zero remains available for real numeric outcomes and will not be overloaded to mean "not applicable".
- Keep profit/loss color rules in the frontend, but source them only from `lucroPrejuizo` returned by the position endpoint. The backend remains responsible for realized, unrealized and total values.

## Risks / Trade-offs

- Existing frontend code that expects `precoMedioVenda` to always be numeric may need a null check. Mitigation: update the contract and tests together, then render a dash or empty value for null.
- Enriching operation DTOs increases response payload size. Mitigation: include only compact action fields needed for display: ticker, nomeEmpresa, mercado and moeda.
- If operation mapping uses lazy action access outside an active transaction, it may fail or produce inefficient queries. Mitigation: fetch operations with action data or map inside an appropriate transactional/read path.

## Migration Plan

- Update backend DTOs and mapping first.
- Add or update integration tests for paginated operations and position average-sale semantics.
- Update frontend operations screen to read `response.content` and consume enriched fields directly.
- Update frontend position display to render `precoMedioVenda === null` as not applicable.
