## Context

See `proposal.md` and its delta specs. The admin landing page already links to `/usuarios`; the user page already consumes the published list resource. The global action catalog has a listing and detail boundary, but the administrative `POST /acoes` flow is not available from the interface. The published contract creates/imports a global action only; it is not a purchase operation.

## Goals / Non-Goals

**Goals:**

- Restore the protected administrative user list and its existing loading, empty and error states.
- Add one administrative action-catalog form that submits the published `{ ticker, mercado }` payload.
- Reuse routes, services, page states, Material controls and the existing Swiss financial language.
- Test role boundaries, user-list loading and action-catalog request validation.

**Non-Goals:**

- No endpoint, authority, token, backend authorization, financial calculation or generic cross-user search is added.
- No carteira, posição or operação is created by the global action form, and no duplicate action catalog is introduced.

## Decisions

### Reuse the existing administrative action catalog boundary

Add the create/import action to the current action catalog and restrict its visibility and submission to `ROLE_ADMIN`. This preserves the existing global catalog as the single source of actions. Modelling it as a financial purchase was rejected because the backend request has no carteira, posição or operação fields.

### Keep user-list diagnosis at the existing service boundary

The existing `UsuarioService.list` and list-page loading/error/empty states remain the only source for users. The implementation will correct route, guard or rendering defects discovered by focused tests rather than adding a new API fallback.

## Risks / Trade-offs

- [Backend rejects an admin request despite frontend role] → show the normalized backend message and retain the form values for correction.
- [The action already exists] → present the backend message and do not fabricate a second catalog entry.

## Migration Plan

1. Deploy the role-protected user-list and global action form together with focused contract tests.
2. Roll back by removing the admin form and restoring the prior user-list route behavior; no data migration or API rollback exists.
