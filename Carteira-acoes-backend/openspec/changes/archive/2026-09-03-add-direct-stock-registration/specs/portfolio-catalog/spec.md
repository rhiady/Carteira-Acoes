## MODIFIED Requirements

### Requirement: Catálogo de ações por mercado
O sistema SHALL consultar ações por identificador ou ticker e SHALL listar ações paginadamente. O sistema SHALL expor `POST /acoes` para criar uma ação diretamente a partir de `ticker` e `mercado`, sem exigir carteira, posição ou operação, respondendo HTTP 201 com a ação criada e o cabeçalho `Location`. Na criação direta ou pela compra, o sistema SHALL importar nome da empresa e cotação atual do provedor compatível e persistir ticker único em maiúsculas, mercado `BRASIL` ou `EUA` e a moeda derivada BRL ou USD. O cliente MUST NOT informar nome da empresa, moeda, preço ou data/hora da cotação. O sistema MUST rejeitar ticker duplicado, ticker não localizado ou resposta sem nome ou cotação válida sem persistir a ação; uma criação direta bem-sucedida MUST NOT criar posição nem operação.

#### Scenario: Criação de ação por compra
- **WHEN** uma compra válida informa ticker e mercado ainda não cadastrados
- **THEN** o sistema cria a ação com seus dados de mercado e a associa somente à posição criada ou atualizada para a carteira da compra

#### Scenario: Cadastro direto indisponível
- **WHEN** um cliente envia `ticker` e `mercado` válidos para `POST /acoes` e o provedor retorna dados válidos
- **THEN** o sistema cria e retorna a ação importada sem criar carteira, posição ou operação

#### Scenario: Ação brasileira válida
- **WHEN** uma compra válida informa ticker do mercado BRASIL ainda não cadastrado
- **THEN** o sistema cria a ação com moeda BRL, ticker em maiúsculas, nome da empresa e cotação importados

#### Scenario: Cadastro direto com ticker duplicado
- **WHEN** um cliente envia para `POST /acoes` um ticker já cadastrado
- **THEN** o sistema retorna erro de conflito e não altera a ação existente

#### Scenario: Combinação de mercado e moeda inválida
- **WHEN** uma compra ou cadastro direto informa ticker inexistente ou o provedor não retorna dados válidos para o mercado
- **THEN** o sistema rejeita a solicitação sem persistir ação, posição ou operação
