## Why

O registro de vendas falha quando a cotação externa está indisponível, embora o usuário já possua o preço efetivamente negociado. Compras já aceitam esse valor manualmente; vendas precisam oferecer a mesma alternativa para que o histórico possa ser registrado sem depender do provedor de mercado.

## What Changes

- Aceitar `precoUnitario` opcional no corpo de uma solicitação de venda.
- Usar o preço informado para registrar a venda sem consultar cotação externa.
- Manter a busca de cotação externa como alternativa quando o preço não for informado.
- Validar que o preço manual de venda, quando enviado, seja maior ou igual a `0.0001`.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `portfolio-operations`: permitir preço unitário opcional em vendas e definir a precedência entre preço manual e cotação externa.

## Impact

- API `POST /operacoes/vendas` e seu DTO de requisição.
- Serviço de operações e testes de compra/venda.
- Documentação do contrato de operações no README.
