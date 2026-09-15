## 1. Foundation and application shell

- [x] 1.1 Install and configure Angular Material with the customized financial theme, global CSS tokens, and accessible focus/semantic-state styles.
- [x] 1.2 Create the `core`, `shared`, `layout`, and `features` folder boundaries and the centralized environment API URL configuration.
- [x] 1.3 Create typed API DTOs for pages, errors, users, brokers, portfolios, stocks, positions, and operations, including default pagination parameters.
- [x] 1.4 Add the global HTTP error interceptor and reusable API error-message extraction without exposing technical details.
- [x] 1.5 Implement the main layout, responsive sidebar, toolbar, page-title resolution, and root redirect to `/dashboard`.
- [x] 1.6 Configure lazy feature routes for dashboard, portfolios, stocks, operations, brokers, and users.

## 2. Shared UI and data presentation

- [x] 2.1 Implement reusable page header, loading state, empty state, and confirmation dialog components with keyboard-accessible behavior.
- [x] 2.2 Implement reusable currency, market, and operation-type presentation components using locale-aware currency and local-time date formatting.
- [x] 2.3 Establish responsive card, table, dialog, snackbar, and mobile row presentation patterns that preserve information and actions.

## 3. API services and administrative features

- [x] 3.1 Implement typed user service methods for create, paginated list, and detail retrieval using `HttpParams`.
- [x] 3.2 Implement user list, create dialog, and detail page with reactive validation, loading, empty, pagination, and error states.
- [x] 3.3 Implement typed broker service methods for create, paginated list, detail retrieval, and CNPJ lookup.
- [x] 3.4 Implement broker list, CNPJ-only creation dialog, lookup feedback, and detail page, including pending external-validation state and backend failures.

## 4. Portfolio and stock features

- [x] 4.1 Implement typed portfolio service methods for create, list, detail, user portfolios, and position retrieval.
- [x] 4.2 Implement portfolio listing and creation dialog with populated user/broker selections and dependency-aware validation.
- [x] 4.3 Implement portfolio detail with position enrichment from stock details, contextual actions, empty state, and refreshable loading/error state.
- [x] 4.4 Implement typed stock service methods for paginated list, detail, normalized ticker lookup, and quote refresh.
- [x] 4.5 Implement stock list, confirmed ticker search, responsive details, contextual purchase entry point, and duplicate-safe quote-refresh feedback.

## 5. Operations and history

- [x] 5.1 Implement typed operation service methods for purchase, sale, detail retrieval, and per-portfolio history.
- [x] 5.2 Implement the purchase dialog with exactly-one asset-reference mapping, optional costs, client estimate labeling, and request validation.
- [x] 5.3 Implement the sale dialog initiated from a position, available-quantity validation, optional costs, and explicit confirmation.
- [x] 5.4 Implement portfolio-scoped operation history with portfolio selection, responsive fields, pagination when supplied, and operation-detail retrieval.
- [x] 5.5 Wire successful purchase and sale to dialog close, snackbar feedback, and position/history/dashboard refresh without treating estimates as official values.

## 6. Dashboard and validation

- [x] 6.1 Implement the dashboard using only existing endpoints, explicit temporary user context, derived counts/previews, and a first-portfolio empty state.
- [x] 6.2 Ensure the dashboard omits patrimônio when authoritative responses cannot safely derive it and does not simulate authentication or financial data.
- [x] 6.3 Add unit tests for DTO request mapping, pagination defaults, error fallback, purchase exclusivity, sale quantity validation, and formatting helpers.
- [x] 6.4 Add component/integration tests for critical loading, empty, failure, retry, post-operation refresh, and quote-refresh duplicate-prevention scenarios.
- [x] 6.5 Run Angular build, lint/type checks, and accessibility verification for keyboard navigation, dialogs, contrast, desktop, tablet, and mobile layouts; correct resulting issues.
