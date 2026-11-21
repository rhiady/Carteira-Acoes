## Why

O backend passou a aceitar compras apenas de ações previamente cadastradas, identificadas por `acaoId`. O formulário ainda permite enviar ticker e mercado, o que agora retorna `400` e impede compras nesses caminhos.

## What Changes

- **BREAKING**: enviar toda compra para `/api/operacoes/compras` com `carteiraId`, `acaoId`, quantidade e campos financeiros opcionais; remover ticker e mercado do payload e do DTO de compra.
- Substituir a entrada manual de ID/ticker/mercado pela seleção de uma ação cadastrada, carregada da listagem publicada de ações.
- Manter a compra contextual por posição, usando seu `acaoId`, e permitir a primeira compra ao selecionar uma ação cadastrada.
- Atualizar validações e testes para impedir submissão sem `acaoId` e confirmar que referências legadas não são enviadas.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `investment-api-integration`: compras passam a exigir exclusivamente a referência por `acaoId` de uma ação existente.
- `contextual-trading-experience`: a escolha de ativo na compra passa a usar somente ações cadastradas e seu identificador.
- `trading-analytics-experience`: a validação da identificação de compra deixa de aceitar ticker/mercado.

## Impact

- Código afetado: `CompraRequest`, `OperacaoService`, `CompraPage`, `AcaoService` e seus testes de contrato, página e rotas contextuais.
- API/backend: consome `GET /api/acoes` para a seleção e `POST /api/operacoes/compras` com `acaoId`; não cria endpoints nem altera regras financeiras no cliente.
- Dependências: nenhuma; reutiliza os serviços, DTOs, paginação, Angular Material e padrões de erro existentes.
