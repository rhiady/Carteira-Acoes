## MODIFIED Requirements

### Requirement: Histórico real de cotações
O sistema SHALL persistir um registro de histórico para cada cotação válida obtida com sucesso para uma ação, inclusive na importação inicial por cadastro direto, na importação durante uma compra e nas atualizações posteriores. Cada registro SHALL conter identificador, ação, preço decimal e data/hora em UTC. O sistema SHALL expor `GET /acoes/{id}/historico` paginado, com padrão `page=0`, `size=100` e ordenação `dataHora,asc`, retornando `id`, `acaoId`, `cotacao` e `dataHora`; o endpoint SHALL aceitar filtros inclusivos opcionais `inicio` e `fim`.

#### Scenario: Cadastro direto cria histórico inicial
- **WHEN** um cliente cadastra diretamente uma ação e o provedor retorna uma cotação válida
- **THEN** o sistema persiste a ação com preço e data/hora retornados e registra um histórico correspondente

#### Scenario: Consulta cronológica de histórico
- **WHEN** um cliente consulta o histórico de uma ação com os parâmetros padrão
- **THEN** o sistema retorna uma página de registros daquela ação em ordem crescente de data/hora

#### Scenario: Histórico filtrado por período
- **WHEN** um cliente informa `inicio` e/ou `fim` válidos na consulta de histórico
- **THEN** o sistema retorna somente registros cuja data/hora esteja dentro dos limites informados

#### Scenario: Falha de cotação não cria histórico
- **WHEN** uma consulta ao provedor falha ou retorna cotação inválida
- **THEN** o sistema não altera a cotação atual e não persiste histórico para a tentativa
