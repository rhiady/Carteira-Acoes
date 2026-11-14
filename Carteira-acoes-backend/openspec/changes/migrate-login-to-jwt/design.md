## Context

O código atual possui login manual por email e senha, grava a autenticação em `HttpSession` e mantém `CurrentUserService` baseado no nome autenticado. Há uma implementação parcial de JWT, mas ela não compila: o DTO, o serviço e o resource usam contratos diferentes, e os beans de encoder/decoder não existem.

## Goals / Non-Goals

**Goals:**

- Usar autenticação stateless com JWT Bearer.
- Reutilizar BCrypt e o papel persistido para autenticação e autorização.
- Manter respostas JSON uniformes para `401` e `403`.
- Preservar o isolamento por usuário já aplicado pelos services.

**Non-Goals:**

- Refresh token, revogação, blacklist ou armazenamento de tokens.
- OAuth login, MFA, recuperação de senha ou confirmação de email.
- Alteração do domínio para suportar nomes de usuário separados de email, salvo se a implementação confirmar essa necessidade.

## Decisions

- **Identidade por email:** o banco possui `email`, não `username`. O campo `username` do contrato será tratado como a identidade de login e resolvido por email, evitando criar uma coluna duplicada.
- **`AuthenticationManager` como ponto de autenticação:** o login usará o provider padrão do Spring com `UsuarioDetailsService` e `PasswordEncoder`, em vez de comparar a senha manualmente no controller/service.
- **Subject igual ao email:** o JWT usará o email normalizado como `sub`, mantendo compatibilidade com `CurrentUserService`, que busca o usuário por email.
- **Authorities com prefixo consistente:** o token armazenará authorities no formato reconhecido pelo conversor; autorização administrativa usará `ROLE_ADMIN`.
- **SecretKey HMAC compartilhada:** encoder e decoder usarão a mesma chave derivada do segredo configurado e `HS256`, com validação padrão de expiração e issuer.
- **CSRF desabilitado somente para o modelo stateless:** como a credencial será enviada explicitamente no cabeçalho Authorization e não em cookie de sessão, CSRF global não será necessário. Se o transporte mudar para cookie, essa decisão deverá ser revista.
- **Contrato único de autenticação:** `/api/auth/login` será o endpoint JWT. O endpoint de sessão `/auth/login` e o resource duplicado serão removidos ou redirecionados somente se os callers forem atualizados no mesmo change.

## Risks / Trade-offs

- [Clientes atuais usam sessão e `email/senha`] → atualizar testes, documentação e callers para Bearer JWT e `username/password`.
- [Segredo ausente em perfil de teste] → fornecer configuração segura apenas para testes; nunca usar segredo fraco em ambiente real.
- [Logout não revoga JWT já emitido] → aceitar a limitação nesta etapa e não prometer revogação.
- [Authorities antigas podem não coincidir com o conversor JWT] → cobrir USER e ADMIN com testes de integração, incluindo `401` e `403`.

## Migration Plan

1. Corrigir o contrato de usuário/autenticação e registrar as propriedades JWT.
2. Criar encoder, decoder, converter de authorities e cadeia stateless.
3. Migrar o login e adicionar `/api/auth/me`.
4. Atualizar callers, testes, propriedades de teste e documentação.
5. Executar build, testes de autenticação/autorização e validação OpenSpec.
6. Rollback: restaurar o contrato de sessão somente junto com os callers compatíveis; não misturar os dois mecanismos na mesma rota.
