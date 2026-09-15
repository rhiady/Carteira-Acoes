## Why

O login já autentica e persiste uma identidade mínima, mas as telas ainda tratam usuários e carteiras como dados globais ou dependências selecionáveis. Isso permite uma experiência incoerente após a entrada e pode exibir dados fora do contexto da pessoa autenticada; a aplicação precisa transformar a identidade do login no contexto único de navegação, operações e analytics.

## What Changes

- Reutilizar o login existente e adicionar a entrada secundária `Criar conta`.
- Criar `/cadastro` com nome, email, senha e confirmação de senha usando o endpoint existente `POST /usuarios`.
- Tratar validação, confirmação de senha, loading, email duplicado, sucesso e erros sem persistir dados sensíveis.
- Reutilizar `AuthService.currentUser` como contexto da aplicação e apresentar discretamente o usuário autenticado no shell.
- Carregar carteiras por `GET /usuarios/{usuarioId}/carteiras`, removendo seletores redundantes de usuário.
- Contextualizar Dashboard, Carteiras, detalhes, criação de carteira, Compra, Venda, Operações e Analytics ao usuário atual.
- Limpar seleção de carteira, filtros, dados financeiros em memória e persistências relacionadas no logout e na troca de usuário.
- Respeitar respostas de ownership do backend, exibindo estado amigável para carteira inexistente ou não acessível.
- Preservar a direção Swiss / financial editorial e validar acessibilidade e responsividade.
- Não alterar backend, pasta `deploy`, endpoints, regras de segurança do servidor ou adicionar autenticação avançada.

## Capabilities

### New Capabilities

- `user-access-experience`: cadastro frontend, contexto autenticado, isolamento de dados por usuário, troca de sessão e limpeza de estado.

### Modified Capabilities

- `investment-api-integration`: incluir o contrato frontend de criação de usuário e consulta das carteiras do usuário autenticado.
- `investment-frontend-experience`: contextualizar as superfícies financeiras, shell e estados de acesso pela identidade autenticada.
- `creation-form-experience`: alterar criação de usuário e carteira para refletir o fluxo público de cadastro e a propriedade implícita do usuário autenticado.

## Impact

- Afeta `AuthService`, rotas, shell, models/services, telas de cadastro, carteiras, dashboard, operações e analytics, além de testes.
- Usa somente contratos existentes: `POST /usuarios`, `POST /auth/login`, `GET /usuarios/{id}/carteiras` e endpoints financeiros já publicados.
- Pode exigir que `CarteiraRequest` continue compatível com o backend enquanto a UI deixa de expor a escolha de proprietário; a implementação não deve inventar um endpoint alternativo.
- Nenhuma alteração será feita no backend ou em `deploy`.
