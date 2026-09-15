## 1. Auditoria e fundação

- [x] 1.1 Inventariar todas as rotas em `app.routes.ts`, componentes, estilos, dialogs, tabelas, gráficos e estados usados por cada rota.
- [x] 1.2 Registrar baseline visual em runtime para desktop grande, notebook, tablet e mobile, incluindo overflow e dimensões relevantes.
- [x] 1.3 Ler e aplicar `frontend-design` com âncora Swiss, diferenciador de régua/faixa de contexto e disciplina de conteúdo real.
- [x] 1.4 Consolidar tokens globais de containers, page padding, gaps, tipografia, controles, cards, tabelas, dialogs, foco e breakpoints.

## 2. Shell e componentes compartilhados

- [x] 2.1 Revisar shell, navegação, toolbar, item ativo e comportamento mobile proporcional ao novo conteúdo.
- [x] 2.2 Revisar cabeçalho de página, métricas, cards, botões, inputs, filtros e paginação.
- [x] 2.3 Revisar tabelas financeiras, alinhamento numérico, altura de linha, ações e estratégia responsiva.
- [x] 2.4 Revisar chart panels, legendas, tooltips, altura mínima e alternativa textual sem inventar dados.
- [x] 2.5 Revisar todos os dialogs por categoria de tamanho, foco, padding, footer, loading, erro e comportamento mobile.
- [x] 2.6 Revisar empty, loading, error, success e indisponível para que preservem dimensões finais e próximos passos.

## 3. Rotas analíticas e de investimento

- [x] 3.1 Refatorar Home e Dashboard com container analítico, métricas, CTA `Comprar ação`, gráficos e seções de proporção adequada.
- [x] 3.2 Refatorar Analytics/gráficos e histórico de cotações com áreas dominantes e controles proporcionais.
- [x] 3.3 Refatorar Carteiras, detalhe, posições e carteira vazia, incluindo `Comprar ação` contextual e pré-seleção possível.
- [x] 3.4 Refatorar Ações e detalhe do ativo, separando informações do ativo e posição, com CTA de compra contextual quando suportado.

## 4. Operações e compra

- [x] 4.1 Refatorar Compra em formulário + resumo no desktop e coluna funcional no mobile.
- [x] 4.2 Refatorar Venda com contexto de posição, disponibilidade, preço médio, cotação, resumo e confirmação explícita.
- [x] 4.3 Revisar histórico de operações para comparação ampla de tipo, ativo, quantidade, preço, data e carteira.
- [x] 4.4 Validar descoberta, pré-seleção, estimativa, CTA `Confirmar compra`, processamento, sucesso, erro e bloqueio de dupla submissão.

## 5. Cadastros e demais rotas

- [x] 5.1 Refatorar Corretoras, consulta, criação, detalhes, tabelas e estados.
- [x] 5.2 Refatorar Usuários, criação, edição, detalhes, tabela, empty state e dialogs.
- [x] 5.3 Refatorar Carteira, Corretora e Usuário forms para largura proporcional e foco acessível.
- [x] 5.4 Confirmar que nenhuma rota adicional ou layout legado ficou fora do inventário.

## 6. Validação completa

- [x] 6.1 Percorrer todas as rotas reais em runtime e registrar cobertura por viewport.
- [x] 6.2 Corrigir overflow, gráficos comprimidos, tabelas ilegíveis, dialogs inadequados e ações ocultas.
- [x] 6.3 Validar foco, teclado, labels, contraste, estados sem dependência exclusiva de cor e preferência de movimento reduzido.
- [x] 6.4 Executar testes existentes e build de produção, corrigindo regressões introduzidas.
- [x] 6.5 Repetir a revisão visual tela a tela e marcar a change somente após todas as rotas e o fluxo de compra estarem cobertos.
