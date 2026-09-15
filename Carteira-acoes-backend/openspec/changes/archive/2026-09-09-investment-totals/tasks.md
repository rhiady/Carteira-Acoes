## 1. Mapeamento do contrato atual

- [x] 1.1 Identificar DTOs, calculadora/serviços, mapeadores e resumo usados por `GET /carteiras/{id}/acoes`.
- [x] 1.2 Identificar a origem e a ordenação das operações de compra/venda e a política de escala BigDecimal vigente.

## 2. Cálculos e dados financeiros

- [x] 2.1 Estender o resultado financeiro da posição com `valorInvestidoAtual`, `valorTotalComprado` e `valorTotalVendido`, acumulando valores brutos sem alterar fórmulas existentes.
- [x] 2.2 Garantir zero para totais sem operações e preservar `precoMedioVenda` não aplicável quando não houver vendas.
- [x] 2.3 Usar agregação de repository somente se necessária para performance, validando equivalência com o histórico cronológico e sem persistir derivados.

## 3. Respostas e moedas

- [x] 3.1 Adicionar os três campos aos DTOs/mapeadores de posição de forma aditiva, mantendo nomes e campos financeiros atuais.
- [x] 3.2 Estender o resumo de carteira existente, agregando por moeda e reutilizando exclusivamente conversão BRL/USD já disponível.
- [x] 3.3 Atualizar OpenAPI para documentar tipos, moeda e semântica dos novos campos.

## 4. Testes

- [x] 4.1 Testar somente compra (10 @ 30), incluindo investido 300, comprado 300 e vendido zero.
- [x] 4.2 Testar múltiplas compras (10 @ 30 + 10 @ 40), incluindo quantidade 20, preço médio 35 e investido/comprado 700.
- [x] 4.3 Testar compra e venda parcial (venda 5 @ 45), verificando investido 525, comprado 700, vendido 225 e resultados 50/150/200.
- [x] 4.4 Testar ausência de vendas, moedas distintas e compatibilidade dos campos existentes.
- [x] 4.5 Executar `./mvnw test`, build e smoke test da resposta de posições.

## 5. Verificação de escopo

- [x] 5.1 Confirmar que frontend, deploy, regras de Compra/Venda e histórico não foram alterados fora do necessário.
