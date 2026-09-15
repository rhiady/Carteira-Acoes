# Tasks: Enhance Investment Product Experience

## 1. Foundation
- [x] 1.1–1.9 Foundation: frontend-design, OpenSpec/arquitetura, tokens de cor/tipografia/spacing/motion, reduced-motion, Material e centralização de tokens.

## 2. Application Shell
- [x] 2.1–2.8 Sidebar, grupos Investimentos/Gestão, estado ativo, toolbar, largura, responsividade, redução de aparência administrativa e transições.

## 3. Home / Início
- [x] 3.1–3.12 Rota principal, resumo BRL/USD, carteiras, posições, movimentações disponíveis, skeletons, estados acionáveis, responsividade e ausência de dados fictícios.

## 4. Dashboard
- [x] 4.1–4.10 Área analítica, hierarquia, gráficos, painéis por moeda, legendas/tooltips, estados, sinais de resultado, montagem real e sem séries artificiais.

## 5. Carteiras
- [x] 5.1–5.8 Cards, subtotal/resultado, tickers, hover, navegação contextual, remoção de Material padrão, hierarquia e estados.

## 6. Detalhe da Carteira
- [x] 6.1–6.13 Header, ações, métricas, gráficos, moedas separadas, posições financeiras, ticker/cotação/lucro, preço médio nulo, estados e paginação.

## 7. Creation Dialogs
- [x] 7.1–7.13 Auditoria e conversão apropriada de criações, compra/venda, padrão visual, loading/error, transições, Reactive Forms, validações, foco e ausência de duplicação.

## 8. Generic Details Refactor
- [x] 8.1–8.9 Localizar `Detalhes`, CTAs contextuais, páginas ricas, dialogs simples de operação/corretora/usuário, títulos não técnicos e evitar páginas vazias.

## 9. Ações
- [x] 9.1–9.8 Lista, ticker/cotação, metadata, atualização, hover, CTA `Ver ativo`, mercado/moeda e estados.

## 10. Detalhe da Ação
- [x] 10.1–10.8 Hero, gráfico, timestamp, filtros, estados, histórico real, ações e validação visual.

## 11. Operações
- [x] 11.1–11.15 Página orientada a carteira, contrato paginado, dados de ativo, filtros, seletor, rows, valor líquido, semântica, paginação, estados, resumos autoritativos, sem agregados incorretos/N+1 e hierarquia contextual.

## 12. Operation Dialog
- [x] 12.1–12.8 Dialog responsivo com ticker/empresa/tipo, quantidade × preço, bruto/custos/líquido, data, sem IDs principais e somente `OperacaoResponse`.

## 13. Corretoras
- [x] 13.1–13.7 Lista, densidade, criação, detalhe em dialog, situação/CVM, natureza administrativa e prioridade ao nome fantasia.

## 14. Usuários
- [x] 14.1–14.6 Lista, criação/detalhe em dialog, sem senha, natureza administrativa e prioridade a nome/email.

## 15. Motion
- [x] 15.1–15.8 Hover, botões, dialogs, loading/conteúdo, atualização, reduced-motion, ausência de animação decorativa e tokens globais.

## 16. UI States
- [x] 16.1–16.7 Skeletons, empty/error/retry, refresh sem flashes, preservação de conteúdo e ausência de mocks.

## 17. Accessibility
- [x] 17.1–17.8 Foco de dialogs, teclado, contraste, semântica de resultados, reduced-motion, labels, foco visível e alternativas de gráficos.

## 18. Frontend Runtime Review
- [x] 18.1–18.18 Executar com API disponível, validar todas as rotas/fluxos/dialogs/viewports, console/Network e registrar incompatibilidades sem corrigir backend. (API validada; inspeção visual manual de navegador não disponível neste ambiente.)

## 19. Automated Frontend Validation
- [x] 19.1–19.10 Rodar testes/build/diff, corrigir regressões, validar contratos, autoridade financeira, moedas, dialogs, estados e navegação Home.

## 20. Final Review
- [x] 20.1–20.13 Comparar proposal/design/specs, revisar aparência, papéis Home/Dashboard, consistência, dialogs, skill, paleta, motion, dados reais, incompatibilidades e prontidão para archive. (Revisão estática concluída.)

## 21. Expanded Product Scope
- [x] 21.1 Atualizar e validar a experiência de comparação entre carteiras sem conversão implícita. (Resumos de carteiras permanecem separados por moeda e não realizam conversão.)
- [x] 21.2 Validar a experiência de desempenho do ativo sem simular posições ou métricas ausentes. (Detalhe e histórico usam somente dados retornados pela API.)
- [x] 21.3 Auditar novamente todas as rotas legadas após as novas mudanças de escopo.
- [x] 21.4 Confirmar critérios de conclusão de produto contra proposal, design e specs atualizados.
