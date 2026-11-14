# jwt-authentication Specification

## Purpose

Define autenticação stateless baseada em JWT assinado, com validade temporal, emissor confiável e authorities usadas para proteger os recursos da API.

## Requirements

### Requirement: Configuração segura do JWT
O sistema MUST carregar segredo, expiração e emissor por propriedades tipadas. O segredo MUST possuir pelo menos 32 bytes, a expiração MUST ser maior que zero e o emissor MUST ser informado; configuração inválida MUST impedir a inicialização.

#### Scenario: Segredo ausente ou curto
- **WHEN** a aplicação inicia com segredo ausente ou menor que 32 bytes
- **THEN** a inicialização MUST falhar com erro de configuração

#### Scenario: Expiração inválida
- **WHEN** a aplicação inicia com expiração ausente, zero ou negativa
- **THEN** a inicialização MUST falhar com erro de configuração

### Requirement: Emissão de token
O sistema MUST emitir o JWT somente após validar as credenciais. O token MUST conter emissor, instante de emissão, expiração, subject e authorities do usuário.

#### Scenario: Token contém claims obrigatórias
- **WHEN** o login é realizado com credenciais válidas
- **THEN** o token MUST conter `iss`, `iat`, `exp`, `sub` e as authorities efetivas do usuário

### Requirement: Validação de token
O sistema MUST validar assinatura, emissor e claims temporais dos JWTs recebidos. Tokens expirados, adulterados ou emitidos por outro emissor MUST ser rejeitados.

#### Scenario: Token expirado
- **WHEN** uma requisição usa token cuja expiração já passou
- **THEN** o sistema MUST responder `401 Unauthorized`

#### Scenario: Emissor divergente
- **WHEN** uma requisição usa token assinado corretamente, mas com issuer diferente do configurado
- **THEN** o sistema MUST responder `401 Unauthorized`

### Requirement: Authorities e autorização
O sistema MUST converter as authorities presentes no token para a autenticação da requisição. Rotas administrativas MUST exigir `ROLE_ADMIN`, enquanto as demais rotas protegidas MUST exigir autenticação válida.

#### Scenario: Usuário comum acessa rota administrativa
- **WHEN** um usuário autenticado sem `ROLE_ADMIN` acessa uma rota administrativa
- **THEN** o sistema MUST responder `403 Forbidden` sem alterar dados

#### Scenario: Administrador acessa rota administrativa
- **WHEN** um usuário autenticado com `ROLE_ADMIN` acessa uma rota administrativa
- **THEN** o sistema MUST permitir a operação conforme suas validações funcionais

### Requirement: Consulta da identidade autenticada
O sistema MUST expor `GET /api/auth/me` para retornar o subject e as authorities derivados do token válido.

#### Scenario: Consulta com token válido
- **WHEN** o cliente chama `/api/auth/me` com token válido
- **THEN** o sistema MUST responder `200 OK` com a identidade e suas permissões
