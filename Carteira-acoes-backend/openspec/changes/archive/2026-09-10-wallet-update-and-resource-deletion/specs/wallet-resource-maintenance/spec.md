## Purpose

Permitir manutenção limitada de carteiras e exclusão segura de carteiras e corretoras, protegendo ownership, histórico financeiro e integridade referencial.

## ADDED Requirements

### Requirement: Atualização limitada da carteira
O sistema MUST aceitar `PUT /carteiras/{id}` com request contendo somente `nome`, aplicar as mesmas regras de obrigatoriedade, conteúdo não vazio e tamanho máximo da criação, normalizar o nome consistentemente e preservar todos os demais atributos e relacionamentos.

#### Scenario: Atualização válida
- **WHEN** o proprietário envia nome válido para uma carteira existente
- **THEN** a API responde 200 e somente o nome persistido é alterado

#### Scenario: Campos protegidos
- **WHEN** o request inclui usuário, corretora, posições, operações, valores ou datas
- **THEN** esses campos não podem ser alterados pelo endpoint e a carteira mantém seus valores originais

#### Scenario: Nome inválido
- **WHEN** o request contém nome ausente, vazio ou somente espaços, ou excede o limite do domínio
- **THEN** a API responde 400 com erro padronizado e não altera a carteira

#### Scenario: Carteira inexistente ou de outro usuário
- **WHEN** o ID não existe ou pertence a outro usuário autenticado
- **THEN** a API responde 404 e não revela nem altera dados de terceiros

### Requirement: Exclusão segura de carteira
O sistema MUST aceitar `DELETE /carteiras/{id}` e somente excluir uma carteira existente e pertencente ao usuário quando não houver operações, posições ou qualquer outro vínculo impeditivo real. A operação deve ser atômica e responder 204 sem corpo quando concluída.

#### Scenario: Carteira sem vínculos
- **WHEN** o proprietário exclui carteira sem posições nem operações
- **THEN** a API responde 204 e a carteira deixa de existir

#### Scenario: Carteira vinculada
- **WHEN** a carteira possui posição ou operação vinculada
- **THEN** a API responde 409 com mensagem clara de conflito, mantém a carteira e preserva os vínculos

#### Scenario: Carteira inexistente ou de outro usuário
- **WHEN** o ID não existe ou pertence a outro usuário autenticado
- **THEN** a API responde 404 sem realizar exclusão

### Requirement: Exclusão segura de corretora
O sistema MUST aceitar `DELETE /corretoras/{id}` e excluir somente corretora existente sem carteiras ou outros vínculos impeditivos. Não pode apagar dados dependentes em cascade para viabilizar a operação.

#### Scenario: Corretora sem vínculos
- **WHEN** o cliente exclui corretora não referenciada
- **THEN** a API responde 204 e a corretora deixa de existir

#### Scenario: Corretora vinculada
- **WHEN** existem carteiras ou registros dependentes da corretora
- **THEN** a API responde 409 com mensagem de domínio compreensível, mantendo a corretora e os registros associados

#### Scenario: Corretora inexistente
- **WHEN** o ID não existe
- **THEN** a API responde 404 com erro padronizado

### Requirement: Exclusão segura de ação
O sistema MUST aceitar `DELETE /acoes/{id}` e somente excluir uma ação existente quando não houver operações, posições, histórico de cotações ou outros registros dependentes que precisem ser preservados. A operação deve responder 204 sem corpo quando concluída e não pode introduzir cascade destrutivo.

#### Scenario: Ação sem vínculos
- **WHEN** o cliente exclui uma ação sem operações, posições ou histórico de cotações vinculados
- **THEN** a API responde 204 e a ação deixa de existir

#### Scenario: Ação inexistente
- **WHEN** o ID da ação não existe
- **THEN** a API responde 404 com erro padronizado

#### Scenario: Ação com posição
- **WHEN** a ação possui posição em carteira
- **THEN** a API responde 409, mantém a ação e preserva a posição

#### Scenario: Ação com operações
- **WHEN** a ação possui operação de compra ou venda
- **THEN** a API responde 409, mantém a ação e preserva as operações

#### Scenario: Ação com histórico de cotação
- **WHEN** a ação possui histórico de cotação ou cotação persistida que o domínio precisa preservar
- **THEN** a API responde 409 e mantém o histórico e a ação

### Requirement: Erros de integridade e documentação
Conflitos de relacionamento MUST ser traduzidos para erro padronizado HTTP 409, sem expor exceções SQL, nomes de constraints ou mensagens brutas de banco. Os quatro endpoints MUST estar documentados no OpenAPI com respostas aplicáveis 200, 204, 400, 404 e 409.

#### Scenario: Falha referencial durante exclusão
- **WHEN** uma exclusão encontra violação de integridade referencial
- **THEN** a API responde erro de negócio 409 e não expõe `ConstraintViolationException`, `PSQLException` ou texto de foreign key
