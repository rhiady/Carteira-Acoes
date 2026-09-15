## Context

As regras atuais já calculam posição, preço médio e resultados a partir do histórico cronológico de operações (ver `portfolio-operations`). A mudança deve apenas expor novos derivados, sem criar uma fonte de verdade paralela.

## Goals / Non-Goals

**Goals:**

- Estender o resultado financeiro interno e os DTOs de posição/resumo com os três totais históricos e o custo atual.
- Calcular compras e vendas brutas a partir das operações existentes, com `BigDecimal` e escala consistente.
- Manter separação de moedas e compatibilidade JSON/OpenAPI.

**Non-Goals:**

- Alterar as fórmulas de compra, venda, preço médio ou lucro existentes.
- Persistir valores derivados, criar endpoint novo, ou modificar frontend/deploy.

## Decisions

- Reutilizar o processamento financeiro cronológico já existente e acrescentar acumuladores de valor bruto comprado e vendido. Isso evita duplicação e garante que vendas parciais continuem reduzindo apenas o custo atual; uma query agregada só será adotada se a implementação existente não fornecer as operações sem custo desproporcional.
- Calcular `valorInvestidoAtual` no mesmo ponto em que quantidade e preço médio são consolidados (`quantidadeAtual × precoMedio`), preservando a política de arredondamento vigente.
- Adicionar campos de forma aditiva aos DTOs e mapeadores. O resumo agregará posições por moeda; conversão BRL/USD será delegada ao mecanismo já existente.
- Cobrir os cenários da especificação com testes unitários e de contrato/API, além de atualizar o schema OpenAPI gerado/manual.

## Risks / Trade-offs

- [Histórico muito grande] → preferir agregação no repository quando o serviço não puder reutilizar a carga já necessária, verificando equivalência com o cálculo cronológico.
- [Arredondamento divergente] → reutilizar constantes/utilitários de escala existentes e comparar os valores esperados nos testes.
- [Clientes desconhecem campos novos] → manter nomes e tipos aditivos e não remover ou renomear propriedades atuais.

## Migration Plan

Nenhuma migração de banco. Publicar a versão com campos opcionais/aditivos, executar a suíte de testes e validar uma resposta de `GET /carteiras/{id}/acoes`; rollback consiste em reverter o artefato de aplicação.
