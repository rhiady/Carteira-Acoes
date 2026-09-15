## Purpose

Oferecer uma experiência financeira integrada para interpretar posições, resultados, moedas e analytics reais do backend em todas as telas do produto, com clareza, acessibilidade e responsividade.

## ADDED Requirements

### Requirement: Posições exibem dados financeiros autoritativos
O sistema SHALL suportar o response de posição contendo `carteiraId`, `acaoId`, `ticker`, `nomeEmpresa`, `mercado`, `moeda`, `quantidade`, `cotacaoAtual`, `dataHoraCotacao`, `valorAtual`, `precoMedio`, `cotacaoEmBrl`, `valorAtualEmBrl`, `taxaUsdBrl`, `dataHoraTaxaUsdBrl`, `precoMedioVenda`, `lucroPrejuizoRealizado`, `lucroPrejuizoNaoRealizado` e `lucroPrejuizo`. SHALL apresentar esses campos quando fornecidos, sem recalculá-los ou fabricá-los. Campos ausentes SHALL permanecer indisponíveis; `precoMedioVenda` SHALL ser exibido como `—`.

#### Scenario: Posição com dados completos
- **WHEN** uma posição retorna preço médio, cotação, valor atual e resultados
- **THEN** a tela apresenta ticker, empresa, quantidade, preço médio, cotação, valor atual e resultado com moeda e sinal preservados

#### Scenario: Campo financeiro ausente
- **WHEN** um campo financeiro ou conversão não é retornado
- **THEN** a interface mostra estado indisponível e não substitui o valor por zero, estimativa ou conversão inventada

### Requirement: Resultados e moedas são interpretáveis
O sistema SHALL diferenciar resultado realizado, não realizado e total, formatar BRL como `R$` e USD como `US$`, e manter subtotais separados quando houver múltiplas moedas. Sinais e significado SHALL permanecer compreensíveis sem depender apenas de cor.

#### Scenario: Carteira multimoeda
- **WHEN** o resumo contém BRL e USD
- **THEN** a interface apresenta seções independentes sem somar ou converter os valores

#### Scenario: Resultado negativo
- **WHEN** um resultado é negativo
- **THEN** o valor aparece com sinal anterior ao símbolo monetário e possui indicação textual ou iconográfica complementar à cor

### Requirement: Analytics usam somente dados suficientes e reais
O sistema SHALL oferecer, quando aplicável, composição por ativo baseada em `valorAtual`, comparação entre `precoMedio` e `cotacaoAtual`, resultado por ativo e rankings derivados do conjunto completo carregado. Histórico de cotação SHALL usar exclusivamente pontos retornados pelo backend e MAY exibir uma referência identificada como `Preço médio`.

#### Scenario: Dados suficientes para composição
- **WHEN** existem posições completas com valores autoritativos comparáveis
- **THEN** a composição identifica cada ticker, valor e participação, e os gráficos têm alternativa textual acessível

#### Scenario: Dados insuficientes para série ou ranking
- **WHEN** não há histórico real ou o carregamento é parcial
- **THEN** a interface exibe estado vazio/indisponível e não apresenta série, benchmark ou ranking como completo

### Requirement: Todas as telas compartilham estados e linguagem visual
O sistema SHALL revisar Home, Dashboard, carteiras, posições, ações, histórico, operações, corretoras, usuários, formulários e dialogs usando uma linguagem Swiss/financial editorial consistente, com loading, vazio, erro, sucesso quando aplicável, retry seguro, foco visível e mensagens de erro priorizando `message`.

#### Scenario: Coleção vazia
- **WHEN** uma tela recebe uma coleção vazia
- **THEN** apresenta uma explicação acessível e a próxima ação relevante, sem parecer quebrada

#### Scenario: Falha de operação
- **WHEN** uma solicitação falha sem mensagem utilizável
- **THEN** mostra `Não foi possível concluir a operação.`, preserva contexto e valores de formulário, e evita submissão duplicada

### Requirement: Experiência permanece acessível e responsiva
O sistema SHALL manter todas as informações e ações operáveis por teclado em desktop e mobile, sem overflow acidental, gráficos cortados, dialogs maiores que a viewport ou dependência exclusiva de cor. Respeitará preferências de redução de movimento.

#### Scenario: Viewport estreito
- **WHEN** a tela é visualizada em largura mobile
- **THEN** campos, dados, ações, tabelas e gráficos permanecem alcançáveis e legíveis sem rolagem horizontal obrigatória

#### Scenario: Navegação por teclado
- **WHEN** a pessoa percorre uma tela ou dialog com teclado
- **THEN** a ordem é lógica, os rótulos são acessíveis, o foco é visível e a ação principal pode ser acionada sem armadilha de foco
