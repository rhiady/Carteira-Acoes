## Context

See proposal.md for motivation and the delta specs for behavior. `CompraPage` currently combines asset-ID and ticker/market input for every authenticated person, while `CarteiraService.positions` already exposes the authoritative position, currency and current quote needed for a contextual choice. `OperacaoService.buy` accepts the existing `CompraRequest`; no endpoint supports choosing a third party wallet. `CarteirasPage` already loads user-scoped wallets, summaries and a small ticker preview, but renders every unit as a dense, similarly weighted card.

The visual audit found no detector violations. The work is a scoped refinement of the incumbent Swiss financial system, not a replacement visual world: neutral surfaces, Helvetica-style typography, 1px rules and one Yves Klein blue action color remain authoritative. The signature move is an investment list that turns a selected position into one compact, editable order rail, so the authoritative quote reads as a starting point rather than a hidden assumption.

## Goals / Non-Goals

**Goals:**

- Branch only the purchase composition by authenticated authority while preserving the current route and request lifecycle.
- Keep the user-common path tied to the positions returned for the selected wallet, with one selected order at a time and a visible, editable suggested price.
- Recompose the wallet index using existing summary and preview data so financial context precedes technical metadata and redundant card chrome is removed.
- Retain Angular Material controls, current formatters, service boundaries, semantic theme tokens, keyboard flow, status messaging and responsive stacking.

**Non-Goals:**

- No endpoint, DTO, quote lookup, market lookup, permission relaxation, client-side financial calculation, currency conversion or new state library.
- No administrator ability to buy into another person's wallet, bulk purchase, inline editing of multiple orders, or recreation of the portfolio-detail summary change.
- No global redesign, new design system, visual texture, gradients, invented financial values or decorative dashboard content.

## Decisions

### Keep one purchase route and branch the asset-selection region by authority

Use the authenticated authority only to select the first stage of the existing purchase page. The administrator retains the current complete asset-reference fields (ID or ticker plus market). The common-user path loads only the selected wallet's positions, lets the person select one, then derives the existing `acaoId` reference from that selection.

Duplicating routes or pages by role was rejected because validation, costs, confirmation, payload construction and error handling are shared. A user-side ticker search was rejected because it would defeat the requested simplified path and introduce an unneeded choice.

### Make the administrative purchase route discoverable

Expose `Carteiras` in the administrator entry and persistent administrative navigation. It reuses the existing authenticated wallet list and detail route, whose purchase action already reaches `CompraPage`; ownership validation remains unchanged. Adding a separate administrative purchase route was rejected because it would duplicate the same wallet context and could imply unsupported cross-owner access.

### Treat a quote as an editable seed, not an official calculated result

When the selected position has `cotacaoAtual`, place that value into the existing unit-price form control. Keep the control editable and identify it as the current quote suggestion; if absent, leave the control ready for manual entry and state that no quote is available. The existing estimate remains explicitly non-authoritative, and the request sends exactly the entered positive price when one is supplied.

Locking the price to the quote was rejected because the user explicitly needs to override it. Recalculating a price or deriving it from history was rejected because only the position quote is authoritative here.

### Preserve ownership and current request validation at the page boundary

Continue loading and validating the wallet before enabling confirmation. For common users, reject unavailable or foreign wallets as today and never fall back to a global position list. For administrators, retain the same eligible-wallet constraint until an API contract publishes administrative wallet selection. Keep identifier exclusivity in the shared submit path, even though the common-user UI supplies only `acaoId`.

Relying on hidden UI alone was rejected because ownership must remain enforced before any purchase request.

### Recompose wallet cards into a scan-first financial list

Use the already loaded wallet, per-currency summary and ticker preview. Remove wallet and broker identifiers from the primary visual tier; show them only when meaningful secondary context exists. Make the wallet name, per-currency current value, textual result, asset count/tickers and `Ver carteira` action the reading order. Use a restrained list/grid that becomes one column on narrow widths, hairline separators and tabular numerals rather than more card elevation or equal-weight metric tiles.

Introducing a new shared wallet-summary component or fetching additional aggregate data was rejected because the existing page owns the data and the current responses are sufficient.

### Verify role, quote and responsive boundaries at page tests

Extend purchase tests for administrator asset reference, common-user position restriction, quote seeding/override, missing quote and single-reference submission. Extend wallet tests for the financial hierarchy, currency separation, ticker preview and mobile-safe semantic structure. Run AXE plus rendered checks at 375px, tablet and desktop when browser tooling is available.

Snapshot-only coverage was rejected because it would not verify request payloads, role separation or editable quote behavior.

## Risks / Trade-offs

- [A wallet has many positions] → load through the existing paginated position contract with an explicit empty/loading/error state; do not invent a global asset catalog or silently truncate a selectable list.
- [A quote becomes stale before confirmation] → label it as a suggestion and leave backend-returned operation values authoritative.
- [Active role-area work changes session APIs or routing] → depend on `AuthService` authority and current route context only; reconcile imports and guards with that change at implementation time.
- [Wallet hierarchy overlaps the active detail-summary change] → limit this change to `CarteirasPage`; leave totals and analytics in `CarteiraDetailPage` to its owning change.
- [Material/theme CSS affects contrast or compact controls] → reuse semantic tokens and validate focus, dark theme and touch targets in the affected pages.

## Migration Plan

1. Implement the shared purchase safeguards first, then branch only the asset-selection experience and add focused role/quote tests.
2. Recompose the wallet list with existing data and extend list tests without changing its APIs or dialog actions.
3. Validate change specs, focused/full tests, production build, AXE and rendered responsive checks where tooling permits.
4. Roll back by reverting the affected page templates/styles/tests; requests, persisted data and backend contracts remain unchanged.
