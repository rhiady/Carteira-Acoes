## ADDED Requirements

### Requirement: Registration and wallet creation respect implicit ownership
The registration form SHALL contain name, email, password and confirmation only. Wallet creation SHALL not allow choosing an arbitrary owner when the authenticated identity is known; it SHALL preserve required broker selection and use the current user's identity according to the published compatible backend contract.

#### Scenario: Authenticated person creates a wallet
- **WHEN** the person opens wallet creation while authenticated
- **THEN** the form does not offer a user selector and submits the current user's identity through the existing wallet contract

#### Scenario: Registration validation fails
- **WHEN** a required registration field is empty, email is invalid or passwords differ
- **THEN** submission is prevented and each correction is identified accessibly without logging sensitive values
