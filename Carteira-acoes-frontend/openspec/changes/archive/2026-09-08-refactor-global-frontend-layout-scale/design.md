## Context

O projeto é Angular standalone com Angular Material, organização em `core`, `features` e `shared`, e rotas carregadas por componente. A change existente `investment-frontend-experience` já define dados financeiros e direção Swiss; esta change atua na composição, escala e descoberta de compra sem alterar contratos ou criar nova arquitetura.

## Goals / Non-Goals

**Goals:**

- Aplicar um único sistema visual Swiss/financial editorial: superfícies claras, regras finas, tipografia sans, numerais tabulares, grid assimétrico e um acento controlado.
- Criar tokens compartilhados para container, padding, gaps, tipografia, controles, cards, tabelas, dialogs e breakpoints.
- Usar composição por tipo de conteúdo: analítica ampla, detalhe intermediário, formulário controlado e operação em formulário + resumo.
- Revisar runtime todas as rotas e estados, incluindo o fluxo completo de compra.

**Non-Goals:**

- Alterar endpoints, DTOs, regras financeiras, cálculos autoritativos ou contratos de compra/venda.
- Introduzir biblioteca de gráficos, nova arquitetura de features ou redesign de marca.
- Resolver com aumento global indiscriminado de fontes, padding ou largura.

## Decisions

1. **Tokens de layout no nível global.** Centralizar valores semânticos de largura, espaçamento, superfície, raio, controles e breakpoints em estilos compartilhados; páginas poderão escolher variantes de container. Isso reduz números aleatórios sem forçar uma largura única.

2. **Variantes de composição por densidade.** Dashboard, analytics, históricos e tabelas usarão container analítico amplo; detalhes usarão container intermediário; cadastros simples usarão container controlado; compra/venda usarão grid de operação e resumo que colapsa em mobile. Alternativa rejeitada: aplicar o mesmo container a todas as rotas.

3. **Componentes globais antes do refinamento de páginas.** Shell, cabeçalho, métricas, cards, tabelas, charts, estados e dialogs serão estabilizados antes da revisão tela a tela. Alternativa rejeitada: ajustes isolados por página que criariam divergência.

4. **Âncora frontend-design: Swiss.** A direção combina branco/neutro, sans única, hairlines, alinhamento à esquerda e numerais como elemento de composição; o diferenciador será uma faixa de contexto com régua de métricas alinhada ao grid. A decisão preserva a direção financeira existente e evita uma interface genérica baseada apenas em aumento de escala.

5. **Compra como ação contextual explícita.** Reutilizar rotas e estado existentes para expor `Comprar ação`, `Comprar <ticker>` e `Comprar primeira ação`, propagando parâmetros somente quando já suportados. Alternativa rejeitada: criar endpoint ou mecanismo global novo apenas para pré-seleção.

6. **Validação visual por matriz de rotas e viewports.** A validação deve percorrer as rotas reais em runtime em desktop grande, notebook, tablet e mobile, verificando dimensões, overflow, foco, dialogs, gráficos, tabelas e fluxo de compra. Build/testes e AXE complementam, mas não substituem a inspeção visual.

## Risks / Trade-offs

- [Muitas telas e estados] → manter inventário das rotas em `app.routes.ts` e checklist de cobertura por fase.
- [Backend indisponível durante a inspeção] → usar apenas fixtures/responses já existentes e registrar rotas não exercitáveis, sem fabricar dados.
- [Tabelas largas em mobile] → priorizar colunas, permitir composição responsiva/linhas empilhadas e preservar acesso aos dados.
- [Mudança visual regressiva] → validar tokens e componentes primeiro, executar testes/build e comparar cada rota em runtime.
- [Pré-seleção de compra não suportada] → preservar apenas o contexto disponível e manter seleção manual explícita.

## Migration Plan

1. Auditar rotas, componentes, estilos e estados atuais.
2. Consolidar tokens e ajustar shell/componentes compartilhados.
3. Refatorar páginas analíticas, carteiras, ações e históricos.
4. Refatorar compra, venda, operações e estados de compra contextual.
5. Refatorar cadastros, dialogs e demais rotas.
6. Validar quatro faixas de viewport, acessibilidade, testes e build.

O rollback consiste em reverter os commits da change; não há migração de dados nem alteração de API.
