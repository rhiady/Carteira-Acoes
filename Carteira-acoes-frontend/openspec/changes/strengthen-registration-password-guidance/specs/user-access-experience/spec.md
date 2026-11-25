## MODIFIED Requirements

### Requirement: Account creation is simple and separate from login
The system SHALL expose `/cadastro` with only name, email, password and password confirmation. It SHALL validate required fields, valid email, a password with at least 8 characters, at least one uppercase letter and at least one special character, and matching passwords. The form SHALL visibly communicate these password criteria before submission, submit only `{ nome, email, senha }` to `POST /usuarios`, and never send or persist `confirmarSenha`.

#### Scenario: Password requirements are communicated
- **WHEN** the person opens or focuses the password field
- **THEN** the form clearly states that the password needs at least 8 characters, one uppercase letter and one special character

#### Scenario: Password does not meet the requirements
- **WHEN** the person submits a password missing one or more required criteria
- **THEN** submission is prevented and the unmet criteria are identified accessibly near the password field without sending a request

#### Scenario: Passwords do not match
- **WHEN** the person submits different password and confirmation values
- **THEN** submission is prevented and the mismatch is identified near the confirmation field

#### Scenario: Account is created
- **WHEN** `POST /usuarios` succeeds with a password that meets all criteria
- **THEN** the UI shows `Conta criada com sucesso. Agora você pode entrar.` and redirects to `/login` without automatic login

#### Scenario: Email is already registered
- **WHEN** the backend reports a duplicate email
- **THEN** the UI shows a comprehensible duplicate-account message and keeps non-sensitive fields available for correction
