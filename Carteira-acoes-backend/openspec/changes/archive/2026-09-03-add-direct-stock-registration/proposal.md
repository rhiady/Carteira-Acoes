## Why

Hoje uma ação só pode ser criada junto com uma compra, obrigando o usuário a registrar uma operação financeira para simplesmente incluir um ativo no catálogo. O cadastro direto por ticker permite preparar e acompanhar ações antes de investir nelas.

## What Changes

- Expor `POST /acoes` para cadastrar diretamente uma ação a partir de `ticker` e `mercado`.
- Importar nome, cotação atual, moeda e data/hora pelo provedor de mercado apropriado; o cliente não informa esses dados.
- Persistir a ação e seu histórico inicial sem criar carteira, posição ou operação.
- Rejeitar ticker duplicado, identificação inválida e respostas externas inválidas sem deixar dados parciais.
- Documentar e testar o novo contrato HTTP.

## Capabilities

### New Capabilities

_Nenhuma._

### Modified Capabilities

- `portfolio-catalog`: permite criar uma ação diretamente por ticker e mercado, além da criação durante uma compra.
- `market-data`: exige o registro de cotação e histórico inicial também para o cadastro direto de ação.

## Impact

Afeta o recurso e serviço de ações, DTOs, adaptadores de cotação, histórico de cotações, testes de integração e documentação da API. Adiciona o endpoint `POST /acoes` sem remover fluxos existentes.
