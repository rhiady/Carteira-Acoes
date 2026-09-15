## MODIFIED Requirements

### Requirement: Catálogo de ações por mercado
O sistema SHALL consultar ações por identificador ou ticker e SHALL listar ações paginadamente. Uma ação SHALL ser criada exclusivamente como parte de uma compra válida para uma carteira existente; o sistema MUST NOT expor cadastro direto de ação. Na criação pela compra, o sistema SHALL receber ticker e mercado, importar nome da empresa e cotação atual do provedor compatível, e persistir ticker único em maiúsculas, mercado `BRASIL` ou `EUA` e a moeda derivada BRL ou USD. O sistema MUST rejeitar ticker não localizado ou resposta sem nome ou cotação válida sem persistir a ação.

#### Scenario: Criação de ação por compra
- **WHEN** uma compra válida informa ticker e mercado ainda não cadastrados
- **THEN** o sistema cria a ação com seus dados de mercado e a associa somente à posição criada ou atualizada para a carteira da compra

#### Scenario: Ação brasileira válida
- **WHEN** uma compra válida informa ticker do mercado BRASIL ainda não cadastrado
- **THEN** o sistema cria a ação com moeda BRL, ticker em maiúsculas, nome da empresa e cotação importados

#### Scenario: Cadastro direto indisponível
- **WHEN** um cliente solicita o cadastro direto de uma ação
- **THEN** o sistema rejeita a operação e não persiste ação sem uma compra vinculada a carteira

#### Scenario: Combinação de mercado e moeda inválida
- **WHEN** uma compra informa ticker inexistente ou o provedor não retorna dados válidos para o mercado
- **THEN** o sistema rejeita a compra sem persistir ação, posição ou operação
