## Context

Veja a motivação em `proposal.md`. Atualmente, o contrato de venda não recebe preço e o serviço sempre consulta o provedor de cotação depois de validar a posição. A compra já implementa a precedência de preço informado sobre cotação externa.

## Goals / Non-Goals

**Goals:**

- Tornar o contrato de venda simétrico ao contrato de compra quanto ao preço unitário opcional.
- Preservar o comportamento atual de cotação automática para vendas sem preço.
- Evitar alteração de posição ou histórico quando a validação do preço falhar.

**Non-Goals:**

- Não alterar os cálculos financeiros, o modelo persistido nem o fluxo de compra.
- Não atualizar a cotação corrente da ação a partir do preço informado na venda.

## Decisions

- Incluir `precoUnitario` opcional e validado no DTO de venda, usando a mesma precisão/regra mínima do DTO de compra. Isso mantém um contrato de API consistente; aceitar qualquer valor positivo sem validação quebraria as garantias numéricas existentes.
- No fluxo de venda, selecionar primeiro o preço da requisição quando presente; consultar o adaptador de cotação somente quando ele estiver ausente. Essa abordagem elimina a dependência externa para a venda manual e mantém compatibilidade com clientes existentes. A alternativa de remover a cotação automática exigiria que todos os clientes enviassem um valor e seria uma quebra de contrato.
- Reutilizar o cálculo e a persistência de operação existentes, pois ambos já recebem o preço unitário resolvido e calculam valores bruto e líquido corretamente.

## Risks / Trade-offs

- [O preço manual pode divergir da cotação de mercado] → Ele representa o preço efetivamente negociado e será gravado como histórico da operação; a cotação corrente permanece sob responsabilidade do fluxo de mercado.
- [Clientes podem enviar zero ou preço negativo] → A validação de requisição rejeitará valores abaixo de `0.0001` antes da venda ser processada.

## Migration Plan

O campo é opcional e aditivo no JSON de `POST /operacoes/vendas`, portanto clientes atuais continuam usando cotação automática. Em caso de reversão, basta retirar o campo do contrato e restaurar a consulta obrigatória; não há migração de dados porque o preço já é persistido em todas as operações.
