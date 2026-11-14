## Why

O login atual foi implementado com sessão HTTP, enquanto o contrato de autenticação esperado exige uma API stateless com JWT. Além disso, a implementação parcial de `UserDetailsService`, DTOs e recursos de login está incompatível e impede a compilação do projeto.

## What Changes

- **BREAKING**: substituir o login por sessão em `POST /auth/login` pelo contrato JWT em `POST /api/auth/login`.
- Alinhar `LoginRequest` ao contrato `username/password` e retornar `TokenResponse` sem expor a entidade `Usuario`.
- Integrar `UsuarioDetailsService` ao `UsuarioRepository`, ao `PasswordEncoder` e ao `AuthenticationManager`.
- Configurar `JwtProperties` tipado com validação de segredo, expiração e emissor.
- Configurar `JwtEncoder`, `JwtDecoder`, validação de issuer/expiração e conversão das authorities do token.
- Tornar a cadeia do Spring Security stateless, desabilitar CSRF para o transporte por `Authorization` e proteger as rotas por autenticação e papel.
- Emitir tokens assinados após autenticação válida, com `sub`, `iss`, `iat`, `exp` e authorities.
- Adicionar `GET /api/auth/me` para expor a identidade e permissões derivadas do token.
- Padronizar respostas `401` e `403` sem revelar detalhes internos.
- Atualizar testes e callers que dependem da sessão ou do contrato antigo de login.

## Capabilities

### New Capabilities

- `jwt-authentication`: autenticação stateless, emissão e validação de JWT e consulta da identidade autenticada.

### Modified Capabilities

- `user-authentication`: alterar o contrato de login, o mecanismo de autenticação e as respostas de autorização, preservando a validação de credenciais e a não exposição da senha.

## Impact

Afeta `LoginRequest`, `TokenResponse`, `AuthService`, resources de autenticação, `UsuarioDetailsService`, `UsuarioRepository`, `SecurityConfig`, propriedades da aplicação, beans criptográficos, conversor JWT, `CurrentUserService`, testes de integração e documentação OpenAPI. Clientes que mantêm sessão ou enviam `email/senha` precisarão usar `Authorization: Bearer <token>` e `username/password`.
