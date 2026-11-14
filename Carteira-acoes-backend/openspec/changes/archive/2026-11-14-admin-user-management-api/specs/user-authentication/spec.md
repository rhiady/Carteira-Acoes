## MODIFIED Requirements

### Requirement: Compatibilidade e escopo da autenticação
O sistema MUST manter os endpoints de negócio funcionando com o cabeçalho `Authorization: Bearer <token>`, MUST NOT depender de sessão HTTP para autenticar requisições e MUST tratar usuários com `ativo=false` como contas desabilitadas.

#### Scenario: Endpoint protegido com token válido
- **WHEN** um endpoint protegido recebe um token JWT válido
- **THEN** o sistema MUST autenticar a requisição usando a identidade e as authorities do token

#### Scenario: Endpoint protegido sem token
- **WHEN** um endpoint protegido é chamado sem token ou com token inválido
- **THEN** o sistema MUST responder `401 Unauthorized` sem executar a operação

#### Scenario: Endpoints existentes
- **WHEN** um endpoint existente é chamado com um token válido e nas condições funcionais previamente suportadas
- **THEN** seu comportamento funcional MUST permanecer compatível com o comportamento anterior

#### Scenario: Login de usuário desativado
- **WHEN** uma identidade com `ativo=false` tenta autenticar
- **THEN** o sistema MUST rejeitar o login e MUST emitir nenhum token
