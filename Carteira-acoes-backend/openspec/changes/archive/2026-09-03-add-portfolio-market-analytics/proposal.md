## Why

A API já mantém posições e cotações atuais, mas não fornece a série histórica nem os indicadores financeiros oficiais que a nova interface precisa para exibir carteira, resumo e gráficos sem repetir cálculos no Angular. A atualização também precisa preservar valores monetários por moeda e tolerar falhas pontuais de provedores ao atualizar o catálogo inteiro.

## What Changes

- Registrar cada cotação externa bem-sucedida como histórico persistente e expor a série paginada de uma ação, com filtro opcional por período.
- Manter a atualização individual de cotação e adicionar atualização global, contabilizando êxitos e falhas individuais sem descartar o processamento inteiro.
- Evoluir as posições de carteira para uma representação enriquecida, calculada pelo backend, com valor atual, preço médio de venda e resultados realizado, não realizado e total.
- Adicionar um resumo de carteira com quantidade de ativos e totais financeiros separados por moeda para uso em cards e cabeçalhos.
- Tornar explícita a identificação exclusiva de ação em compras e os custos opcionais de compras e vendas; manter o preço de venda obtido pelo provedor, sem entrada manual.
- Preservar precisão decimal, paginação e respostas de erro padronizadas; impedir consolidação silenciosa de BRL com USD enquanto não existir conversão cambial.

## Capabilities

### New Capabilities

- Nenhuma.

### Modified Capabilities

- `market-data`: histórico de cotações e atualização global resiliente para ações cadastradas.
- `portfolio-catalog`: posições enriquecidas e resumo de carteira orientados à consulta da interface.
- `portfolio-operations`: regras de identificação, custos e cálculo cronológico dos resultados financeiros oficiais.

## Impact

- Afeta entidades, migrations Flyway, repositórios, serviços de cálculo, DTOs, mappers, controllers, tratamento de exceções e testes Spring Boot.
- Adiciona `POST /acoes/atualizar-cotacoes`, `GET /acoes/{id}/historico` e `GET /carteiras/{id}/resumo`; altera a representação de `GET /carteiras/{id}/acoes` para posições enriquecidas paginadas. O resumo segmenta totais por BRL e USD.
- Reutiliza os adapters de cotação por mercado e introduz persistência de histórico; não introduz conversão cambial nem novos provedores externos.
