## Why

A aplicação atual expõe recursos administrativos, mas não responde às perguntas centrais de uma pessoa investidora: quanto cada ativo representa, qual é o resultado de cada posição e como a cotação evoluiu. Esta mudança reposiciona as carteiras e ações como a experiência principal, apoiada por dados financeiros autoritativos do backend.

## What Changes

- Adiciona métricas de valor atual, resultado e preço médio de venda às visões de carteira e posição, sem usar valores fictícios quando os dados não estiverem disponíveis.
- Introduz gráficos responsivos de composição por ativo, resultado por ativo e evolução de cotação, usando uma única biblioteca compatível com Angular.
- Define o consumo de histórico de cotação por ação, atualização individual com recarga de histórico e atualização global de cotações.
- Reestrutura Dashboard, Carteiras, posições, Ações e fluxos de compra/venda para priorizar ativos, resultados e contexto financeiro sobre telas administrativas.
- Padroniza a escala de espaçamento, estados de carregamento/vazio de gráficos, tooltips de cálculo e apresentação acessível de resultados positivos, negativos e indisponíveis.

## Capabilities

### New Capabilities

- `portfolio-analytics`: Métricas autoritativas de carteira e posição, cartões financeiros e gráficos de composição e resultado por ação.
- `stock-market-history`: Histórico de cotação, gráfico temporal por ação e atualização individual ou global de cotações.
- `investment-focused-experience`: Hierarquia, fluxos e responsividade da experiência centrada em carteira, posição, compra e venda.

### Modified Capabilities

_Nenhuma; as specs principais anteriores foram removidas e estes contratos passam a defini-las novamente._

## Impact

Afeta DTOs e serviços HTTP, páginas Dashboard/Carteiras/Ações/Operações, componentes de posição, diálogos de compra e venda, estilos globais e testes. O backend publicará `POST /acoes/atualizar-cotacoes`, `GET /acoes/{id}/historico`, posições com cálculos oficiais em `GET /carteiras/{id}/acoes` e resumo multimoeda em `GET /carteiras/{id}/resumo`. O frontend não recalculará ou converterá valores financeiros e não inventará totais quando uma carteira tiver mais de uma moeda. Adiciona uma única dependência de gráficos compatível com Angular.
