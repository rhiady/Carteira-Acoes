## Why

Compras e vendas não estão sendo gravadas no banco segundo o relato do usuário, apesar de o fluxo já possuir persistência transacional. É necessário reproduzir o caminho HTTP real, identificar a validação, restrição ou falha de integração que impede a gravação e tornar o resultado observável para o cliente.

## What Changes

- Auditar o fluxo ponta a ponta de `POST /operacoes/compras` e `POST /operacoes/vendas`, incluindo validação, resolução de carteira/ação, cotação opcional, posição e operação.
- Corrigir a causa de persistência identificada, preservando a atomicidade entre posição e operação.
- Retornar erros padronizados e acionáveis quando uma requisição não puder ser persistida, sem mascarar a causa como sucesso.
- Adicionar cobertura de integração HTTP e de persistência para payloads válidos e falhas relevantes.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `portfolio-operations`: garantir que operações válidas sejam persistidas de forma verificável e que impedimentos de persistência sejam reportados de maneira padronizada ao cliente.

## Impact

- `OperacaoResource`, `OperacaoService`, DTOs de compra e venda, tratamento global de exceções e repositórios de operação/posição.
- Testes de integração HTTP, JPA e documentação de contratos de erro, caso a investigação revele divergência de payload ou validação.
