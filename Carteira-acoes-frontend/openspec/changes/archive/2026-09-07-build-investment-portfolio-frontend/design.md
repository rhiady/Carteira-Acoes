## Context

The Angular application consumes a local Spring Boot API and already has OpenSpec capabilities for API integration and visual experience. See [proposal.md](proposal.md) for motivation and the associated delta specifications for the required behavior. The API is intentionally limited to creation and consultation flows, with quote refresh as the only update operation; it has no authentication or dashboard endpoint.

## Goals / Non-Goals

**Goals:**

- Build a route-oriented, responsive investment interface around the published DTOs and endpoints.
- Keep HTTP access, API errors, pagination, formatting, and shared feedback consistent across features.
- Make the portfolio → position → purchase/sale → history workflow the primary experience.
- Keep derived UI estimates clearly separate from backend-authoritative operation values.

**Non-Goals:**

- Adding backend endpoints, changing DTOs, or creating client-side persistence for financial data.
- Authentication, authorization, token storage, edit/delete controls, direct stock creation, and charts based on synthetic data.
- Resolving concurrent position updates from the UI `version` field without a backend contract.

## Decisions

### Layer features around typed core services

Create `core/config`, `core/models`, `core/services`, and `core/interceptors`, with one typed service per published resource. Feature pages own orchestration and presentation state, while dialogs own their reactive form state. Shared components own reusable visual states and semantic display primitives.

This separates transport concerns from feature flows and makes DTO changes localized. A generic data-access service was considered but rejected because the resource endpoints and operations have materially different contracts.

### Use a shell with lazy feature routes

`AppComponent` hosts a main layout containing the sidebar, toolbar, and router outlet. `/` redirects to `/dashboard`; Dashboard, Carteiras, Ações, Operações, Corretoras, and Usuários load by feature route. The layout uses a fixed desktop navigation and an overlay navigation at narrower widths.

This keeps navigation consistent and bounds initial feature loading. A separate top-level layout per feature was rejected because it duplicates responsive navigation and page title behavior.

### Keep API state local and signal-driven

Pages represent idle/loading/success/error state with signals and derive display state with `computed`. HTTP orchestration uses RxJS operators such as `switchMap`, `forkJoin`, `catchError`, and `finalize`; subscriptions are bounded with `takeUntilDestroyed`. Existing content stays visible when a page replacement request fails.

This avoids introducing a global store for a small application while retaining predictable state transitions. A global state library was rejected as disproportionate to the available API and scope.

### Implement forms as Reactive Forms with explicit request mapping

User, broker, portfolio, purchase, and sale dialogs use reactive forms. Before submission, purchase mapping emits either `{ acaoId }` or `{ ticker, mercado }`, never both, and only emits optional price/cost values when supplied and valid. Sale mapping accepts a position-selected `acaoId` and applies client-side available-quantity feedback, while backend validation remains final.

This makes the API contract observable in a single mapping boundary. Binding raw form values directly to DTOs was rejected because optional fields and mutually exclusive purchase references would be error-prone.

### Compose only supported dashboard information

The dashboard selects a temporary user context because there is no authentication contract, then combines existing list, portfolio, position, and action responses as required. Position enrichment calls `/acoes/{acaoId}` only to show action metadata. Counts and previews are displayed only when derivable; consolidated patrimônio is absent unless the needed authoritative inputs permit a safe calculation.

This keeps the UI useful without inventing a `/dashboard` backend. A synthetic demo user or hard-coded financial metrics was rejected because it would misrepresent application data.

### Apply Material behavior with a custom CSS design system

Angular Material supplies accessible controls, dialogs, navigation, tables, menus, feedback, and loading indicators. Global CSS customizes visual tokens for the prescribed restrained palette, spacing, typography, surfaces, and focus states. Responsive table alternatives preserve fields and actions on mobile rather than hiding data.

This preserves component accessibility while avoiding the default Material aesthetic. Replacing Material with bespoke controls was rejected due to the accessibility and interaction scope.

## Risks / Trade-offs

- [Backend pagination behavior may differ for carteira positions or history] → Keep pagination adapters tolerant of the published page envelope and validate against running API responses before release.
- [Position enrichment can make multiple stock requests] → Request only visible position details, combine calls with `forkJoin`, and avoid persisting duplicate data.
- [No authenticated user context] → Make selected/default context explicitly temporary, keep it isolated from authorization, and avoid claims of logged-in identity.
- [Quote, broker, and operation requests can be slow or fail externally] → Use localized loading, disabled duplicate actions, backend messages, and safe retries.
- [Mobile tables may be dense] → Use a responsive row/card presentation while keeping labels, values, actions, keyboard access, and screen-reader context intact.

## Migration Plan

1. Add the shell, theme, models, centralized configuration, services, interceptor, and shared UI primitives.
2. Introduce feature routes and implement in dependency order: users, brokers, portfolios, stocks, operations, then dashboard.
3. Validate each feature against the local backend and run Angular checks plus accessibility checks at desktop and narrow breakpoints.
4. Roll back by deploying the preceding frontend build; this change neither migrates server data nor changes API contracts.

## Open Questions

None. The temporary selected-user context remains a visible frontend convention until the backend publishes authentication or a current-user endpoint.
