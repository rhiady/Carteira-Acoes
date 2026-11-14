## Why

O tratamento de falhas de autenticação e autorização está duplicado: `SecurityConfig` constrói respostas inline, enquanto as classes em `security` usam o contrato legado `StandardError`. Isso dificulta a manutenção e pode produzir formatos diferentes para o mesmo tipo de erro. A mudança mantém responsabilidades separadas, mas estabelece um único contrato HTTP para erros de segurança.

## What Changes

- Conectar `RestAuthenticationEntryPoint` ao fluxo de autenticação do Spring Security para responder falhas 401.
- Conectar `RestAccessDeniedHandler` ao fluxo de autorização para responder falhas 403.
- Padronizar ambos os handlers com `ErroResponse`, incluindo status, código, mensagem e path.
- Remover os handlers inline equivalentes de `SecurityConfig`, mantendo nela somente a configuração da cadeia de segurança.
- Manter `GlobalExceptionHandler` responsável pelas exceções lançadas pela aplicação e pelos endpoints MVC, sem duplicar o fluxo de filtros de segurança.
- Cobrir respostas 401 e 403, incluindo conteúdo JSON e ausência de stack trace ou detalhes internos.

## Capabilities

### New Capabilities

- `security-error-handling`: Define handlers separados e o contrato JSON para falhas de autenticação e autorização.

### Modified Capabilities

- `jwt-authentication`: Explicita o contrato de resposta para requisições sem autenticação e para usuários autenticados sem permissão.

## Impact

- `SecurityConfig` e as classes `security.RestAuthenticationEntryPoint` e `security.RestAccessDeniedHandler`.
- DTO `ErroResponse` e o tratamento global em `GlobalExceptionHandler`.
- Testes de integração de segurança e testes unitários dos handlers.
- Clientes da API que consomem o corpo das respostas 401/403; o formato será uniformizado no contrato `ErroResponse`.
