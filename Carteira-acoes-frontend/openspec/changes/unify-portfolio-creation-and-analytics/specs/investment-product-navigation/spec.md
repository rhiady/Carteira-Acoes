## MODIFIED Requirements

### Requirement: Navegação distingue investimento e gestão

The system SHALL visually and semantically separate investment destinations from secondary management destinations while preserving keyboard access, active-route indication and responsive overlay behavior. In the administrator context, the navigation SHALL expose direct entries for `Ações`, `Carteiras` and `Operações` using published routes, in addition to existing administrative resources. Common users MUST NOT receive administrator-only shortcuts or management actions.

#### Scenario: Administrador abre a área administrativa
- **WHEN** uma pessoa com `ROLE_ADMIN` abre a entrada administrativa
- **THEN** encontra links claros para ações, carteiras e operações, com rota ativa identificável e sem duplicar conteúdo de outra tela

#### Scenario: Administrador abre operações
- **WHEN** o administrador seleciona `Operações`
- **THEN** é levado à rota de operações publicada e pode continuar para carteiras e detalhes sem URL ou endpoint inventado

#### Scenario: Usuário comum navega
- **WHEN** uma pessoa com `ROLE_USER` abre a navegação em qualquer viewport
- **THEN** vê apenas investimentos próprios e conta, sem atalhos administrativos para usuários, corretoras, manutenção de ações ou carteiras de terceiros

#### Scenario: Viewport estreito
- **WHEN** a navegação administrativa ou comum é aberta em viewport estreito
- **THEN** ela funciona como overlay acessível, pode ser fechada por teclado e não deixa o conteúdo principal encoberto após o fechamento

#### Scenario: Rota financeira ativa
- **WHEN** o usuário navega para uma carteira ou operação
- **THEN** o destino de investimento correspondente fica visivelmente ativo e o conteúdo atual permanece alcançável por teclado
