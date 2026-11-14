## MODIFIED Requirements

### Requirement: Authorities e autorização
O sistema MUST converter as authorities presentes no token para a autenticação da requisição. Rotas administrativas MUST exigir `ROLE_ADMIN`, enquanto as demais rotas protegidas MUST exigir autenticação válida. Falhas de autenticação MUST responder `401 Unauthorized` com o contrato JSON padronizado da API, e autenticação válida sem a authority necessária MUST responder `403 Forbidden` com o mesmo contrato, sem alterar dados.

#### Scenario: Usuário comum acessa rota administrativa
- **WHEN** um usuário autenticado sem `ROLE_ADMIN` acessa uma rota administrativa
- **THEN** o sistema MUST responder `403 Forbidden` com erro JSON padronizado, sem alterar dados

#### Scenario: Administrador acessa rota administrativa
- **WHEN** um usuário autenticado com `ROLE_ADMIN` acessa uma rota administrativa
- **THEN** o sistema MUST permitir a operação conforme suas validações funcionais

#### Scenario: Requisição sem autenticação
- **WHEN** uma requisição sem token acessa uma rota protegida
- **THEN** o sistema MUST responder `401 Unauthorized` com erro JSON padronizado e sem detalhes internos
