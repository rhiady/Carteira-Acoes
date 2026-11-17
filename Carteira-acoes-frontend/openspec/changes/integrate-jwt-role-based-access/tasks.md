## 1. Model and session contract

- [ ] 1.1 Replace the legacy local-identity auth DTOs with typed login request/response, authenticated identity, authority and session-expiry models matching `/api/auth/login` and `/api/auth/me`.
- [ ] 1.2 Update `AuthService` to submit `{ login, password }`, persist only the token/expiry in `sessionStorage`, restore the token after refresh through `/api/auth/me`, expose signal-based identity/authorities, and clear session plus financial context on logout or expiry.
- [ ] 1.3 Add focused service tests for successful login, identity loading, invalid credentials, refresh restoration, expiry, logout cleanup and unsupported authorities; assert that passwords and token values are not persisted as user identity.

## 2. HTTP authentication and route protection

- [ ] 2.1 Update the HTTP interceptor to omit the Bearer header for `/api/auth/login`, attach it to authenticated requests, handle `401` with one safe session clear and redirect, and handle `403` without clearing the session.
- [ ] 2.2 Extend the existing authentication guard and add the smallest role-aware guard/data convention needed to protect every administrative route with `ROLE_ADMIN` while preserving common-user routes.
- [ ] 2.3 Add route tests covering anonymous redirects, common-user access, admin access, blocked common-user access to admin routes, missing authorities and role-specific default navigation.

## 3. Role-separated application entry experience

- [ ] 3.1 Classify the existing routes according to the published backend contract, keeping user-owned investment flows in the common area and exposing only documented management commands in the admin area.
- [ ] 3.2 Add lazy standalone admin and common-user entry/shell components or reuse existing shared navigation components where they already provide the needed boundary; do not duplicate financial feature pages.
- [ ] 3.3 Update login success and root/wildcard navigation to resolve the authenticated authority and route to the correct area, with an accessible access-denied state and a path back to the permitted area.
- [ ] 3.4 Apply the existing Swiss tokens and shared spacing/typography to both areas, including visible focus, responsive stacking, semantic loading/error states, and no fabricated metrics or labels.
- [ ] 3.5 Add component and route-level accessibility coverage for both areas in light/dark themes and at mobile and desktop widths, including keyboard navigation and access-denied messaging.

## 4. Regression and contract validation

- [ ] 4.1 Update login tests for the published `/api/auth/login` payload, pending state, `/api/auth/me` follow-up and role-specific redirect without exposing sensitive values.
- [ ] 4.2 Update interceptor and API service tests to assert Bearer injection, login exclusion, `401` cleanup/redirect, `403` session preservation and normalized backend errors.
- [ ] 4.3 Run the relevant Angular/Vitest test suites, OpenSpec validation, TypeScript/build checks and AXE checks; manually verify both role areas in light/dark at 375px, 768px and desktop widths.
- [ ] 4.4 Confirm no frontend dependency, backend endpoint, JWT decoding logic, client-side authorization substitute or financial calculation was introduced beyond the published contract.
