## 1. Contratos, diagnóstico e criação de carteira

- [x] 1.1 Reproduzir o erro de criação com `ROLE_USER` e `ROLE_ADMIN`, conferir rota, identidade autenticada, lista de corretoras, payload enviado e resposta do backend sem alterar o contrato.
- [x] 1.2 Atualizar os testes de `CarteiraFormPage`, `CarteiraDialogComponent`, rota e serviço para garantir ownership implícito nos dois papéis, ausência de seletor de usuário, ausência de listagem de usuários, dependência obrigatória de corretora, bloqueio sem identidade e preservação de valores após falha.
- [x] 1.3 Unificar de fato a composição visual da página e do diálogo de carteira e seus estados de loading, vazio, erro e sucesso para os dois papéis, mantendo Angular Material, foco visível, uma ação primária e layout sem overflow em 375px.
- [x] 1.4 Validar que `CarteiraService`, `AuthService` e os modelos continuam alinhados ao contrato publicado e que nenhuma correção inventa usuário, corretora, endpoint ou fallback financeiro.
- [x] 1.5 Remover o controle visual de `usuarioId`, o seletor e a chamada de `UsuarioService` do `CarteiraDialogComponent`; manter `usuarioId` no payload de `POST /api/carteiras`, preenchido pela identidade autenticada, e cobrir o payload/erro de identidade com teste.
- [x] 1.6 Implementar o redesign guiado pelas imagens de `Melhorar/` nas duas superfícies de criação: hierarquia editorial, largura útil, agrupamento de campos, estados, ações, foco e breakpoints para mobile, tablet e desktop.

## 2. Navegação administrativa

- [x] 2.1 Atualizar a entrada/lateral administrativa para incluir `Ações`, `Carteiras` e `Operações` nas rotas já publicadas, com nomes acessíveis, estado ativo e sem duplicar páginas.
- [x] 2.2 Atualizar testes de navegação/rotas para confirmar que administrador vê os atalhos e usuário comum não recebe links administrativos nem acesso indireto a carteiras de terceiros.
- [x] 2.3 Verificar o comportamento do overlay de navegação, foco, fechamento por teclado e leitura em desktop, tablet e mobile nos dois temas.
- [x] 2.4 Remover da listagem administrativa de usuários a ação de abrir carteiras vinculadas; a tabela deve permanecer somente informativa.

## 3. Filtro de mercado e mappers financeiros

- [x] 3.1 Adicionar o estado local do filtro `Todos`/`Brasil`/`EUA` nas telas de carteira e operações, preservando o contexto da rota e a separação de moeda.
- [x] 3.2 Estender ou corrigir mappers existentes para filtrar por `Mercado`, manter `BRL`/`USD`, distinguir `null` de zero, preservar sinais e suprimir agregações quando a página for parcial.
- [x] 3.3 Cobrir os mappers com testes para mistura BRL/USD, filtro vazio, valores nulos, resultado negativo, valor zero e histórico incompleto, usando apenas campos autoritativos.
- [x] 3.4 Atualizar os textos de contexto dos cards, tabelas e resumos para identificar mercado/moeda selecionados e declarar indisponibilidade quando necessário.

## 4. Gráficos e apresentação interativa

- [x] 4.1 Estender `ChartPanelComponent` para aceitar estados e conteúdo textual acessível consistentes, mantendo a dependência de gráficos existente e sem criar um novo wrapper de página.
- [x] 4.2 Atualizar as configurações dos gráficos de composição, resultado, comparação, fluxo e volume para usar os mappers filtrados e os mesmos formatters nos eixos, labels, tooltips e resumos.
- [x] 4.3 Adicionar interação de hover/foco, legendas compreensíveis, sinal textual para positivo/negativo, reduced motion e tratamento de `null` sem desenhar valores fictícios.
- [x] 4.4 Ajustar a grade responsiva para empilhar gráficos e manter títulos, filtros, labels, tabelas e alternativas textuais utilizáveis sem rolagem horizontal.
- [x] 4.5 Atualizar testes de páginas/componentes para verificar filtro, série correta, tooltip/resumo textual, loading, vazio, erro, retry e números formatados.

## 5. Direção visual e acessibilidade

- [x] 5.1 Aplicar a direção Swiss/financial editorial existente: superfícies neutras, azul Yves Klein como seleção/ação, regras finas, tipografia sans existente, numerais tabulares e o market lens como assinatura visual.
- [x] 5.2 Conferir contraste WCAG AA, foco, nomes acessíveis, mensagens não dependentes apenas de cor, ordem de tabulação e comportamento de reduced motion nos fluxos alterados.
- [ ] 5.3 Validar visualmente as páginas de criação, administração, carteira e operações em 375px, tablet e desktop, em claro e escuro, usando as imagens de `Melhorar/` como referência e corrigindo o que a inspeção renderizada revelar.

## 6. Verificação final

- [x] 6.1 Executar testes unitários, de rota e de componentes relacionados a carteiras, operações, navegação, mappers e gráficos.
- [x] 6.2 Executar build estrito, AXE e checagens de overflow/responsividade; confirmar que os fluxos sem dados mostram estados honestos.
- [x] 6.3 Executar `openspec validate --change "unify-portfolio-creation-and-analytics" --strict` e registrar qualquer dependência de backend que permaneça explicitamente indisponível.
