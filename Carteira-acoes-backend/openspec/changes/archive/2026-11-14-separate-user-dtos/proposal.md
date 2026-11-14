## Why

The public user API must not expose the `Usuario` persistence entity, which contains the password hash and mixes storage concerns with API contracts. Separate request and response DTOs make credential handling explicit and prevent accidental hash exposure.

## What Changes

- Add a dedicated `UsuarioCreateRequest` for public login and password registration, defaulting every new account to `USER`.
- Add a dedicated `UsuarioUpdateRequest` for login and ativo, without password or papel.
- Add a dedicated `AlterarSenhaRequest` for password changes only.
- Add a dedicated role-change request for the administrator-only promotion operation.
- Restrict `UsuarioResponse` to `id`, `login`, `papel` and `ativo`, never including senha/password.
- Change authentication input from `username` to `login`.
- Update user resources, services, mappers and tests to use the separated contracts.
- Preserve password hashing and role validation; only administrators may assign or change `papel`.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `portfolio-catalog`: user endpoints use explicit create, update and password-change DTO contracts without exposing persistence fields.
- `user-access-data-isolation`: account updates preserve credential safety while separating profile changes from password changes.
- `jwt-authentication`: authenticated identity and authorities remain based on persisted users without exposing password data through responses.

## Impact

Affected code includes user DTOs, `UsuarioResource`, `UsuarioService`, `UsuarioMapper`, authentication responses and related integration/unit tests. Client payloads for user creation and updates use `login`, `password` and `ativo`; password and role changes are separate operations. The persistence field remains internal, so no database login-column rename is required.
