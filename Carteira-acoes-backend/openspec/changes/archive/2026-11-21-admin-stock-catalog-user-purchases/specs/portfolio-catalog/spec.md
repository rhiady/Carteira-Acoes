## MODIFIED Requirements

### Requirement: Catálogo de ações por mercado
O sistema SHALL consultar ações por identificador ou ticker e SHALL listar ações paginadamente. Somente um usuário autenticado com `ROLE_ADMIN` SHALL poder criar e importar uma ação global por `POST /acoes`, a partir de `ticker` e `mercado`, sem exigir carteira, posição, quantidade ou operação, respondendo HTTP 201 com a ação criada e o cabeçalho `Location`. A criação SHALL importar nome da empresa e cotação atual válidos do provedor compatível e persistir ticker único em maiúsculas, mercado `BRASIL` ou `EUA` e a moeda derivada BRL ou USD. O cliente MUST NOT informar nome da empresa, moeda, preço ou data/hora da cotação. O sistema MUST rejeitar ticker duplicado, ticker não localizado ou resposta sem nome ou cotação válida sem persistir a ação; uma criação bem-sucedida MUST NOT criar posição ou operação. Ações disponibilizadas nesse catálogo SHALL poder ser selecionadas por usuários comuns em suas compras, mas não poderão ser criadas ou importadas por essas compras.

#### Scenario: Administrador disponibiliza ação no catálogo
- **WHEN** um administrador envia `ticker` e `mercado` válidos para `POST /acoes` e o provedor retorna dados válidos
- **THEN** o sistema cria e retorna a ação importada com a cotação atual, sem criar carteira, posição, quantidade ou operação

#### Scenario: Usuário comum tenta disponibilizar ação
- **WHEN** um usuário autenticado com `ROLE_USER` envia `POST /acoes`
- **THEN** o sistema responde `403 Forbidden` e não persiste a ação

#### Scenario: Cadastro direto com ticker duplicado
- **WHEN** um administrador envia para `POST /acoes` um ticker já cadastrado
- **THEN** o sistema retorna erro de conflito e não altera a ação existente

#### Scenario: Dados de mercado inválidos
- **WHEN** um administrador informa ticker inexistente ou o provedor não retorna nome ou cotação válidos para o mercado
- **THEN** o sistema rejeita a solicitação sem persistir ação, posição ou operação

#### Scenario: Criação de ação por compra
- **WHEN** uma compra informa ticker e mercado ainda não cadastrados
- **THEN** o sistema rejeita a compra sem criar ação, posição ou operação

#### Scenario: Cadastro direto indisponível
- **WHEN** um administrador envia `ticker` e `mercado` válidos para `POST /acoes` e o provedor retorna dados válidos
- **THEN** o sistema cria e retorna a ação importada sem criar carteira, posição ou operação

#### Scenario: Ação brasileira válida
- **WHEN** um administrador cadastra ticker válido do mercado BRASIL ainda não existente
- **THEN** o sistema cria a ação com moeda BRL, ticker em maiúsculas, nome da empresa e cotação importados

#### Scenario: Combinação de mercado e moeda inválida
- **WHEN** um administrador informa ticker inexistente ou o provedor não retorna dados válidos para o mercado
- **THEN** o sistema rejeita a solicitação sem persistir ação, posição ou operação
