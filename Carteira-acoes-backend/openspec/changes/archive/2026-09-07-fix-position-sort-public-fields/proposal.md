## Why

`GET /carteiras/{id}/acoes?sort=ticker,asc` pode gerar erro 500 porque o sort recebido pelo `Pageable` é aplicado sobre a entidade paginada `CarteiraAcao`, mas `ticker` não é propriedade direta dela; o campo existe na associação `acao.ticker`. O endpoint deve manter `sort=ticker,asc` como contrato externo e traduzir esse campo público para a propriedade real de persistência sem expor detalhes internos para o frontend.

## What Changes

- Manter o contrato funcional de `GET /carteiras/{id}/acoes` e permitir ordenação pública por `ticker`, `quantidade` e `id`.
- Traduzir `sort=ticker,<direcao>` para a propriedade JPA correta da associação de ação.
- Rejeitar campos de sort não permitidos com erro 400 padronizado, em vez de deixar a falha virar erro 500.
- Preservar paginação, resposta enriquecida de posição e comportamento sem sort explícito.
- Adicionar cobertura para sort por ticker ascendente/descendente, paginação com ticker, sort inválido e consulta sem sort explícito.

## Capabilities

### New Capabilities

### Modified Capabilities
- `portfolio-catalog`: listagem de posições ativas deve aceitar apenas campos públicos permitidos de ordenação e traduzir `ticker` para o caminho real da entidade.

## Impact

- API de carteiras/posições: validação e tradução de `Pageable.sort` antes da consulta de posições.
- Repository de posições: poderá receber sort por propriedade associada, como `acao.ticker`, sem mudar o contrato externo.
- Tratamento de erros: sort público desconhecido deve retornar 400 com erro de validação apropriado.
- Testes de integração ou service: cobertura de ordenação, paginação e rejeição de sort inválido.
