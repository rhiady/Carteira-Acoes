## 1. Contract and visualization foundation

- [x] 1.1 Add strict DTOs and typed service methods for the defined position page, multimoeda portfolio summary, paginated quote history and global quote-refresh outcome contracts.
- [x] 1.2 Add `ng-apexcharts` as the sole chart dependency and create reusable accessible chart-panel, loading-skeleton, empty-state and textual-summary components.
- [x] 1.3 Implement pure portfolio-analytics and quote-history mappers that convert authoritative API DTOs into chart view models without HTTP dependencies.
- [x] 1.4 Add global spacing tokens, the 1440px page container, financial metric styles and responsive chart-grid rules.

## 2. Quote history and refresh flows

- [x] 2.1 Implement quote-history retrieval with documented pagination/sort defaults and optional date-range filters, period selection and a line-chart view with empty, loading, failure and retry states.
- [x] 2.2 Update individual quote refresh to disable duplicates and reload stock details and quote history before rendering the new chart data.
- [x] 2.3 Add the bodyless global quote-refresh service action and pending, partial-failure and success feedback using its returned counts and timestamp.

## 3. Portfolio analytics experience

- [x] 3.1 Implement authoritative multimoeda portfolio summary and enriched position retrieval, including nullable average sale price and no cross-currency total semantics.
- [x] 3.2 Rebuild Dashboard around the temporary portfolio context with value, lucro/prejuízo, asset-count cards, composition donut and result-by-stock bars.
- [x] 3.3 Rebuild the portfolio listing as financial cards with broker, metrics, asset count, ticker preview and contextual navigation.
- [x] 3.4 Rebuild portfolio detail with summary metrics, responsive analytics charts, quote refresh and spacious position cards with accessible metric labels/tooltips.

## 4. Contextual trading and stock presentation

- [x] 4.1 Rework the stock list into an investment-focused presentation with quote refresh, market summaries and 72px-or-greater asset rows.
- [x] 4.2 Rework stock detail with contextual purchase, quote refresh, current quote hierarchy and the quotation-history chart.
- [x] 4.3 Rework purchase into a contextual asset flow with quantity controls, optional-cost disclosure and clearly non-authoritative totals.
- [x] 4.4 Rework sale into a contextual position flow with available quantity, average sale price, optional-cost disclosure, explicit confirmation and post-success refresh.
- [x] 4.5 Refine portfolio-scoped operation history so transaction records remain a secondary history view rather than a primary analytics source.

## 5. Verification and rollout safety

- [x] 5.1 Add unit tests for DTO/mapping behavior, unavailable metrics, signed money formatting, price-history periods and duplicate refresh prevention.
- [x] 5.2 Add component/integration tests for chart loading/empty/error states, portfolio refresh after operations, responsive chart arrangement and contextual trading validation.
- [x] 5.3 Verify keyboard navigation, tooltip/dialog focus management, chart textual alternatives, contrast and desktop/tablet/mobile layouts with the backend analytics responses.
- [x] 5.4 Run Angular tests and production build, validate the OpenSpec change, and document any backend contract mismatch discovered during integration.
