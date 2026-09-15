# user-authentication Specification

## Purpose

Define uma autenticação backend simples por email e senha, com respostas previsíveis, validação de entrada e proteção explícita contra exposição da senha do usuário.

## Requirements

### Requirement: Login por email e senha
O sistema MUST expor `POST /auth/login` aceitando JSON com `email` e `senha`, localizar o usuário pelo email e validar a senha informada.

#### Scenario: Login válido
- **WHEN** a requisição contém email cadastrado e senha correspondente
- **THEN** o sistema MUST responder `200 OK` com `id`, `nome` e `email` do usuário

### Requirement: Validação do request de login
O sistema MUST rejeitar requests sem email, sem senha, com senha vazia ou com email em formato inválido.

#### Scenario: Email inválido
- **WHEN** a requisição contém email em formato inválido
- **THEN** o sistema MUST responder `400 Bad Request`

#### Scenario: Senha vazia ou ausente
- **WHEN** a requisição não contém senha ou contém senha vazia
- **THEN** o sistema MUST responder `400 Bad Request`

### Requirement: Tratamento uniforme de credenciais inválidas
O sistema MUST responder `401 Unauthorized` com mensagem externa equivalente a `Email ou senha inválidos.` tanto para email inexistente quanto para senha incorreta, sem revelar qual caso ocorreu.

#### Scenario: Email inexistente
- **WHEN** a requisição contém email não cadastrado
- **THEN** o sistema MUST responder `401 Unauthorized` com erro de credenciais inválidas

#### Scenario: Senha incorreta
- **WHEN** a requisição contém email cadastrado e senha incorreta
- **THEN** o sistema MUST responder `401 Unauthorized` com a mesma mensagem externa usada para email inexistente

### Requirement: Resposta segura do usuário
O sistema MUST NOT incluir a senha, hash da senha ou qualquer segredo de autenticação na resposta de login.

#### Scenario: Response não expõe senha
- **WHEN** o login é concluído com sucesso
- **THEN** o corpo da resposta MUST conter somente dados básicos públicos do usuário, incluindo `id`, `nome` e `email`, sem campo ou valor de senha

### Requirement: Compatibilidade e escopo da autenticação
O sistema MUST manter os endpoints existentes funcionando e MUST NOT exigir autenticação global nem introduzir JWT, refresh token, OAuth, roles, permissões, MFA, recuperação de senha ou confirmação de email como parte deste login.

#### Scenario: Endpoints existentes
- **WHEN** um endpoint existente é chamado nas condições previamente suportadas
- **THEN** seu comportamento MUST permanecer compatível com o comportamento anterior
