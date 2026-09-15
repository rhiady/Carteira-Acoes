## MODIFIED Requirements

### Requirement: Contratos HTTP seguros e documentados
Todos os endpoints existentes SHALL ser inventariados e verificados de ponta a ponta: método, path, variáveis, query params, request DTO, validação, service, regra de domínio, repository, entity, response DTO, status HTTP, exception handling, autenticação, ownership e OpenAPI. A documentação MUST refletir o código real e incluir requests, responses, enums, nullability, paginação, sucesso e erros aplicáveis. Nenhuma response SHALL conter senha, hash, segredo, stack trace, SQL ou detalhe interno desnecessário; `PUT /usuarios/{id}` SHALL preservar a senha armazenada quando nenhuma nova senha for fornecida.

#### Scenario: Matriz completa de endpoints
- **WHEN** a auditoria é concluída
- **THEN** cada rota real possui rastreabilidade documentada do controller até a resposta HTTP e seus erros possíveis

#### Scenario: Atualização sem nova senha
- **WHEN** o cliente atualiza um usuário sem informar senha
- **THEN** a senha anterior permanece válida e não aparece na response nem na documentação do schema de response

#### Scenario: OpenAPI corresponde ao runtime
- **WHEN** um contrato publicado é comparado com requests e responses reais
- **THEN** método, path, campos, tipos, nullability, status e erros documentados correspondem ao comportamento observado

### Requirement: Validação estrutural de requests
Requests SHALL rejeitar nulos, strings vazias, IDs obrigatórios ausentes, formatos inválidos de email, CNPJ, ticker e mercado, além de quantidade, preço e custos fora das regras contratuais, antes de executar mutações de domínio. Exceções conhecidas MUST ser traduzidas para o status e o corpo de erro padronizados, sem resultar em HTTP 500.

#### Scenario: Request estrutural inválido
- **WHEN** uma request contém quantidade ou preço zero/negativo, formato inválido ou identificador obrigatório ausente
- **THEN** a API responde HTTP 400 com erro padronizado e não altera o estado

#### Scenario: Erro de domínio conhecido
- **WHEN** uma regra conhecida rejeita usuário, corretora, carteira, ação, posição, operação ou credencial
- **THEN** a API responde com 401, 404, 409 ou outro status explicitamente definido, nunca 500 por falta de mapeamento

### Requirement: Paginação e ordenação previsíveis
Toda response paginada SHALL usar uma estrutura consistente com `content`, número e tamanho da página, `totalElements`, `totalPages`, `first`, `last` e `empty`. O backend MUST aplicar ordenação antes da paginação, aceitar somente campos públicos documentados, validar `page`, `size` e `sort`, e retornar HTTP 400 para parâmetros inválidos em vez de 500.

#### Scenario: Paginação válida
- **WHEN** o cliente informa `page`, `size` e `sort` permitidos
- **THEN** a response contém metadados coerentes e o conteúdo corresponde à ordenação aplicada antes do recorte da página

#### Scenario: Ordenação inválida
- **WHEN** o cliente informa um campo ou direção de ordenação não suportados
- **THEN** a API responde HTTP 400 com erro padronizado sem expor nomes internos de persistência

## ADDED Requirements

### Requirement: Cobertura integral das APIs existentes
A auditoria MUST incluir todas as rotas reais de usuários, autenticação, corretoras, carteiras, posições, ações, cotações, histórico e operações, além de qualquer rota adicional descoberta no código. Nenhum endpoint poderá ser considerado aprovado apenas por analogia com outro endpoint.

#### Scenario: Rota adicional descoberta
- **WHEN** a inspeção de controllers encontra uma rota não listada inicialmente
- **THEN** essa rota é adicionada à matriz e recebe a mesma revisão completa de contrato, segurança, erros e OpenAPI

### Requirement: Isolamento dos contratos financeiros
Endpoints de carteira, posição, resumo, operação, compra, venda e dados financeiros derivados MUST identificar o usuário autenticado no backend e impedir acesso ou mutação de recursos de terceiros por manipulação de IDs.

#### Scenario: IDOR financeiro
- **WHEN** um usuário autenticado troca manualmente `usuarioId`, `carteiraId`, `operacaoId` ou outro identificador financeiro
- **THEN** nenhum dado de terceiro é retornado ou alterado e a resposta segue o contrato seguro de não encontrado ou acesso negado
