## Purpose

Define como o frontend apresenta os totais financeiros autoritativos retornados pelo backend, mantendo distinção entre capital investido, fluxos históricos e resultados por moeda.

## Requirements

### Requirement: Totais financeiros de posição são claros
O frontend SHALL exibir, quando presentes na posição, `valorInvestidoAtual`, `valorTotalComprado`, `valorTotalVendido`, `lucroPrejuizoRealizado`, `lucroPrejuizoNaoRealizado` e `lucroPrejuizo` com rótulos contextuais, sem recalcular ou substituir os valores autoritativos.

#### Scenario: Posição completa
- **WHEN** uma posição retorna todos os totais financeiros
- **THEN** a interface apresenta investido atualmente, valor atual, total comprado, total vendido, resultado realizado, resultado não realizado e resultado total na moeda da posição.

### Requirement: Ausência, sinais e moedas são preservados
Valores nulos ou ausentes SHALL aparecer como indisponíveis (`—` quando semanticamente ausentes), resultados SHALL preservar sinal textual e BRL/USD SHALL usar seus formatters próprios.

#### Scenario: Preço médio de venda ausente
- **WHEN** `precoMedioVenda` é null
- **THEN** a interface mostra `—` e não `R$ 0,00`, `null`, `undefined` ou `NaN`.

### Requirement: Totais globais não usam página parcial
O frontend MUST NOT calcular totais de carteira somando somente o conteúdo de uma página paginada; apenas valores autoritativos do backend ou um conjunto comprovadamente completo podem formar totais globais.

#### Scenario: Posições paginadas
- **WHEN** somente uma página de posições é carregada
- **THEN** a interface mostra os valores por posição e mantém totais globais indisponíveis quando não fornecidos pelo backend.
