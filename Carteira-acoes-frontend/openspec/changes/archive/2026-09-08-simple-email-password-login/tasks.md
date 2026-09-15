## 1. Contrato e estado de autenticação

- [x] 1.1 Confirmar no contrato OpenAPI/backend o shape de sucesso de `POST /auth/login` e ajustar os DTOs frontend sem inventar campos.
- [x] 1.2 Criar os tipos de request/response e o `AuthService` singleton usando `apiConfig.endpoint('/auth/login')`.
- [x] 1.3 Implementar signal de usuário atual, restauração/validação de `authenticatedUser`, persistência somente de `id`, `nome` e `email`, e logout sem armazenar senha ou segredo.
- [x] 1.4 Adicionar testes unitários do serviço para sucesso, 401, erro inesperado, storage e garantia de que a senha não é persistida.

## 2. Roteamento e fluxo protegido

- [x] 2.1 Criar guard funcional baseado no estado local e definir a política de rotas públicas/protegidas sem alterar endpoints.
- [x] 2.2 Adicionar a rota lazy `/login`, preservar `/inicio` como destino após sucesso e redirecionar logout/anônimo para `/login`.
- [x] 2.3 Cobrir guard e redirecionamentos com testes de roteamento, incluindo usuário armazenado inválido ou ausente.

## 3. Tela de login

- [x] 3.1 Criar o componente de feature de login standalone, mantendo a estrutura `src/app/features` e reutilizando Angular Material/tokens existentes.
- [x] 3.2 Implementar Reactive Form com apenas email e senha, validações obrigatórias/email válido, labels, placeholders e mensagens associadas.
- [x] 3.3 Implementar senha oculta por padrão e controle acessível de mostrar/ocultar sem perder o valor.
- [x] 3.4 Implementar submit por Enter, loading `Entrando…`, desabilitação do CTA e prevenção de submissão duplicada.
- [x] 3.5 Implementar mensagens `Email ou senha inválidos.` e `Não foi possível entrar. Tente novamente.`, preservando o email digitado.
- [x] 3.6 Implementar redirecionamento após sucesso e ação `Sair` nas superfícies autenticadas existentes, reutilizando o serviço sem criar state management paralelo.

## 4. Direção visual e acessibilidade

- [x] 4.1 Aplicar o anchor Swiss/financial editorial definido no design: superfície neutra, sans única, regras finas, acento vermelho controlado e gesto editorial de grade.
- [x] 4.2 Garantir foco visível, nomes acessíveis, `aria-live` para estados, contraste WCAG AA e operação completa por teclado.
- [x] 4.3 Validar layout em desktop, notebook, tablet e mobile, sem overflow e com áreas de toque confortáveis.

## 5. Validação final

- [x] 5.1 Executar testes unitários e corrigir falhas de tipagem, contrato ou regressões de rotas.
- [x] 5.2 Executar build Angular de produção e confirmar que backend e deploy não foram alterados.
- [x] 5.3 Fazer revisão manual do fluxo login válido, 401, erro inesperado, refresh, logout, deep link protegido e senha não persistida.
- [x] 5.4 Executar checagem de acessibilidade/AXE e registrar/corrigir problemas encontrados.
