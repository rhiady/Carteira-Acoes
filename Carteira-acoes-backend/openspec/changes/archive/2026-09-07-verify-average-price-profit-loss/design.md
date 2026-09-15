## Context

Os requisitos atuais já definem os indicadores oficiais e os endpoints que os expõem. A implementação existente concentra o cálculo em um serviço dedicado e agrega os resultados na consulta de posições e no resumo; o design deve preservar essa separação e tornar as regras observáveis por testes.

## Goals / Non-Goals

**Goals:**

- Garantir cálculo determinístico a partir das operações ordenadas cronologicamente.
- Adicionar `precoMedio` ao DTO e preenchê-lo com o custo médio de aquisição vigente.
- Cobrir compras, vendas parciais, custos, posição aberta, precisão decimal e segmentação por moeda.
- Manter o contrato HTTP existente, incluindo `null` para preço médio de venda sem vendas.
- Manter os campos existentes e tornar `precoMedio` parte do contrato de posições.

**Non-Goals:**

- Alterar o modelo persistido para armazenar indicadores derivados.
- Introduzir conversão cambial no cálculo original do ativo.
- Redesenhar telas ou criar novos endpoints.

## Decisions

- Manter uma única regra de cálculo financeiro reutilizada por posições e resumo; duplicar fórmulas nos agregadores aumentaria o risco de divergência.
- Usar `BigDecimal` em toda a cadeia de cálculo e asserções decimais nos testes; ponto flutuante não atende ao contrato financeiro.
- Processar operações por data/ordem cronológica, acumulando custo e resultado realizado a cada venda; calcular o não realizado somente sobre a posição restante.
- Validar o comportamento em testes unitários do cálculo e testes de integração dos endpoints. Isso cobre a fórmula isoladamente e a serialização/agrupamento consumidos pelos clientes.
- Preservar moedas separadas no resumo; conversão USD/BRL continua limitada aos campos explícitos de equivalente em BRL.

## Risks / Trade-offs

- [Operações com datas iguais] → definir e testar um desempate estável pela ordem persistida/identificador.
- [Custos aplicados incorretamente ao preço médio de venda] → incluir cenário específico com custos e validar valor líquido apenas no resultado realizado.
- [Divergência entre posição e resumo] → testar o resumo contra a soma dos indicadores retornados para as posições da mesma moeda.

## Migration Plan

Não há migração de dados. Executar a suíte de testes, publicar a alteração mantendo o contrato atual e reverter apenas o código/testes da mudança caso a validação falhe.
