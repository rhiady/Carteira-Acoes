## Purpose

Define uma escala visual global e proporcional para que cada rota use o espaço disponível de acordo com seu conteúdo, mantendo consistência, legibilidade, acessibilidade e responsividade em todo o produto financeiro.

## ADDED Requirements

### Requirement: Rotas usam composição proporcional ao conteúdo
O sistema SHALL apresentar todas as rotas existentes com uma largura útil e uma hierarquia adequadas ao tipo de tela: páginas analíticas e tabelas SHALL usar áreas amplas, detalhes SHALL usar largura intermediária e formulários simples SHALL manter largura controlada. Nenhuma rota SHALL permanecer artificialmente estreita em uma viewport desktop ampla.

#### Scenario: Dashboard em desktop amplo
- **WHEN** o Dashboard é aberto em uma viewport desktop ampla
- **THEN** seus indicadores, gráficos, ações e seções ocupam uma proporção adequada da área disponível sem comprimir informações relevantes

#### Scenario: Formulário simples em desktop
- **WHEN** um formulário simples de cadastro é aberto em desktop
- **THEN** seus campos permanecem confortáveis e legíveis sem expandir desnecessariamente até a largura de uma página analítica

### Requirement: Escala compartilhada permanece consistente
O sistema SHALL aplicar uma hierarquia consistente para títulos, seções, indicadores financeiros, labels, metadados, cards, inputs, botões, tabelas e dialogs. Valores financeiros importantes SHALL ter destaque visual maior que seus rótulos, sem aumento indiscriminado de todos os elementos.

#### Scenario: Comparação entre rotas equivalentes
- **WHEN** uma pessoa navega entre carteiras, ações, operações e cadastros
- **THEN** componentes equivalentes mantêm dimensões, espaçamento, foco e hierarquia reconhecíveis

### Requirement: Layout responde progressivamente
O sistema SHALL adaptar páginas, tabelas, gráficos, formulários, dialogs e ações para desktop grande, notebook, tablet e mobile. Em mobile, SHALL preservar legibilidade e área de toque sem overflow horizontal acidental ou redução excessiva do conteúdo.

#### Scenario: Tela estreita
- **WHEN** qualquer rota é exibida em uma viewport mobile
- **THEN** conteúdo, ações e estados permanecem acessíveis, organizados e legíveis em uma coluna ou estratégia responsiva equivalente

### Requirement: Comprar ação é uma ação primária descobrível
Quando comprar for relevante ao contexto, o sistema SHALL exibir a ação com o texto explícito `Comprar ação` ou `Comprar <ticker>`, sem depender apenas de ícone, sinal de mais ou menu oculto. O fluxo SHALL preservar carteira ou ativo de origem quando os contratos e o estado existente permitirem.

#### Scenario: Compra iniciada no Dashboard
- **WHEN** uma pessoa está no Dashboard
- **THEN** encontra uma ação primária visível para `Comprar ação` e pode iniciar o fluxo sem procurar em um menu genérico

#### Scenario: Compra iniciada em carteira ou ativo
- **WHEN** uma pessoa inicia uma compra a partir de uma carteira ou dos detalhes de um ativo
- **THEN** a tela de compra pré-seleciona o contexto de origem quando tecnicamente possível e mantém o CTA explícito

### Requirement: CTA de compra comunica estados e efeito
O fluxo de compra SHALL usar um CTA final inequívoco, como `Confirmar compra`, indicar processamento, bloquear submissão duplicada e comunicar sucesso ou erro sem depender exclusivamente de cor. Carteiras vazias SHALL oferecer uma ação clara para a primeira compra.

#### Scenario: Compra válida em processamento
- **WHEN** uma operação de compra válida é submetida
- **THEN** o CTA indica `Processando compra…` ou equivalente e não permite uma segunda submissão

#### Scenario: Carteira sem posições
- **WHEN** uma carteira não possui posições
- **THEN** o estado vazio explica o próximo passo e oferece `Comprar primeira ação` ou ação equivalente explícita
