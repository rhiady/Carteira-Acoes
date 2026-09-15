## Context

Veja a motivação em `proposal.md`. O fluxo atual recebe compras e vendas no recurso HTTP, valida os DTOs, altera a posição e grava a operação em uma transação. A causa do relato ainda não foi reproduzida com o payload e a resposta reais; os testes existentes são predominantemente de serviço e não cobrem de forma completa a confirmação de persistência pelo contrato HTTP.

## Goals / Non-Goals

**Goals:**

- Reproduzir a falha de persistência pelo endpoint e identificar se ela ocorre em validação, resolução de entidades, cotação, posição, operação ou banco.
- Garantir que uma resposta HTTP 201 represente uma operação e uma posição efetivamente gravadas e consultáveis.
- Preservar rollback transacional quando qualquer etapa falhar e tornar o erro retornado acionável.

**Non-Goals:**

- Não alterar regras financeiras, cálculos de custos ou o modelo de dados sem evidência da investigação.
- Não criar endpoints alternativos nem alterar os contratos de payload além do necessário para corrigir a falha identificada.

## Decisions

- Começar por testes de integração HTTP com banco de testes e dados mínimos válidos. Isso avalia o caminho efetivamente usado pelo cliente, em vez de inferir a causa apenas pelos testes de serviço.
- Validar a operação persistida, a posição e o código HTTP depois de cada compra ou venda bem-sucedida. A resposta 201 será tratada como prova observável da persistência.
- Categorizar falhas esperadas por validação, recurso inexistente, quantidade insuficiente, indisponibilidade de cotação e integridade. Isso preserva o rollback e fornece ao frontend uma ação clara; expor detalhes internos de banco é descartado por segurança.
- Manter a transação de compra/venda como unidade única. Separar a gravação da posição e da operação poderia produzir estado financeiro inconsistente.

## Risks / Trade-offs

- [O payload ou a resposta de erro real ainda não foi fornecido] → reproduzir cenários HTTP documentados e registrar o primeiro erro concreto encontrado antes de alterar regras de negócio.
- [Uma correção de validação pode aceitar entradas anteriormente rejeitadas] → limitar a alteração ao caso reproduzido e cobrir regressões de payload inválido.
- [Falhas de cotação podem impedir operações sem preço manual] → manter o preço manual como caminho sem dependência externa e retornar erro de indisponibilidade quando o fallback for necessário.

## Migration Plan

Não há migração de dados prevista. A correção será implantada com testes de integração; se houver regressão, reverter o ajuste de fluxo preserva as operações já gravadas, pois não haverá alteração destrutiva de schema.
