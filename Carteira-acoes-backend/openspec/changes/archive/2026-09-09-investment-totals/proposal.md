## Why

As respostas de posições expõem resultados, mas não distinguem claramente o capital ainda investido dos fluxos históricos de compra e venda. Adicionar esses totais torna a API mais útil para acompanhamento financeiro sem alterar as regras de negociação já validadas.

## What Changes

- Adicionar à resposta de posição o custo atualmente investido, o valor histórico comprado e o valor histórico vendido.
- Disponibilizar os totais também em resumo de carteira existente, quando aplicável, mantendo valores separados por moeda.
- Derivar os novos valores das operações e cálculos atuais usando `BigDecimal`, preservando médias, cotações e lucros realizados/não realizados existentes.
- Acrescentar testes numéricos, documentação OpenAPI e validação de compatibilidade aditiva.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `portfolio-operations`: ampliar o contrato de posições e resumos com totais de investimento, compras e vendas, sem modificar as regras financeiras existentes.

## Impact

- DTOs, calculadora/serviços e mapeadores responsáveis por posições e resumos.
- Consultas ou agregações do repositório de operações somente se necessárias para evitar carregar históricos inteiros.
- Testes de serviço/API e especificação OpenAPI. Frontend e deploy permanecem inalterados.
