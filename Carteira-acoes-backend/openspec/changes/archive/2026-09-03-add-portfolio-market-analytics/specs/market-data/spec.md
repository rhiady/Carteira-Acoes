## MODIFIED Requirements

### Requirement: Consulta normalizada de cotação
O sistema SHALL obter a cotação de uma ação por um provedor compatível com seu mercado e SHALL normalizar o resultado em preço decimal e data/hora em UTC. A API de atualização de cotação SHALL atualizar os dados atuais da ação somente quando a consulta for bem-sucedida e SHALL registrar o preço e a data/hora retornados no histórico de cotações.

#### Scenario: Atualização bem-sucedida
- **WHEN** um cliente solicita atualizar a cotação de uma ação existente e o provedor retorna uma cotação válida
- **THEN** o sistema persiste o novo preço e a data/hora da cotação, registra um histórico e retorna a ação atualizada

#### Scenario: Ação não encontrada
- **WHEN** um cliente solicita atualização para uma ação inexistente
- **THEN** o sistema retorna erro padronizado de ação não encontrada

### Requirement: Sem preço fictício em falhas
O sistema MUST NOT substituir uma cotação ausente por zero, valor estimado ou valor fictício. Quando a cotação não for encontrada, o provedor exigir autenticação, aplicar limite de uso ou estiver indisponível, o sistema SHALL retornar uma categoria de erro padronizada que permita ao cliente distinguir essas condições.

#### Scenario: Provedor indisponível
- **WHEN** a consulta de cotação falha por timeout ou erro transitório do provedor
- **THEN** o sistema retorna erro de API indisponível e não altera a cotação existente

#### Scenario: Limite de uso excedido
- **WHEN** o provedor responde que o limite de requisições foi atingido
- **THEN** o sistema retorna erro de limite de uso sem repetir imediatamente a chamada

## ADDED Requirements

### Requirement: Histórico real de cotações
O sistema SHALL persistir um registro de histórico para cada cotação válida obtida com sucesso para uma ação, inclusive na importação inicial da ação e nas atualizações posteriores. Cada registro SHALL conter identificador, ação, preço decimal e data/hora em UTC. O sistema SHALL expor `GET /acoes/{id}/historico` paginado, com padrão `page=0`, `size=100` e ordenação `dataHora,asc`, retornando `id`, `acaoId`, `cotacao` e `dataHora`; o endpoint SHALL aceitar filtros inclusivos opcionais `inicio` e `fim`.

#### Scenario: Consulta cronológica de histórico
- **WHEN** um cliente consulta o histórico de uma ação com os parâmetros padrão
- **THEN** o sistema retorna uma página de registros daquela ação em ordem crescente de data/hora

#### Scenario: Histórico filtrado por período
- **WHEN** um cliente informa `inicio` e/ou `fim` válidos na consulta de histórico
- **THEN** o sistema retorna somente registros cuja data/hora esteja dentro dos limites informados

#### Scenario: Falha de cotação não cria histórico
- **WHEN** uma consulta ao provedor falha ou retorna cotação inválida
- **THEN** o sistema não altera a cotação atual e não persiste histórico para a tentativa

### Requirement: Atualização global resiliente de cotações
O sistema SHALL expor `POST /acoes/atualizar-cotacoes` para atualizar todas as ações cadastradas usando o provedor apropriado para cada mercado. O processamento SHALL tentar cada ação independentemente; uma falha individual MUST NOT impedir a tentativa das demais ações. A resposta SHALL conter `total`, `atualizadas`, `falhas` e `dataHora` em UTC, e SHALL satisfazer `total = atualizadas + falhas`.

#### Scenario: Atualização global parcialmente bem-sucedida
- **WHEN** uma solicitação de atualização global encontra cotações válidas para parte das ações e falha para as demais
- **THEN** o sistema atualiza e historiza somente as ações bem-sucedidas e retorna os contadores correspondentes sem falhar toda a solicitação

#### Scenario: Atualização global totalmente bem-sucedida
- **WHEN** o provedor retorna cotação válida para todas as ações cadastradas
- **THEN** o sistema atualiza e registra histórico para todas e retorna `falhas` igual a zero
