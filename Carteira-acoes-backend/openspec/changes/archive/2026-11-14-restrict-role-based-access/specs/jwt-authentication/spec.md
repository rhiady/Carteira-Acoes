## ADDED Requirements

### Requirement: Authorities do JWT nas regras de acesso
As authorities emitidas no JWT MUST ser usadas para distinguir `ROLE_ADMIN` de `ROLE_USER`. Falhas por ausência de autenticação MUST retornar `401`, enquanto autenticação válida sem a authority necessária MUST retornar `403`.

#### Scenario: Token sem authority administrativa
- **WHEN** um usuário autenticado com `ROLE_USER` acessa uma operação administrativa
- **THEN** o sistema responde `403 Forbidden` sem executar a mutação

#### Scenario: Token administrativo
- **WHEN** um usuário autenticado com `ROLE_ADMIN` acessa uma operação administrativa válida
- **THEN** o sistema permite a operação

#### Scenario: Token ausente
- **WHEN** uma requisição sem token acessa uma rota protegida
- **THEN** o sistema responde `401 Unauthorized`

