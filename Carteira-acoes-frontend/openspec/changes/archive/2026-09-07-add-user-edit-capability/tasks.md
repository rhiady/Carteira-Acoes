## 1. Contract and service

- [x] 1.1 Add `UsuarioUpdateRequest` without adding password to `Usuario` response model.
- [x] 1.2 Implement `UsuarioService.update(id, request)` using `PUT /usuarios/{id}`.
- [x] 1.3 Normalize optional password omission at the request boundary.

## 2. Edit dialog

- [x] 2.1 Create the responsive user edit dialog using Reactive Forms and existing design tokens.
- [x] 2.2 Populate name/email from the selected user and keep password empty.
- [x] 2.3 Add validation, loading, focus management, keyboard access and accessible labels.
- [x] 2.4 Preserve form values and display backend/fallback errors without exposing technical details.

## 3. Users integration

- [x] 3.1 Add contextual `Editar usuário` action to the users area.
- [x] 3.2 Replace the list item with the authoritative response after successful update.
- [x] 3.3 Show success feedback and handle 404, 409, 405 and 500 responses.

## 4. Verification

- [x] 4.1 Test payloads with omitted and supplied passwords.
- [x] 4.2 Test validation, loading, success, duplicate email and not-found flows.
- [x] 4.3 Test password privacy in models, templates and logs.
- [x] 4.4 Run `npm test`, `npm run build` and `git diff --check`.
