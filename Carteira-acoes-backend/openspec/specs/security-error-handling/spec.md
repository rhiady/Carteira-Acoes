# security-error-handling Specification

## Purpose

Definir respostas HTTP consistentes para falhas de autenticação e autorização, preservando handlers separados para cada responsabilidade de segurança.

## Requirements

### Requirement: Falha de autenticação em formato padronizado
Quando uma requisição protegida não apresentar credenciais válidas, a API MUST responder `401 Unauthorized` em JSON usando o contrato público `ErroResponse`, com timestamp, status, código `UNAUTHORIZED`, mensagem segura e path requisitado.

#### Scenario: Token ausente
- **WHEN** uma requisição sem token acessa um endpoint protegido
- **THEN** a API responde `401` com `Content-Type: application/json`, código `UNAUTHORIZED` e o path da requisição

#### Scenario: Token inválido ou expirado
- **WHEN** uma requisição usa token inválido, adulterado ou expirado
- **THEN** a API responde `401` com o mesmo contrato JSON sem expor detalhes do token ou da exceção

### Requirement: Falha de autorização em formato padronizado
Quando uma identidade autenticada não possuir a permissão necessária, a API MUST responder `403 Forbidden` em JSON usando o contrato público `ErroResponse`, com timestamp, status, código `FORBIDDEN`, mensagem segura e path requisitado.

#### Scenario: Usuário sem permissão administrativa
- **WHEN** um usuário autenticado sem `ROLE_ADMIN` acessa uma operação administrativa
- **THEN** a API responde `403` com `Content-Type: application/json`, código `FORBIDDEN` e não executa a operação

### Requirement: Separação dos fluxos de segurança e aplicação
Falhas geradas pelo filtro de autenticação ou pela autorização MUST ser tratadas pelo fluxo específico de segurança, enquanto exceções lançadas durante o processamento MVC MUST continuar sendo tratadas pelo contrato global da aplicação. Nenhuma resposta MUST expor stack trace, segredo ou detalhe interno.

#### Scenario: Exceção durante o processamento de uma rota
- **WHEN** uma exceção conhecida ocorre depois que a requisição passa pela segurança
- **THEN** a API usa o mapeamento de exceções da aplicação e preserva o status e código definidos para o domínio
