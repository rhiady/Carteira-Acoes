## 1. Contrato de venda

- [x] 1.1 Adicionar `precoUnitario` opcional ao DTO de venda, validando o mínimo de `0.0001`.
- [x] 1.2 Ajustar construtores, chamadas e exemplos afetados pelo novo campo sem quebrar clientes que omitem o preço.

## 2. Registro de operação

- [x] 2.1 Alterar o fluxo de venda para usar o preço recebido quando presente e consultar a cotação apenas quando ausente.
- [x] 2.2 Garantir que preço manual inválido ou falha de cotação no fallback não alterem posição nem persistam a operação.

## 3. Cobertura e documentação

- [x] 3.1 Criar ou atualizar testes para venda com preço manual, venda sem preço usando cotação e rejeição de preço inválido.
- [x] 3.2 Atualizar o README com o campo opcional e exemplos de `POST /operacoes/vendas`.
- [x] 3.3 Executar a suíte de testes relevante e validar o contrato OpenSpec da mudança.
