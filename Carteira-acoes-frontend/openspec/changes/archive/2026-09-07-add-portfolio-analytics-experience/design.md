## Context

See [proposal.md](proposal.md) and the three capability specs for behavior. The current Angular app has typed resource services and local signal state, but its existing DTOs expose positions and current quotations only. The backend will add the authoritative financial analytics and quotation-history contract required by this frontend change.

## Goals / Non-Goals

**Goals:**

- Keep financial calculations authoritative: render backend-supplied aggregates and never reconstruct unavailable costs, realized results or price history in the browser.
- Establish a reusable mapper/view-model boundary between API DTOs and chart components.
- Deliver accessible, responsive financial visualizations with one chart library and clear loading, empty and failure states.
- Preserve existing routing and operation request contracts while enriching the views after successful refreshes or operations.

**Non-Goals:**

- Creating backend calculations, persistence or endpoints in this frontend repository.
- Synthesizing chart points, results, prices or authenticated user context.
- Adding multiple chart libraries, client-side chart persistence or live streaming.

## Decisions

### Use backend analytics DTOs as the financial source of truth

The frontend will add typed responses for the now-defined backend contract:

- `GET /carteiras/{id}/acoes` returns a paginated `PosicaoAcaoResponse` with stock metadata, quotation, current value, average sale price, realized/unrealized result and total result;
- `GET /carteiras/{id}/resumo` returns asset count plus independent `valorAtual` and `lucroPrejuizo` subtotals by currency;
- `GET /acoes/{id}/historico?page=0&size=100&sort=dataHora,asc` returns paginated `{ id, acaoId, cotacao, dataHora }` points and accepts optional `inicio` and `fim` filters;
- bodyless `POST /acoes/atualizar-cotacoes` returns `{ total, atualizadas, falhas, dataHora }`; and
- individual refresh retains `POST /acoes/{id}/cotacao`.

Nullable or omitted analytics values map to an unavailable presentation state, not to zero. Currency values are formatted in their supplied position/summary currency and never converted or summed across BRL and USD. This is safer than deriving values from incomplete operation history, which would misstate portfolio performance.

### Adopt ng-apexcharts as the sole visualization dependency

Use `ng-apexcharts` for donut, horizontal bar and line charts. It provides responsive options, legends, tooltips and dynamic series replacement with Angular bindings. Chart.js wrappers and custom SVG were considered, but would add either more adapter work or separate rendering/accessibility responsibilities without an advantage for the three required chart types.

### Separate orchestration, mappers and visual components

Feature pages request data through typed services and transform it into immutable chart view models. Shared chart components accept only prepared labels, series and semantic state (`loading`, `empty`, `error`, `ready`); they do not inject HTTP services. A `PortfolioAnalyticsMapper` owns composition and result mapping, while a `QuoteHistoryMapper` owns time-series mapping. This keeps API-contract changes localized and makes chart rendering independently testable.

### Refresh related financial state as a coordinated page action

Individual quote refresh reloads the stock and its history only after the refresh response succeeds. Global refresh reloads dashboard and stock list summaries after its response succeeds. Purchase and sale navigate back or close only after success, and the receiving portfolio page reloads its summary, positions, charts and operation history. Requests are disabled while pending to avoid duplicate commands.

### Apply financial visual hierarchy through CSS tokens and Material behavior

Add the requested `--space-*` tokens and use a 1440px page container. Material continues to supply dialogs, menus, tooltips, form controls and feedback behavior; CSS supplies layout, metric emphasis and responsive chart grids. Positive/negative styles include signed text and labels in addition to restrained color. Chart panels use skeleton content during every reload, so stale data is never portrayed as current.

## Risks / Trade-offs

- [Backend analytics fields or endpoint shape differs from the agreed contract] → Keep DTO mapping at the service boundary and contract-test the documented endpoints before enabling each visual element.
- [Charts can be inaccessible as canvas/SVG-only output] → Render a textual legend/table alternative, labels and accessible summaries beside every chart.
- [Global quote refresh is slow] → Disable duplicate triggers, announce progress and retain a retry-safe failure state.
- [Large quote histories affect initial rendering] → Let the backend filter history by requested period where supported and render only returned points.
- [Financial data is absent during backend rollout] → Render explicit unavailable/empty states and do not use fallback calculations.

## Migration Plan

1. Add and contract-test backend analytics and quote-history endpoints.
2. Add frontend DTOs, services, mappers and the chart dependency.
3. Deliver charts and financial summaries behind authoritative response availability.
4. Rework portfolio, stock and operation presentations, then verify responsive and accessibility alternatives.
5. Roll back by serving the previous frontend build; no client-side data migration is required.
