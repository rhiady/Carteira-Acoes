## 1. Diagnose the existing administrative flow

- [x] 1.1 Trace the administrator Users route, page/component, `UsuarioService`, page-envelope model, and all callers to identify why the published listing is not rendered.
- [x] 1.2 Confirm the request, pagination defaults, role guard, and error normalization against the existing frontend contract before changing code.

## 2. Restore user management listing

- [x] 2.1 Apply the smallest correction at the responsible existing route, page, or service boundary so an authorized administrator sees the paginated user response.
- [x] 2.2 Preserve existing loading, empty, error, retry, pagination, edit action, and keyboard-accessible table behavior without adding client-side user data or endpoints.

## 3. Regression coverage and validation

- [x] 3.1 Add or update focused tests for administrator successful, empty, error, and non-administrator access states, at the boundary implicated by the defect.
- [x] 3.2 Run the focused tests, TypeScript/build checks, and `openspec validate --type change fix-admin-user-listing`.
