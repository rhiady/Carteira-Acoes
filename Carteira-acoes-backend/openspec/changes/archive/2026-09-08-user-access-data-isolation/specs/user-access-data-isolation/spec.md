## Purpose

This capability ensures that authenticated users can use the existing account and financial features while every portfolio-related read or mutation remains isolated to the owning user.

## ADDED Requirements

### Requirement: Secure account authentication and registration
The system MUST preserve the existing email/password login and user-registration contracts. Valid login MUST return only safe user identity data; invalid or incomplete credentials MUST not reveal whether an email exists. Passwords MUST be stored encoded and MUST NOT appear in any response or log. Duplicate email registration MUST be rejected using the established conflict/error convention.

#### Scenario: Valid login
- **WHEN** a registered user submits the correct email and password to the existing login endpoint
- **THEN** the system authenticates the user and returns only the safe login response fields

#### Scenario: Invalid credentials
- **WHEN** a user submits an unknown email or an incorrect password
- **THEN** the system returns the same unauthorized outcome without identifying which credential failed

#### Scenario: Duplicate registration
- **WHEN** a new registration uses an email already registered
- **THEN** the system rejects it with the established conflict response and does not create a second user

### Requirement: Authenticated ownership enforcement
The system MUST determine the authenticated user from the backend authentication context and MUST NOT trust a client-supplied `usuarioId` as proof of identity. Portfolio reads and creation MUST be limited to that user, and accessing another user's portfolio MUST not disclose its existence.

#### Scenario: List own portfolios
- **WHEN** an authenticated user requests portfolios in the user-scoped endpoint
- **THEN** the response contains only portfolios owned by the authenticated user

#### Scenario: Manipulate user identifier
- **WHEN** an authenticated user changes a user-scoped URL identifier to another user's ID
- **THEN** the request is denied or returns an empty/not-found outcome consistent with the existing error policy, and no third-party portfolio data is returned

#### Scenario: Create portfolio with another user ID
- **WHEN** an authenticated user submits a portfolio request containing a different `usuarioId`
- **THEN** the system associates the portfolio with the authenticated user or rejects the request, but never creates it for the other user

### Requirement: Financial resource isolation
The system MUST validate portfolio ownership before returning or mutating positions, operations, purchases, sales, summaries, or other data derived from a portfolio. A resource belonging to another user MUST NOT be returned or changed.

#### Scenario: Access another user's portfolio data
- **WHEN** a user requests another user's portfolio, positions, or operations by changing a portfolio or resource ID
- **THEN** the system denies the request without returning financial data

#### Scenario: Purchase in another user's portfolio
- **WHEN** a user attempts to purchase using a portfolio they do not own
- **THEN** the request fails before persistence and neither the operation nor any position is changed

#### Scenario: Sell in another user's portfolio
- **WHEN** a user attempts to sell using a portfolio they do not own
- **THEN** the request fails before persistence and neither the operation nor any position is changed

### Requirement: Account update preserves credential safety
The existing user update behavior MUST remain available. An omitted password MUST preserve the current password, while a supplied new password MUST be encoded before persistence; update responses MUST exclude password data.

#### Scenario: Update profile without password
- **WHEN** an existing user updates profile fields without supplying a password
- **THEN** the profile changes are saved and the prior password remains usable

#### Scenario: Update password
- **WHEN** an existing user supplies a new password during the existing update operation
- **THEN** the new password is stored encoded and is usable for subsequent login
