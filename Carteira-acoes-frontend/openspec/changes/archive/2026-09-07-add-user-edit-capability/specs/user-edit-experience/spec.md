## Purpose

Define uma experiência segura, acessível e compatível com o backend para editar usuários existentes sem expor ou reutilizar senhas.

## ADDED Requirements

### Requirement: Usuário existente pode ser editado
The application SHALL allow editing an existing user through `PUT /usuarios/{id}` and SHALL send only `nome`, `email` and the optional `senha` fields. It MUST NOT invent additional fields or use another HTTP method as fallback.

#### Scenario: Atualização de nome e e-mail
- **WHEN** a user changes name and email and leaves the password empty
- **THEN** the application sends a JSON PUT payload containing only `nome` and `email`.

#### Scenario: Atualização de senha
- **WHEN** a user supplies a new password
- **THEN** the application includes `senha` in the PUT payload.

### Requirement: Edição é acessível pela área de Usuários
The Users area SHALL provide a contextual `Editar usuário` action and SHALL open a responsive dialog with current `nome` and `email` filled, an empty optional password field, cancel and save actions, visible focus, keyboard navigation and accessible labels.

#### Scenario: Dialog recebe dados atuais
- **WHEN** the edit dialog opens for an existing user
- **THEN** name and email contain current response values and password is empty.

### Requirement: Senha opcional é normalizada com privacidade
The application SHALL omit `senha` when it is empty or contains only whitespace. It MUST never request, infer, display, log or persist the current password, and response models MUST remain without a password field.

#### Scenario: Senha em branco mantém a senha atual
- **WHEN** the password field is empty or whitespace
- **THEN** the request omits the `senha` property rather than sending empty, null or placeholder data.

### Requirement: Validação e estados do dialog
The edit form SHALL use Reactive Forms, require a name and valid email, prevent duplicate submits while loading, preserve unsent values on failure and provide contextual validation and API error feedback without stack traces.

#### Scenario: E-mail inválido
- **WHEN** the email is locally invalid
- **THEN** submission is prevented and the correction is presented near the field.

#### Scenario: Atualização em andamento
- **WHEN** the PUT request is pending
- **THEN** the dialog indicates loading and disables duplicate submission.

### Requirement: Sucesso atualiza a listagem
When the backend returns `200 OK` with a `UsuarioResponse`, the application SHALL close the dialog, show success feedback and update the displayed user without requiring manual refresh. The returned response MUST be treated as authoritative and MUST not be expected to contain a password.

#### Scenario: Usuário atualizado
- **WHEN** the backend returns `200 OK`
- **THEN** the list shows the returned name, email and updated timestamp and the dialog closes.

### Requirement: Erros de contrato são tratados
The application SHALL prioritize the backend `message` from the standard error response. It SHALL keep the dialog available for correction on duplicate email, inform the user when the user is not found, use the global treatment for 405 and show a safe fallback for unexpected failures.

#### Scenario: E-mail duplicado
- **WHEN** the backend returns `409 EMAIL_DUPLICADO`
- **THEN** the dialog remains open, preserves the entered email and presents the backend message or an equivalent duplicate-email message.

#### Scenario: Usuário não encontrado
- **WHEN** the backend returns `404 USUARIO_NAO_ENCONTRADO`
- **THEN** the application does not show success and informs that the user no longer exists.

#### Scenario: Erro inesperado
- **WHEN** the backend returns an unexpected failure without a usable message
- **THEN** the application displays `Não foi possível concluir a operação.` without technical details.
