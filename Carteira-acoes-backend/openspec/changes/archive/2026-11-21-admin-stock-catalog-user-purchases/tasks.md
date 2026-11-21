## 1. Catálogo administrativo de ações

- [x] 1.1 Confirmar e cobrir `POST /api/acoes` como único fluxo administrativo de importação de ticker, incluindo cotação importada e ausência de carteira, posição e operação.
- [x] 1.2 Revisar OpenAPI e README para apresentar o cadastro administrativo de ação como pré-requisito da compra pelo usuário.

## 2. Contrato de compra por ação existente

- [x] 2.1 Simplificar `CompraRequest` para exigir `acaoId` e remover `ticker`, `mercado`, a validação de identificação alternativa e construtores associados.
- [x] 2.2 Alterar `OperacaoService.comprar` para resolver apenas a ação existente por `acaoId`, preservando ownership, transação, custo, preço manual e fallback de cotação.
- [x] 2.3 Remover `AcaoService.resolverOuCriar` e seus caminhos de teste depois de eliminar todos os chamadores.
- [x] 2.4 Atualizar a documentação de `POST /api/operacoes/compras` para remover ticker/mercado e descrever os erros de ação ausente, inexistente e payload legado.

## 3. Cobertura de regressão

- [x] 3.1 Atualizar fixtures e testes de serviço para criar a ação pelo catálogo antes da compra e cobrir preço manual, fallback de cotação, ação inexistente e atomicidade.
- [x] 3.2 Adicionar teste de integração HTTP que comprove que `ROLE_USER` não cria ação por `POST /api/acoes` nem por payload de compra com ticker/mercado, e que consegue comprar uma `acaoId` de catálogo na carteira própria.
- [x] 3.3 Executar a suíte Maven e validar a change com `openspec validate admin-stock-catalog-user-purchases --strict`.
