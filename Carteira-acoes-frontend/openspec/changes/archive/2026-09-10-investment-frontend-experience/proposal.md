## Why

O frontend já possui contratos e telas de investimento, mas a experiência ainda não apresenta de forma consistente os novos dados financeiros autoritativos do backend nem oferece uma leitura clara de preço médio, resultados, moedas e composição. Esta mudança consolida a evolução financeira e a revisão visual/usabilidade de todas as rotas para que o produto seja interpretável, responsivo e acessível.

## What Changes

- Atualizar modelos, DTOs, mappers, services e superfícies de carteira para suportar preço médio, preço médio de venda, resultados realizado/não realizado/total e conversões BRL/USD fornecidas pelo backend.
- Formalizar o response de posição com `carteiraId`, `acaoId`, `ticker`, `nomeEmpresa`, `mercado`, `moeda`, `quantidade`, cotações, valores, datas, preço médio e resultados, mantendo campos de conversão nullable.
- Tratar valores autoritativos como fonte única, preservando nulos, sinais, moedas e mensagens de erro do contrato.
- Redesenhar analytics, dashboard, histórico de cotações, composição, comparação preço médio versus cotação e resultado por ativo somente quando houver dados reais suficientes.
- Revisar individualmente todas as telas existentes, incluindo ações, carteiras, operações, corretoras, usuários, formulários, dialogs, tabelas, estados assíncronos e navegação.
- Aplicar uma linguagem Swiss/financial editorial consistente, com hierarquia financeira, densidade controlada, responsividade, acessibilidade WCAG AA e motion reduzido.
- Validar a aplicação em runtime em todas as rotas e em larguras desktop/mobile, sem inventar endpoints, séries históricas ou valores financeiros.

## Capabilities

### New Capabilities

- `investment-frontend-experience`: experiência integrada de leitura financeira, analytics e refatoração global das telas, baseada nos contratos publicados e com estados acessíveis.

### Modified Capabilities

Nenhuma. Os contratos consolidados permanecem preservados; esta change acrescenta uma capacidade integrada que orquestra sua apresentação e a revisão global da experiência.

## Impact

- Código Angular em `src/app/core`, `src/app/features` e `src/app/shared/components`, incluindo models, formatters, services, rotas, componentes, tabelas e dialogs.
- Endpoints existentes de carteiras, posições, ações, histórico, operações, usuários e corretoras; nenhum endpoint novo será presumido.
- Dependência de respostas reais do backend para analytics e de execução da aplicação para revisão visual final.
- Testes, build Angular e validação manual/responsiva serão ampliados para cobrir o fluxo completo.
