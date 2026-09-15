## Context

The backend has an existing email/password authentication flow, user domain, portfolio ownership relation, and financial services. The implementation must build on those contracts and remain limited to backend code; deployment configuration may be inspected but not changed.

## Goals / Non-Goals

**Goals:**

- Reuse the current login and registration endpoints and authentication mechanism.
- Centralize ownership resolution so every portfolio-derived operation applies the same rule.
- Keep denied financial mutations atomic and preserve existing domain validations and error handling.
- Cover IDOR attempts and credential-safety behavior with integration/service tests.

**Non-Goals:**

- OAuth, MFA, roles, refresh tokens, password recovery, email confirmation, or frontend changes.
- A new parallel login or registration endpoint.

## Decisions

- Use the existing authentication context as the sole source of the current user. This avoids trusting request IDs; a separate token/session architecture would be disproportionate to the project.
- Add one service-level ownership lookup/guard and route portfolio, position, operation, purchase, and sale flows through it. Repository queries may use owner-constrained lookups where they simplify not-found behavior, while controllers remain HTTP-focused.
- Preserve the current public contracts whenever possible. For user-scoped endpoints, a mismatched identifier is treated as unauthorized/not found according to the existing exception handler rather than exposing a new error model.
- Keep purchase and sale methods transactional at the service boundary, performing ownership checks before any operation or position write. This preserves all-or-nothing behavior without introducing a new transaction architecture.
- Preserve the project's password encoder if present; otherwise use the existing Spring security configuration's encoder. Never map password fields into response DTOs.

## Risks / Trade-offs

- [Some endpoints may have inconsistent current ownership assumptions] → inventory every controller and service using `usuarioId`, `carteiraId`, or `operacaoId`, then add cross-user tests for each path.
- [Existing clients may depend on user-supplied IDs] → preserve request shapes while ignoring/rejecting conflicting ownership fields, and document the effective authenticated-owner behavior.
- [A missing transaction boundary could leave partial financial changes] → verify the mutation service is transactional and test database state after denied purchase and sale attempts.
- [Legacy plaintext passwords may exist] → inspect stored password format and retain compatibility only if safe; new and changed passwords must always be encoded.

## Migration Plan

1. Implement ownership and credential-safety changes with tests.
2. Run the full Maven test suite and build, then validate the existing runtime contract.
3. Deploy the backend normally; no schema migration or deployment-file change is expected.
4. Roll back the application artifact if needed; preserve the database because the change does not require destructive data operations.
