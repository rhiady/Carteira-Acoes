## MODIFIED Requirements

### Requirement: Consulta normalizada de cotação
O sistema SHALL obter a cotação de uma ação por um provedor compatível com seu mercado e SHALL normalizar o resultado em preço decimal e data/hora em UTC. A API de atualização de cotação SHALL atualizar os dados atuais da ação somente quando a consulta for bem-sucedida e SHALL registrar o preço e a data/hora retornados no histórico de cotações. Nas respostas de ações do mercado EUA, o sistema SHALL preservar a cotação original em USD e SHALL expor adicionalmente `cotacaoEmBrl`, `taxaUsdBrl` e `dataHoraTaxaUsdBrl` quando a conversão USD/BRL estiver disponível; esses campos MUST estar ausentes para ações de outros mercados.

#### Scenario: Atualização bem-sucedida
- **WHEN** um cliente solicita atualizar a cotação de uma ação existente e o provedor retorna uma cotação válida
- **THEN** o sistema persiste o novo preço e a data/hora da cotação, registra um histórico e retorna a ação atualizada

#### Scenario: Cotação americana convertida para real
- **WHEN** um cliente consulta ou atualiza uma ação do mercado EUA com cotação em USD e a taxa USD/BRL está disponível
- **THEN** a resposta preserva a cotação e moeda USD originais e inclui `cotacaoEmBrl`, `taxaUsdBrl` e `dataHoraTaxaUsdBrl`

#### Scenario: Ação não encontrada
- **WHEN** um cliente solicita atualização para uma ação inexistente
- **THEN** o sistema retorna erro padronizado de ação não encontrada
