## Context

O frontend já possui `/login`, `AuthService.currentUser`, guard, shell com logout, `UsuarioService`, `CarteiraService` e telas financeiras. O backend publica `POST /usuarios`, `POST /auth/login`, `GET /usuarios/{id}/carteiras` e o contrato atual de carteira ainda inclui `usuarioId`. A implementação deve permanecer em `src/app/core`, `src/app/features` e `src/app/shared`, sem alterar backend ou `deploy`.

## Goals / Non-Goals

**Goals:**

- Transformar a identidade autenticada em fonte única para cadastro, navegação e dados financeiros.
- Adicionar cadastro simples conectado a `POST /usuarios`, mantendo confirmação apenas no frontend.
- Reutilizar serviços, guard e componentes atuais, removendo escolhas redundantes de usuário.
- Invalidar contexto financeiro no logout/troca e tratar ownership com estados amigáveis.
- Preservar a direção visual Swiss / financial editorial da skill `frontend-design`: neutro/branco, sans única, hairlines, grid assimétrico, acento controlado e conteúdo sem decoração promocional.

**Non-Goals:**

- Alterar backend, deploy, contratos HTTP, autorização real, roles, permissões ou criar token.
- Criar uma segunda solução de autenticação ou um store global complexo.
- Adicionar login social, recuperação de senha, MFA, perfil complexo ou onboarding longo.

## Decisions

### Cadastro separado e mínimo

Criar uma página lazy `/cadastro` com Reactive Form tipado, confirmação de senha e método `UsuarioService.create`. O payload será montado explicitamente sem `confirmarSenha`; sucesso usa feedback e navegação para `/login`. A alternativa de reutilizar o dialog administrativo foi rejeitada porque o cadastro público tem outro contexto e precisa de uma rota compartilhável.

### Contexto de usuário no core

Reutilizar `AuthService.currentUser()` como fonte de identidade. Serviços e páginas dependentes receberão o id por chamada explícita ou método user-scoped, sem novo state management. A alternativa de carregar `/carteiras` e filtrar no componente é rejeitada porque aumenta exposição, tráfego e risco de estado cruzado.

### Carteira e operações contextualizadas

Carteiras serão carregadas por `/usuarios/{id}/carteiras`; telas de compra/venda/operações/analytics derivarão suas opções desse conjunto. Detalhes por id continuam respeitando a resposta do backend; falhas de ownership viram estado amigável com retorno às próprias carteiras. A criação de carteira removerá o seletor de usuário da UI, mas manterá compatibilidade com o request publicado até confirmar a forma exata de associação aceita pelo backend.

### Invalidação no logout e troca

O logout limpará o usuário e um contexto financeiro local compartilhado (carteira selecionada, filtros e caches em memória) antes da navegação. A troca de identidade terá uma chave de contexto derivada do id para impedir reuso de dados do usuário anterior. Não serão armazenadas senhas, confirmações ou segredos.

### Direção visual

A `frontend-design` será aplicada com o anchor Swiss já estabelecido. O diferenciador será uma linha editorial contínua entre login/cadastro e shell, com cabeçalho de identidade discreto e formulários na mesma régua tipográfica; `Criar conta` será secundário, enquanto `Entrar`/`Criar conta` permanecerão CTAs claros e sem linguagem temática.

### Testes e validação

Adicionar testes de cadastro, payload sem confirmação, erros duplicados, contexto de carteiras, troca de usuários, logout e rotas. Validar com backend existente em smoke test, AXE, screenshots em quatro larguras, testes unitários e build. Nenhum teste deve usar ou persistir senha em logs.

## Risks / Trade-offs

- **[Contrato de criação de carteira ainda exige usuarioId]** → manter o DTO compatível e confirmar no backend antes da implementação; não enviar um formato inventado.
- **[Dados antigos permanecem em signals de páginas já montadas]** → limpar contexto no logout e recarregar por user id na entrada de cada superfície.
- **[Endpoint user-scoped é paginado]** → centralizar paginação no service e evitar fallback global silencioso.
- **[Cadastro e login destoam visualmente]** → compartilhar tokens, estrutura editorial e controles acessíveis do login existente.
- **[Frontend não é segurança]** → tratar guard/isolamento como UX e manter o backend como autoridade de ownership.

## Migration Plan

1. Implementar cadastro e link secundário do login.
2. Centralizar user-scoped wallets e adaptar superfícies financeiras por fatias.
3. Implementar limpeza de contexto e estados de ownership.
4. Executar testes, build, AXE e smoke test contra backend sem modificar backend/deploy.
5. Rollback removendo a rota/cadastro e as adaptações de contexto; nenhum dado persistente sensível é criado.
