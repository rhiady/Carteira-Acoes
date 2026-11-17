## MODIFIED Requirements

### Requirement: Edição é acessível pela área de Usuários
The Users area SHALL provide a contextual `Editar usuário` action only inside the administrator-protected Users area and SHALL open a responsive dialog with current `nome` and `email` filled, an empty optional password field, cancel and save actions, visible focus, keyboard navigation and accessible labels. This administrative edit flow SHALL not be reused as the common user's self-service password flow; that flow belongs to `Minha conta` and requires its own published backend contract.

#### Scenario: Dialog recebe dados atuais
- **WHEN** an administrator opens the edit dialog for an existing user
- **THEN** name and email contain current response values and password is empty

#### Scenario: Common user cannot open administrative edit
- **WHEN** a common user navigates to the Users area or an edit-user URL
- **THEN** the role guard blocks the route before the dialog or user data is rendered
