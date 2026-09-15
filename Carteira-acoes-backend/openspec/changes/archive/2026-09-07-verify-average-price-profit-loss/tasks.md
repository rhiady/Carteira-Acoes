## 1. Cálculo financeiro

- [x] 1.1 Revisar o processamento cronológico de compras e vendas e garantir o custo médio de aquisição vigente para a posição.
- [x] 1.2 Calcular e retornar `precoMedio` com precisão decimal, sem persistir indicador derivado.
- [x] 1.3 Validar vendas parciais, custos, preço médio de venda e resultados realizado, não realizado e total.

## 2. Contrato e integração

- [x] 2.1 Adicionar `precoMedio` ao `PosicaoAcaoResponse` e preencher o campo no serviço de carteira.
- [x] 2.2 Garantir que `GET /carteiras/{id}/acoes` serialize `precoMedio` junto dos indicadores existentes.
- [x] 2.3 Garantir que o resumo continue somando resultados por moeda sem conversão implícita.

## 3. Testes e validação

- [x] 3.1 Ampliar testes unitários com compras múltiplas, venda parcial, custos e posição aberta.
- [x] 3.2 Ampliar testes de integração para verificar `precoMedio` no JSON do endpoint de posições.
- [x] 3.3 Executar a suíte de testes e validar os artefatos OpenSpec.
