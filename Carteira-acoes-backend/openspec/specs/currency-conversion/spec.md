# currency-conversion Specification

## Purpose

Fornecer uma taxa USD/BRL de referência confiável para apresentar valores de ações americanas também em reais, sem alterar os valores originais em dólar.

## Requirements

### Requirement: Taxa USD/BRL de referência
O sistema SHALL consultar a cotação de venda do dólar comercial USD/BRL publicada pela PTAX do Banco Central do Brasil e SHALL usar a taxa mais recente disponível para a conversão. A taxa SHALL conter valor decimal, fonte e data/hora de referência. O sistema MUST calcular valores em BRL exclusivamente com precisão decimal.

#### Scenario: Taxa PTAX disponível
- **WHEN** o sistema precisa converter uma cotação válida em USD para BRL e a PTAX possui uma taxa disponível
- **THEN** o sistema usa a cotação de venda mais recente e retorna o valor convertido, a taxa e sua data/hora de referência

#### Scenario: Dia sem publicação de PTAX
- **WHEN** a consulta ocorre em dia sem nova cotação PTAX publicada
- **THEN** o sistema usa a taxa mais recente disponível antes da consulta e informa a data/hora correspondente

### Requirement: Falha de conversão não altera valor em USD
O sistema MUST NOT estimar, arredondar por ponto flutuante ou substituir uma taxa USD/BRL indisponível. Quando a fonte de câmbio falhar ou não retornar uma taxa válida, o sistema SHALL manter os dados originais em USD disponíveis e SHALL retornar os campos equivalentes em BRL como ausentes.

#### Scenario: Fonte de câmbio indisponível
- **WHEN** uma ação americana possui cotação em USD válida mas a consulta à PTAX falha
- **THEN** o sistema retorna a cotação original em USD e não retorna valor, taxa nem data/hora de conversão em BRL

### Requirement: Conversão monetária sem mistura silenciosa
Valores originais em BRL ou USD SHALL permanecer identificáveis por moeda; equivalentes BRL, taxa e timestamp SHALL ser nulos ou ausentes quando não houver taxa válida, sem substituir ausência por zero.

#### Scenario: Câmbio indisponível
- **WHEN** não existe taxa USD/BRL válida para uma posição americana
- **THEN** os valores USD permanecem disponíveis e os campos BRL dependentes não são fabricados
