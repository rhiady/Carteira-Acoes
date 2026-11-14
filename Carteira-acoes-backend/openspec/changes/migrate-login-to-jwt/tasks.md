## 1. Contrato e carregamento do usuário

- [x] 1.1 Alinhar `LoginRequest` e `TokenResponse` ao contrato `/api/auth/login`, mantendo validação de campos obrigatórios.
- [x] 1.2 Corrigir `UsuarioRepository` e `UsuarioDetailsService` para autenticar pela identidade persistida (email), carregar senha hash e papel, e rejeitar usuário inativo quando esse estado existir.
- [x] 1.3 Configurar o `AuthenticationManager` para usar `UsuarioDetailsService` e o `PasswordEncoder` BCrypt, removendo a comparação manual duplicada de senha.

## 2. Configuração JWT

- [x] 2.1 Registrar `JwtProperties` e adicionar configurações seguras para perfis de desenvolvimento e teste sem colocar segredo real no repositório.
- [x] 2.2 Criar `SecretKey`, `JwtEncoder` e `JwtDecoder` HMAC com HS256, validação de expiração e issuer.
- [x] 2.3 Configurar o conversor de authorities para transformar as claims do JWT em authorities Spring Security com `ROLE_ADMIN` funcionando.

## 3. Cadeia de segurança e serviço de login

- [x] 3.1 Alterar `SecurityConfig` para sessão stateless, login público em `/api/auth/login`, rotas protegidas por padrão e permissões administrativas preservadas.
- [x] 3.2 Desabilitar CSRF somente no modelo Bearer stateless e manter CORS, respostas JSON de `401` e `403` e headers compatíveis.
- [x] 3.3 Implementar `AuthService.login` usando `AuthenticationManager` e emitir claims `iss`, `iat`, `exp`, `sub` e authorities somente após autenticação válida.
- [x] 3.4 Implementar `GET /api/auth/me` usando a autenticação criada pelo JWT e remover a duplicidade entre os resources de autenticação.
- [x] 3.5 Ajustar `CurrentUserService` para continuar resolvendo o usuário pelo subject do token sem confiar em identificadores enviados no request.

## 4. Migração de callers e testes

- [x] 4.1 Atualizar testes, fixtures e callers de `/auth/login` para `/api/auth/login`, `username/password` e `Authorization: Bearer`.
- [x] 4.2 Adicionar testes de integração para token válido, claims obrigatórias, token expirado, issuer divergente, token adulterado e credenciais inválidas.
- [x] 4.3 Adicionar testes de `401`, `403`, USER/ADMIN e `/api/auth/me`, garantindo que operações negadas não alterem dados.
- [x] 4.4 Validar configuração inválida de segredo, expiração e issuer e confirmar que a aplicação falha cedo.
- [x] 4.5 Executar `mvn test`, `openspec validate --change migrate-login-to-jwt` e revisar o diff final para não expor segredos.
