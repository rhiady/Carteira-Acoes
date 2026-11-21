## 1. Purchase contract

- [x] 1.1 Remove `ticker` and `mercado` from the typed purchase request and update every purchase caller, fixture and contract test to send `acaoId` only.
- [x] 1.2 Keep the existing purchase service endpoint and optional order-cost serialization unchanged while verifying its request body contains no legacy asset reference.

## 2. Registered-action selection

- [x] 2.1 Update the purchase page to load and present the existing paginated action catalog when no contextual asset is selected, with loading, empty and error states using current UI patterns.
- [x] 2.2 Replace manual ID, ticker and market controls with one accessible registered-action selection; block submission and provide clear feedback until an action is selected.
- [x] 2.3 Preserve a contextual position selected through the route, its `acaoId` and its current price suggestion without asking the user to reselect it.

## 3. Verification

- [x] 3.1 Add focused purchase-page coverage for catalog selection, missing selection, contextual position selection and the absence of legacy asset controls.
- [x] 3.2 Extend API contract coverage to assert that a purchase posts `acaoId` without `ticker` or `mercado`.
- [x] 3.3 Run focused tests, TypeScript/build checks, accessibility checks for the changed form, and `openspec validate --type change require-stock-id-for-purchases`.
