## 1. Resumo financeiro da carteira

- [x] 1.1 Derivar no `CarteiraDetailPage` os subtotais de `valorInvestidoAtual` por moeda somente a partir do conjunto completo carregado por `allPositions`, mantendo o estado indisponível para dados incompletos.
- [x] 1.2 Reorganizar o template do detalhe para apresentar `Total investido`, `Valor atual`, `Lucro / Prejuízo` e `Ativos` em hierarquia por moeda, sem consolidar BRL e USD.
- [x] 1.3 Preservar os rótulos, formatters, sinais, tooltips e estados indisponíveis dos totais por posição e adicionar a ação explícita `Comprar primeira ação` no estado vazio.

## 2. Hierarquia visual e responsividade

- [x] 2.1 Aplicar a direção Swiss existente ao resumo, ações, analytics e posições usando tokens atuais, regras finas, tipografia alinhada à esquerda e no máximo uma ação primária dominante.
- [x] 2.2 Ajustar os estilos da tela para desktop, tablet e mobile, garantindo empilhamento progressivo, ausência de overflow horizontal, foco visível e controles utilizáveis por teclado.
- [x] 2.3 Revisar loading, erro, retry, atualização de cotações e indisponibilidade para manter contexto e mensagens acessíveis sem depender somente de cor.

## 3. Cobertura e validação

- [x] 3.1 Estender `carteira-detail.page.spec.ts` para total investido completo, separação BRL/USD, dados ausentes, carteira sem posições e ação de primeira compra.
- [x] 3.2 Executar testes unitários/componentes relevantes, validação OpenSpec e checagens AXE/manual em light/dark a 375px, 768px e desktop.
- [x] 3.3 Confirmar que não houve alteração em endpoints, serviços de backend, contratos de operação, conversão cambial ou dados financeiros autoritativos.
