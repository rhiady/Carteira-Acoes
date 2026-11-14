## MODIFIED Requirements

### Requirement: Consulta da identidade autenticada
O sistema MUST aceitar `login` e `password` no endpoint de autenticação e expor `GET /api/auth/me` para retornar o login e as authorities derivados do token válido. A resposta MUST usar um contrato seguro e MUST NOT incluir a entidade `Usuario`, senha, password ou hash persistido.

#### Scenario: Consulta com token válido
- **WHEN** o cliente chama `/api/auth/me` com token válido
- **THEN** o sistema responde `200 OK` com o login e suas authorities, sem dados de credencial

#### Scenario: Consulta sem token
- **WHEN** o cliente chama `/api/auth/me` sem token válido
- **THEN** o sistema responde `401 Unauthorized` sem serializar dados do usuário
