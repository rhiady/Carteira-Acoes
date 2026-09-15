## MODIFIED Requirements

### Requirement: Histórico real de cotações
O sistema SHALL persistir um registro de histórico para cada cotação válida obtida com sucesso para uma ação, inclusive na importação inicial da ação e nas atualizações posteriores. A importação inicial SHALL persistir a ação, sua cotação atual e o respectivo histórico como uma única operação lógica; se qualquer etapa falhar, o sistema MUST NOT deixar ação ou histórico parcial persistido. Cada registro SHALL conter identificador, ação, preço decimal e data/hora em UTC. O sistema SHALL expor `GET /acoes/{id}/historico` paginado, com padrão `page=0`, `size=100` e ordenação `dataHora,asc`, retornando `id`, `acaoId`, `cotacao` e `dataHora`; o endpoint SHALL aceitar filtros inclusivos opcionais `inicio` e `fim`.

#### Scenario: Importação inicial com histórico
- **WHEN** uma compra importa uma ação ainda não cadastrada e o provedor retorna uma cotação válida
- **THEN** o sistema persiste a ação com o preço e data/hora retornados e registra um histórico correspondente

#### Scenario: Falha na persistência inicial
- **WHEN** a criação de uma ação importada falha antes de concluir o registro de seu histórico
- **THEN** o sistema não deixa ação nem histórico parcial persistido

#### Scenario: Consulta cronológica de histórico
- **WHEN** um cliente consulta o histórico de uma ação com os parâmetros padrão
- **THEN** o sistema retorna uma página de registros daquela ação em ordem crescente de data/hora

#### Scenario: Histórico filtrado por período
- **WHEN** um cliente informa `inicio` e/ou `fim` válidos na consulta de histórico
- **THEN** o sistema retorna somente registros cuja data/hora esteja dentro dos limites informados

#### Scenario: Falha de cotação não cria histórico
- **WHEN** uma consulta ao provedor falha ou retorna cotação inválida
- **THEN** o sistema não altera a cotação atual e não persiste histórico para a tentativa
