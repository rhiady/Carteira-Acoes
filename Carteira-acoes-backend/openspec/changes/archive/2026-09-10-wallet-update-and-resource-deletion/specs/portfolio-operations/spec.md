## MODIFIED Requirements

### Requirement: Atomicidade e histórico imutável
O sistema SHALL aplicar cada compra ou venda atomicamente: operação e posição MUST ser persistidas juntas, ou nenhuma mudança poderá persistir. Operações registradas MUST NOT possuir endpoint de edição genérica; o histórico de uma carteira SHALL ser retornado paginado em ordem decrescente de data/hora. Cada item do histórico SHALL conter dados suficientes para exibição de relatório, incluindo identificador da ação, ticker, nome da empresa, mercado, moeda, tipo de operação, quantidade, preço unitário, valores bruto e líquido, custos e data/hora. Uma carteira com operações ou posições vinculadas MUST NOT ser excluída, e uma tentativa de exclusão MUST preservar integralmente esse histórico e essas posições.

#### Scenario: Falha durante venda
- **WHEN** uma venda falha depois de iniciar o processamento, inclusive por falha de cotação
- **THEN** a transação é revertida e a posição e o histórico permanecem inalterados

#### Scenario: Histórico ordenado
- **WHEN** um cliente consulta as operações de uma carteira
- **THEN** o sistema retorna resultados paginados da operação mais recente para a mais antiga

#### Scenario: Histórico paginado consumível pelo frontend
- **WHEN** um cliente consulta `GET /operacoes/carteiras/{carteiraId}`
- **THEN** a API retorna uma página cujo array de operações está em `content`

#### Scenario: Operação enriquecida para relatório
- **WHEN** um cliente consulta o histórico de operações de uma carteira
- **THEN** cada operação retornada inclui ticker, nome da empresa, mercado e moeda da ação sem exigir consulta adicional por `acaoId`

#### Scenario: Exclusão bloqueada preserva histórico
- **WHEN** um cliente tenta excluir carteira que possui operação ou posição
- **THEN** a API responde 409 e nenhum registro financeiro ou posição é removido

#### Scenario: Exclusão bloqueada preserva vínculos da ação
- **WHEN** um cliente tenta excluir ação que possui operação ou posição vinculada
- **THEN** a API responde 409 e nenhuma operação, posição ou histórico financeiro é removido
