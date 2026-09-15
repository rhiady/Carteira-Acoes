## Why

A tela de Operações pode ficar vazia quando o frontend trata a resposta paginada como uma lista direta, e o contrato atual não fornece dados de ação suficientes para um relatório financeiro útil sem chamadas adicionais. Além disso, `precoMedioVenda = 0` quando não houve venda confunde um valor financeiro válido com um estado "não aplicável".

## What Changes

- Manter `GET /operacoes/carteiras/{carteiraId}` como resposta paginada e explicitar que clientes devem consumir os itens em `content`.
- Enriquecer cada operação retornada com dados básicos da ação, incluindo ticker, nome da empresa, mercado e moeda.
- Permitir que a tela de Operações monte um relatório/histórico financeiro com compra, venda, quantidade, preço, totais, custos e dados do ativo sem N+1 de ações no frontend.
- Alterar a semântica de posições para retornar `precoMedioVenda` como `null` quando ainda não houver venda para aquela posição.
- Preservar o uso de `lucroPrejuizo` da posição como fonte para estilização de ganho, perda ou neutro no frontend.

## Capabilities

### New Capabilities

### Modified Capabilities
- `portfolio-operations`: histórico de operações por carteira deve ser paginado, consumido por `content` e retornar dados enriquecidos da ação para relatório.
- `portfolio-catalog`: posições devem distinguir preço médio de venda inexistente usando `null`, mantendo `lucroPrejuizo` como indicador oficial.

## Impact

- API de operações: DTO de resposta de operação e endpoint de listagem por carteira.
- API de carteiras/posições: DTO de posição e cálculo/serialização de `precoMedioVenda`.
- Frontend: consumo de `response.content` no histórico de operações e renderização de ticker/nomeEmpresa vindos do backend.
- Testes de contrato/integração: listagem paginada enriquecida, ausência de vendas com `precoMedioVenda = null` e preservação de lucro/prejuízo negativo, positivo e zero.
