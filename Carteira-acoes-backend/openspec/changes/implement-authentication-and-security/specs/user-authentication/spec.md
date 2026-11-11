## MODIFIED Requirements

### Requirement: Login por email e senha
O sistema MUST expor `POST /auth/login` aceitando JSON com `email` e `senha`, localizar o usuário pelo email, validar a senha informada e estabelecer uma sessão autenticada associada ao usuário e ao seu papel.

#### Scenario: Login válido
- **WHEN** a requisição contém email cadastrado e senha correspondente
- **THEN** o sistema MUST responder `200 OK`, criar uma sessão autenticada e retornar `id`, `nome` e `email` do usuário

### Requirement: Compatibilidade e escopo da autenticação
O sistema MUST permitir sem autenticação somente o cadastro de usuário, o login e as rotas técnicas explicitamente públicas. As demais rotas MUST exigir uma sessão autenticada válida e MUST usar o usuário e o papel armazenados no servidor, sem confiar em `usuarioId` ou `papel` enviados pelo cliente. O sistema MUST NOT introduzir JWT, refresh token, OAuth, MFA, recuperação de senha ou confirmação de email como parte desta mudança.

#### Scenario: Requisição sem sessão
- **WHEN** uma requisição acessa uma rota protegida sem sessão autenticada válida
- **THEN** o sistema MUST responder `401 Unauthorized` com erro padronizado e MUST NOT executar a operação

#### Scenario: Cadastro público
- **WHEN** uma pessoa sem sessão envia um cadastro válido
- **THEN** o sistema MUST criar o usuário com papel `USER` e MUST ignorar qualquer tentativa de informar ou definir papel administrativo

#### Scenario: Atualização não altera papel
- **WHEN** um usuário atualiza seus dados, incluindo um campo de papel ou valor administrativo
- **THEN** o sistema MUST preservar o papel armazenado e MUST NOT permitir autoelevação

#### Scenario: Endpoints existentes
- **WHEN** um endpoint existente é chamado nas condições previamente suportadas e com a autenticação exigida por sua nova proteção
- **THEN** seu comportamento funcional MUST permanecer compatível com o comportamento anterior

## ADDED Requirements

### Requirement: Autorização baseada em papel
O sistema MUST aplicar autorização baseada no papel persistido do usuário. Operações administrativas MUST ser permitidas somente a usuários com papel `ADMIN`, enquanto operações de carteira e dados financeiros MUST continuar limitadas ao proprietário autenticado.

#### Scenario: Usuário comum acessa operação administrativa
- **WHEN** um usuário com papel `USER` acessa uma rota administrativa
- **THEN** o sistema MUST responder `403 Forbidden` e MUST NOT alterar dados

#### Scenario: Administrador acessa operação administrativa
- **WHEN** um usuário com papel `ADMIN` acessa uma rota administrativa autenticada
- **THEN** o sistema MUST permitir a operação conforme as validações funcionais existentes

#### Scenario: Usuário acessa dados de outro usuário
- **WHEN** um usuário autenticado tenta acessar ou alterar carteira ou dado financeiro de outro usuário
- **THEN** o sistema MUST negar a operação sem retornar nem alterar dados de terceiro

### Requirement: Persistência segura do papel
Cada usuário MUST possuir exatamente um papel persistido entre `ADMIN` e `USER`. A migração do esquema MUST atribuir `USER` aos registros existentes sem papel, e o cadastro MUST usar `USER` como valor padrão.

#### Scenario: Registro existente migrado
- **WHEN** a migração de segurança é executada sobre uma base com usuários existentes
- **THEN** todos os usuários recebem um papel válido sem perda de dados de identidade ou credenciais

#### Scenario: Papel inválido
- **WHEN** uma operação tenta persistir ou interpretar um papel fora de `ADMIN` e `USER`
- **THEN** o sistema MUST rejeitar a operação e MUST manter a integridade do usuário
