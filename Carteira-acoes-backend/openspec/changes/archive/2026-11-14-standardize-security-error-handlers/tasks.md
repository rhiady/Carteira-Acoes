## 1. Handlers de segurança

- [x] 1.1 Migrar `RestAuthenticationEntryPoint` para produzir `ErroResponse` com código `UNAUTHORIZED`, status 401, mensagem segura e path.
- [x] 1.2 Migrar `RestAccessDeniedHandler` para produzir `ErroResponse` com código `FORBIDDEN`, status 403, mensagem segura e path.
- [x] 1.3 Verificar referências a `StandardError` e remover somente código legado sem uso após a migração.

## 2. Configuração e separação de responsabilidades

- [x] 2.1 Injetar os handlers separados na configuração de `exceptionHandling` da cadeia de segurança.
- [x] 2.2 Remover as lambdas de escrita inline de respostas 401/403 de `SecurityConfig`.
- [x] 2.3 Confirmar que `GlobalExceptionHandler` continua tratando exceções MVC sem capturar o fluxo específico dos filtros de segurança.

## 3. Testes e validação

- [x] 3.1 Adicionar testes unitários para os handlers, validando status, content type, código, mensagem segura e path.
- [x] 3.2 Adicionar ou ajustar testes de integração para token ausente/inválido retornando 401 e usuário autenticado sem permissão retornando 403.
- [x] 3.3 Executar a suíte de testes e validar os artefatos OpenSpec com `openspec validate --strict`.
