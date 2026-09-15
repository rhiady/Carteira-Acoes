## Why

The API currently supports creating, listing, and retrieving users, but not editing an existing user's data. This prevents correction of names or email addresses after registration and leaves the user resource incomplete for normal account management.

## What Changes

- Add an authenticated-by-resource update operation for an existing user.
- Allow updating the user's name, email, and optionally password through a dedicated request contract.
- Validate required fields, email uniqueness, and user existence.
- Continue omitting passwords from every response.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `portfolio-catalog`: extend user management with an update operation and its validation/error behavior.

## Impact

- `UsuarioResource`, `UsuarioService`, DTOs, mapper, and user repository.
- New endpoint `PUT /usuarios/{id}` and integration/unit tests.
- No database schema migration is expected.
