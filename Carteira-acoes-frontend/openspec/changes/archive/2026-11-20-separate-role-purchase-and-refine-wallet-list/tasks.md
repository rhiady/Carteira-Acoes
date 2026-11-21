## 1. Compra contextual por papel

- [x] 1.1 Mapear os chamadores de `CompraPage`, rotas, guardas, contrato `CompraRequest`, ownership da carteira e testes atuais; preservar a carteira contextual, referência exclusiva do ativo, custos, confirmação e submissão segura.
- [x] 1.2 Manter para `ROLE_ADMIN` a referência completa por ID ou ticker e mercado, com os comportamentos existentes de validação, mercado e normalização de ticker.
- [x] 1.3 Para `ROLE_USER`, carregar somente as posições da carteira validada, renderizar estados de loading/vazio/erro acessíveis e permitir selecionar uma posição como referência por `acaoId`.
- [x] 1.4 Preencher o preço unitário inicial com `cotacaoAtual` quando disponível, manter a edição manual e a indicação de sugestão/indisponibilidade, e preservar o resumo não autoritativo e a confirmação única.

## 2. Lista de carteiras

- [x] 2.1 Reorganizar `CarteirasPage` e estilos relacionados em uma leitura financeira Swiss: nome, valores por moeda, resultado textual, ativos/tickers e ação para abrir a carteira antes de metadados técnicos.
- [x] 2.2 Reduzir bordas, cartões e identificadores redundantes sem alterar a listagem autenticada, resumos, previews, dialogs, paginação, estados ou ações de editar/excluir existentes.
- [x] 2.3 Garantir uma coluna legível em telas estreitas, foco visível, sem overflow horizontal e contraste equivalente nos temas claro e escuro.

## 3. Cobertura e validação

- [x] 3.1 Estender testes de compra para os dois papéis, posições próprias, seleção de ativo, cotação inicial/substituída/ausente, payload de referência exclusiva e estados de erro/confirmar.
- [x] 3.2 Estender testes da lista de carteiras para hierarquia financeira, moedas separadas, preview de ativos, ação de continuidade e estados de lista.
- [x] 3.3 Executar testes focados e completos, build de produção, AXE, detector/auditoria Impeccable e checagens renderizadas em 375px, tablet e desktop quando o navegador estiver disponível; validar a change OpenSpec e confirmar que não houve alteração de API, dependência ou cálculo financeiro.

## 4. Descoberta administrativa

- [x] 4.1 Expor Carteiras na entrada e navegação administrativa, reutilizando a rota autenticada e preservando a propriedade da carteira.
- [x] 4.2 Cobrir a nova entrada administrativa e revalidar os fluxos de compra por papel.
