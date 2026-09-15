## 1. Preparação e contrato

- [x] 1.1 Inventariar rotas, componentes, models, mappers, services e responses backend usados atualmente.
- [x] 1.2 Atualizar tipos e mappers de posição/resumo para todos os campos financeiros confirmados, preservando nullable e compatibilidade.
- [x] 1.3 Centralizar formatters de BRL/USD, sinais, datas locais, indisponível e métricas derivadas seguras.
- [x] 1.4 Adicionar testes unitários para mapeamento, formatação, divisão por zero, moedas e ausência de campos.

## 2. Sistema visual e componentes compartilhados

- [x] 2.1 Implementar tokens Swiss/financial editorial globais: superfícies, regras, tipografia, espaçamento, foco, estados semânticos e numerais tabulares.
- [x] 2.2 Revisar shell, toolbar, sidebar, navegação ativa e comportamento de overlay em desktop/mobile.
- [x] 2.3 Consolidar componentes de loading, vazio, erro/retry, sucesso, indisponível, métricas e ações contextuais.
- [x] 2.4 Revisar dialogs para título, contexto, foco, cancelamento, validação, submissão pendente e prevenção de duplicidade.

## 3. Dashboard e analytics

- [x] 3.1 Refatorar Dashboard para priorizar seções de moeda, valor atual, resultados, quantidade e ações de atualização.
- [x] 3.2 Refatorar a página de gráficos com hierarquia contexto → indicadores → visual principal → controles → comparativos.
- [x] 3.3 Implementar composição por ação usando somente `valorAtual` comparável, com legenda e alternativa textual.
- [x] 3.4 Implementar comparação preço médio versus cotação atual e resultado por ativo com sinais e estados neutros.
- [x] 3.5 Integrar preço médio ao histórico real quando a posição correspondente estiver disponível, sem criar pontos.
- [x] 3.6 Implementar rankings somente para conjuntos completos e documentar/indicar indisponibilidade para páginas parciais.

## 4. Carteiras e posições

- [x] 4.1 Refatorar listagem de Carteiras, filtros, paginação, ações e estados assíncronos.
- [x] 4.2 Refatorar detalhe de carteira e composição/posições para leitura conjunta de ticker, empresa, quantidade, preço médio, cotação, valor e resultado.
- [x] 4.3 Preservar separação de BRL/USD e exibir preço médio de venda nulo como `—` com contexto.
- [x] 4.4 Garantir ações de compra/venda contextualizadas à carteira ou posição sem alterar contratos.

## 5. Ações e histórico de cotações

- [x] 5.1 Refatorar listagem de Ações distinguindo dados do ativo de dados da posição do usuário.
- [x] 5.2 Refatorar detalhe de ativo e atualização individual de cotação com loading, erro, retry e confirmação de resposta autoritativa.
- [x] 5.3 Refatorar histórico de cotações, filtros de período e gráfico responsivo usando apenas dados reais.
- [x] 5.4 Revisar atualização global em Dashboard/Ações e seu feedback de total, atualizadas, falhas e data/hora.

## 6. Operações

- [x] 6.1 Refatorar Compra preservando Reactive Forms, identificação aceita pelo backend, custos opcionais e estimativa não oficial.
- [x] 6.2 Refatorar Venda com posição, quantidade disponível, valor estimado, confirmação explícita e prevenção de duplicidade.
- [x] 6.3 Refatorar histórico de operações, filtros, paginação, preço médio contextual e estados de carregamento/vazio/erro.

## 7. Cadastros e demais telas

- [x] 7.1 Refatorar Corretoras, criação/consulta, tabelas, dialogs, validações e estados.
- [x] 7.2 Refatorar Usuários, criação/edição, dialog de edição, ações, validações e estados sem expor senha.
- [x] 7.3 Refatorar Home e demais rotas encontradas no inventário, mantendo títulos, ações e hierarquia consistentes.
- [x] 7.4 Revisar tabelas, filtros, paginação, formulários e modais para foco, labels, mensagens e responsividade.

## 8. Validação e refinamento

- [x] 8.1 Executar testes e build Angular, corrigindo regressões de tipagem e lint.
- [x] 8.2 Percorrer todas as rotas em runtime com responses reais/fixtures confirmadas e validar valores contra o backend.
- [x] 8.3 Inspecionar desktop e mobile: overflow, tabelas, gráficos, dialogs, ações, estados e navegação por teclado.
- [x] 8.4 Executar checagens de acessibilidade/AXE e corrigir contraste, foco, semântica e alternativas textuais.
- [x] 8.5 Fazer revisão visual final tela a tela e corrigir inconsistências antes de marcar a change concluída.
