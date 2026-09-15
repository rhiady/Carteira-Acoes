# user-access-experience Specification

## Purpose

Define the frontend experience connecting account creation, authenticated identity, user-owned investment data and clean session changes without a second authentication architecture.

## Requirements

### Requirement: Account creation is simple and separate from login
The system SHALL expose `/cadastro` with only name, email, password and password confirmation. It SHALL validate required fields, valid email and matching passwords, submit only `{ nome, email, senha }` to `POST /usuarios`, and never send or persist `confirmarSenha`.

#### Scenario: Passwords do not match
- **WHEN** the person submits different password and confirmation values
- **THEN** submission is prevented and the mismatch is identified near the confirmation field

#### Scenario: Account is created
- **WHEN** `POST /usuarios` succeeds
- **THEN** the UI shows `Conta criada com sucesso. Agora você pode entrar.` and redirects to `/login` without automatic login

#### Scenario: Email is already registered
- **WHEN** the backend reports a duplicate email
- **THEN** the UI shows a comprehensible duplicate-account message and keeps non-sensitive fields available for correction

### Requirement: Login links to account creation without gaining complexity
The existing `/login` screen SHALL retain only email, password and `Entrar` as its primary form, and SHALL provide the secondary action `Ainda não tem uma conta? Criar conta` linking to `/cadastro`.

#### Scenario: Person needs an account
- **WHEN** the person activates the secondary account-creation action
- **THEN** navigation goes to `/cadastro` without changing the login form contract

### Requirement: Authenticated identity scopes application data
The application SHALL use the authenticated user's `id`, `nome` and `email` as its sole frontend identity context. User-owned wallet listings SHALL query the user-scoped endpoint when available, and Dashboard, wallets, wallet details, purchase, sale, operations and analytics SHALL not present another user's data through global user selection or client-side filtering.

#### Scenario: Authenticated person opens wallets
- **WHEN** a person with authenticated user id `7` opens the wallet list
- **THEN** the frontend requests the user-scoped wallet resource for user `7` and renders only that response

#### Scenario: Financial flow opens
- **WHEN** the person starts a purchase, sale, operation history or analytics flow
- **THEN** available wallets and positions are limited to the authenticated person's context and no redundant user selector is presented

### Requirement: Ownership failures are handled as unavailable resources
The frontend SHALL respect backend responses for wallet ownership or missing resources, SHALL not bypass them through global listings, and SHALL offer a path back to the person's own wallets with a friendly message such as `Carteira não encontrada.`.

#### Scenario: Person opens another or invalid wallet
- **WHEN** the backend rejects or cannot find the requested wallet
- **THEN** the UI presents a friendly unavailable state and a link/action to return to the person's wallets

### Requirement: Logout clears the complete user context
Logout SHALL clear the authenticated user, selected wallet, filters, in-memory financial data and related local persistence before redirecting to `/login`. A later login by a different person SHALL not show data from the previous person.

#### Scenario: Two people use the same browser
- **WHEN** Ana logs out and João logs in afterward
- **THEN** João sees only his own wallets, positions, operations and analytics, with no previous selection or filter retained
