## Context

Veja a motivação em `proposal.md`. A aplicação já separa valores por moeda e obtém cotações de ações brasileiras e americanas por adaptadores de mercado. A resposta de ação e a posição de carteira expõem hoje somente a cotação e os valores na moeda nativa do ativo.

## Goals / Non-Goals

**Goals:**

- Exibir equivalentes em BRL para cotações e valor atual de ativos EUA, sem mutar o preço persistido em USD.
- Usar uma fonte pública e oficial de taxa USD/BRL e tornar a indisponibilidade observável no contrato.
- Reutilizar a infraestrutura HTTP e o padrão de tratamento de falhas das integrações existentes.

**Non-Goals:**

- Não converter operações históricas, preço médio, lucros/prejuízos ou subtotais da carteira.
- Não consolidar carteiras multimoeda nem implementar conversão de outras moedas.

## Decisions

- Usar a cotação de venda USD/BRL mais recente publicada no serviço PTAX do Banco Central do Brasil. É uma fonte oficial brasileira, pública e adequada como referência; a alternativa de usar uma cotação cambial comercial em tempo real exigiria outro provedor e contrato de credenciais.
- Modelar a taxa como uma dependência de conversão isolada do adaptador de cotações de ações. Isso evita acoplar o provedor americano ao câmbio e permite testar separadamente os cenários de indisponibilidade.
- Calcular campos derivados no momento de montar as respostas, mantendo `cotacaoAtual`, `valorAtual` e `moeda` como fonte original em USD. Persistir o valor convertido geraria inconsistência quando a taxa fosse atualizada.
- Retornar campos de conversão ausentes quando o câmbio falhar, sem falhar a consulta que já dispõe de uma cotação USD válida. A alternativa de falhar toda a resposta reduziria a disponibilidade de dados de mercado já conhecidos.

## Risks / Trade-offs

- [PTAX é uma taxa de referência e pode não refletir o câmbio intradiário] → A resposta identifica a taxa e seu instante de referência.
- [Feriados e fins de semana não têm nova taxa] → Consultar a taxa mais recente disponível e expor sua data/hora.
- [Indisponibilidade da PTAX] → Preservar os valores em USD e não produzir conversão estimada.

## Migration Plan

Os novos campos de resposta são aditivos e opcionais, sem migração de dados. A implantação adiciona a URL configurável da PTAX com valor padrão público; uma reversão remove os campos derivados sem alterar ações, operações ou posições já persistidas.
