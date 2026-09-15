## Why

A aplicação já possui os fluxos fundamentais de autenticação, carteiras, ações e operações, mas a experiência ainda é desigual entre rotas e pouco orientada à decisão financeira. Esta change consolida a revisão da aplicação inteira para tornar navegação, leitura de métricas, compra, venda, análise e estados de interface mais claros sem inventar dados ou alterar contratos do backend.

## What Changes

- Auditar todas as rotas, componentes, serviços, modelos, dialogs e contratos existentes antes da implementação.
- Consolidar tokens visuais, containers, tipografia, espaçamento, tabelas, cards, formulários, feedbacks e responsividade sob a direção Swiss/financial editorial definida pela `frontend-design`.
- Refinar Login, Cadastro, shell, navegação, Dashboard, Carteiras, detalhe de carteira, posições, Ações, detalhe de ação, histórico, Compra, Venda, Operações, Analytics, Corretoras, Usuários e estados auxiliares.
- Melhorar hierarquia de métricas financeiras, distinguindo valores autoritativos, realizado, não realizado, resultado total, preço médio, preço médio de venda e métricas derivadas válidas.
- Tornar Compra e Venda mais descobríveis, contextuais, confirmáveis e resistentes a submissão duplicada, preservando carteira, ativo e posição quando o fluxo permitir.
- Melhorar empty states, loading, erros, feedback de sucesso, dialogs, tooltips, busca/filtros e estados responsivos.
- Validar rotas reais, fluxos de novo usuário, troca de usuário, compra, venda, analytics, acessibilidade, desktop, notebook, tablet e mobile.
- Não alterar backend, migrations, banco, Docker, Nginx ou `deploy/`.

## Capabilities

### New Capabilities

- `complete-interactive-investment-experience`: experiência integrada de navegação, interação, feedback e responsividade em todas as rotas do frontend.
- `financial-analysis-presentation`: apresentação segura e contextualizada de métricas financeiras reais e derivadas matematicamente válidas.
- `contextual-trading-experience`: fluxos interativos e contextuais de Compra e Venda, incluindo resumo, confirmação, loading e erros.

### Modified Capabilities

- `investment-frontend-experience`: ampliar os requisitos de consistência visual, navegação, estados, análises e cobertura de rotas existentes.
- `investment-api-integration`: explicitar que a refatoração deve preservar contratos, paginação, campos autoritativos e separação de moedas.
- `user-access-experience`: manter isolamento do usuário autenticado durante navegação, operações, logout e troca de identidade.

## Impact

- Afeta componentes em `src/app/features`, `src/app/shared/components`, estilos globais, shell, rotas, formatters, mappers e testes frontend.
- Pode adicionar componentes reutilizáveis de apresentação, estados, filtros ou confirmação, preservando a arquitetura Angular existente.
- Não cria endpoints, não altera payloads sem contrato existente e não modifica backend ou deploy.
- Requer auditoria dos models/services atuais e validação runtime com os dados disponíveis.
