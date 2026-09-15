## Purpose

Definir contratos HTTP verificáveis para que os recursos existentes do backend exponham dados seguros, erros coerentes e documentação alinhada ao comportamento implementado.

## ADDED Requirements

### Requirement: Contratos HTTP seguros e documentados
Os endpoints existentes SHALL documentar no OpenAPI seus requests, responses, status de sucesso e erros 400, 404, 405 e 409 quando aplicáveis. Nenhuma response SHALL conter senha, segredo ou stack trace, e `PUT /usuarios/{id}` SHALL preservar a senha armazenada quando nenhuma nova senha for fornecida.

#### Scenario: Atualização sem nova senha
- **WHEN** o cliente atualiza um usuário sem informar senha
- **THEN** a senha anterior permanece válida e não aparece na response nem na documentação do schema de response

### Requirement: Validação estrutural de requests
Requests SHALL rejeitar nulos, strings vazias, IDs obrigatórios ausentes, email, CNPJ, ticker, mercado, quantidade e preço fora das regras contratuais antes de executar regras de domínio.

#### Scenario: Request estrutural inválido
- **WHEN** uma request contém quantidade ou preço zero/negativo, ou identificador obrigatório ausente
- **THEN** a API responde HTTP 400 com erro padronizado e não altera o estado

### Requirement: Paginação e ordenação previsíveis
Responses paginadas SHALL expor `content`, `page`, `size`, `totalElements`, `totalPages`, `first`, `last` e `empty`; campos de ordenação inválidos SHALL retornar HTTP 400, nunca 500.

#### Scenario: Paginação válida
- **WHEN** o cliente informa `page`, `size` e `sort` permitidos
- **THEN** a response contém metadados coerentes com a página e a ordenação solicitadas

