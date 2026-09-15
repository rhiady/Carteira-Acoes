## 1. Contrato e contexto de autenticação

- [x] 1.1 Reconfirmar os contratos backend de `POST /usuarios`, `POST /auth/login`, `GET /usuarios/{id}/carteiras` e `POST /carteiras`, documentando status e mensagens relevantes.
- [x] 1.2 Inspecionar `AuthService`, storage e signals atuais e definir uma API única para identidade, mudança de usuário e limpeza de contexto financeiro.
- [x] 1.3 Criar/ajustar tipos de cadastro, contexto de usuário e respostas de erro sem incluir senha ou confirmação em modelos persistentes.
- [x] 1.4 Adicionar testes de contrato para garantir URL configurada, payload de cadastro sem `confirmarSenha` e identidade limitada a `id`, `nome`, `email`.

## 2. Cadastro e login

- [x] 2.1 Adicionar `Criar conta` ao login existente como ação secundária, preservando somente email, senha e `Entrar` no formulário principal.
- [x] 2.2 Criar rota lazy `/cadastro` e tela standalone com direção Swiss/financial editorial alinhada ao login.
- [x] 2.3 Implementar Reactive Form com nome, email, senha e confirmar senha, incluindo mensagens associadas e validação de igualdade.
- [x] 2.4 Integrar criação ao `POST /usuarios` enviando somente nome, email e senha.
- [x] 2.5 Implementar loading `Criando conta…`, bloqueio de submissão duplicada, email duplicado e fallback `Não foi possível criar sua conta. Tente novamente.`.
- [x] 2.6 Exibir sucesso e redirecionar para `/login` sem login automático; garantir que nenhum valor sensível seja persistido ou logado.
- [x] 2.7 Adicionar testes de cadastro válido, validações, confirmação divergente, payload e erros.

## 3. Carteiras contextualizadas

- [x] 3.1 Adicionar método user-scoped ao `CarteiraService` para `GET /usuarios/{usuarioId}/carteiras` usando paginação existente.
- [x] 3.2 Adaptar Carteiras para usar `AuthService.currentUser().id`, contextualizar vazio e remover carregamento global seguido de filtro local.
- [x] 3.3 Adaptar criação de carteira para não exibir seletor de usuário, mantendo corretora e o contrato compatível de `POST /carteiras`.
- [x] 3.4 Garantir que detalhe, posições e resumo respeitem erro de ownership/missing resource e ofereçam retorno a `/carteiras`.
- [x] 3.5 Adicionar testes de request por usuário, estado vazio, ausência do seletor e erro de ownership.

## 4. Operações e analytics por usuário

- [x] 4.1 Remover seletores redundantes de usuário nos fluxos financeiros e derivar carteiras disponíveis do contexto autenticado.
- [x] 4.2 Contextualizar Dashboard e Analytics para carregar somente carteiras próprias e limpar dados ao trocar a identidade.
- [x] 4.3 Contextualizar Compra e manter carteira de origem pré-selecionada quando iniciada por detalhe, sem listar carteiras de terceiros.
- [x] 4.4 Contextualizar Venda e Operações, garantindo que posições e históricos pertençam à carteira própria escolhida.
- [x] 4.5 Adicionar testes de isolamento para Dashboard, Compra, Venda, Operações e Analytics.

## 5. Shell, logout e troca de usuário

- [x] 5.1 Exibir nome/email do usuário autenticado discretamente no shell, mantendo `Sair` acessível.
- [x] 5.2 Criar/reutilizar contexto financeiro compartilhado para carteira selecionada, filtros e caches em memória.
- [x] 5.3 Fazer logout limpar usuário, contexto financeiro, storage relacionado e redirecionar para `/login`.
- [x] 5.4 Validar o cenário Ana → logout → João, garantindo que nenhuma carteira, posição, operação ou analytics de Ana permaneça.
- [x] 5.5 Tratar deep links de carteira inválidos/de terceiros com mensagem amigável e retorno às próprias carteiras.

## 6. Visual, acessibilidade e validação

- [x] 6.1 Aplicar a `frontend-design` skill à relação login/cadastro/shell, preservando Swiss, financial editorial, superfícies neutras, regras finas e hierarquia existente.
- [x] 6.2 Garantir labels, foco visível, teclado, submit por Enter, `aria-live`, mensagens associadas e controles de senha acessíveis.
- [x] 6.3 Validar login, cadastro e telas afetadas em desktop grande, notebook, tablet e mobile sem overflow.
- [x] 6.4 Executar AXE e corrigir problemas de contraste, foco e semântica.
  - Em 2026-09-08, AXE foi executado em Chrome headless sobre `/login`, `/cadastro`, `/inicio`, `/dashboard`, `/carteiras` e `/operacoes` renderizados.
  - A primeira rodada encontrou landmarks duplicados em `/cadastro` e conteúdo da toolbar fora de landmark; após a correção semântica, a segunda rodada retornou zero violações nas seis rotas.

## 7. Verificação final

- [x] 7.1 Executar testes unitários e de integração frontend, corrigindo regressões.
- [x] 7.2 Executar smoke test com backend existente para cadastro, login, carteiras próprias, erros de ownership e logout.
  - Em 2026-09-08, o frontend de desenvolvimento foi executado em `http://localhost:4200` com proxy para `http://localhost:8082` e rewrite `^/api` → ``.
  - `GET /api/usuarios?page=0&size=1&sort=id,desc` retornou `200 OK`; o log do backend registrou `GET "/usuarios?page=0&size=1&sort=id,desc"` e o mapeamento para `UsuarioResource#listar(Pageable)`.
  - `POST /api/auth/login` com credenciais fictícias retornou `401 CREDENCIAIS_INVALIDAS`; o log do backend registrou `POST "/auth/login"` e o mapeamento para `AuthController#login(LoginRequest, HttpServletRequest)`.
  - O fluxo Chrome headless criou uma conta pela UI, retornou ao Login, autenticou e chegou a `/inicio`; o storage continha somente `email`, `id` e `nome`.
  - A tela de Carteiras requisitou `/api/usuarios/6/carteiras?page=0&size=10&sort=nome,asc` e exibiu o estado vazio do novo usuário.
  - Um segundo usuário autenticado abriu o deep link de uma carteira pertencente ao primeiro; o backend retornou `404 CARTEIRA_NAO_ENCONTRADA` e a UI exibiu `Carteira não encontrada.`.
  - `Sair` redirecionou para `/login` e removeu `authenticatedUser`, `selectedWalletId` e `financialFilters` dos storages verificados.
- [x] 7.3 Executar build de produção e confirmar que backend e `deploy` não foram alterados.
- [x] 7.4 Fazer revisão final da troca entre usuários e registrar evidências de que senhas e confirmações não são armazenadas.
