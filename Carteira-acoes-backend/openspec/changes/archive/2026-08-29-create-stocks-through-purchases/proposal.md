## Why

O cadastro direto de ações permite criar itens no catálogo sem vínculo com nenhuma carteira ou posição. A entrada de uma ação na carteira deve ocorrer exclusivamente por uma compra, para que ação, posição e operação sejam criadas de forma coerente e atômica.

## What Changes

- Permitir que uma compra identifique uma ação existente por `acaoId` ou informe `ticker` e `mercado` para localizar/criar a ação durante a própria compra.
- Criar a ação e a primeira posição somente quando uma compra válida para uma carteira existente exigir um ticker ainda não cadastrado.
- **BREAKING** Remover o cadastro direto por `POST /acoes`; as rotas de consulta do catálogo permanecem disponíveis.
- Rejeitar requisições de compra com identificador de ação e ticker/mercado simultâneos, ou sem uma forma válida de identificar a ação.

## Capabilities

### New Capabilities

_Nenhuma._

### Modified Capabilities

- `portfolio-catalog`: ações deixam de ser cadastráveis diretamente e passam a entrar no catálogo apenas pelo fluxo de compra.
- `portfolio-operations`: compra aceita identificação de ação existente ou dados de um novo ticker e cria ação/posição de maneira atômica para a carteira informada.

## Impact

- Contratos HTTP de `POST /acoes` e `POST /operacoes/compras`, DTOs, validações e documentação OpenAPI.
- Serviços de ação/operação, transação de compra, integrações de cotação e testes de recursos/serviços.
- Clientes devem migrar do cadastro prévio de ação para a compra com `ticker` e `mercado` quando necessário.
