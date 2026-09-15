## Why

O backend precisa oferecer uma entrada simples e consistente para usuários autenticarem suas credenciais por email e senha. Isso estabelece o contrato inicial de autenticação sem introduzir JWT, sessões ou mecanismos fora do escopo.

## What Changes

- Adicionar `POST /auth/login` com validação de email e senha.
- Localizar o usuário por email e validar a senha usando o armazenamento/encoder existente, ou introduzir `PasswordEncoder` se necessário.
- Retornar apenas `id`, `nome` e `email` em login válido.
- Retornar erro uniforme `401 Unauthorized` para email inexistente e senha incorreta.
- Documentar request e respostas `200`, `400` e `401` no OpenAPI.
- Criar testes de sucesso, validação, credenciais inválidas e ausência de senha no response.
- Manter os endpoints existentes funcionando e não alterar frontend ou deploy.
- Não adicionar JWT, refresh token, OAuth, roles, permissões, MFA, recuperação/confirmação de email ou autenticação global.

## Capabilities

### New Capabilities

- `user-authentication`: autenticação de usuário por email e senha, incluindo validação, resposta segura e tratamento uniforme de credenciais inválidas.

### Modified Capabilities

Nenhuma.

## Impact

Código de domínio de usuário, controller/service/repository de autenticação, DTOs, validação, tratamento de erros, encoder de senha caso necessário, testes e configuração/documentação OpenAPI. O banco e os endpoints atuais devem permanecer compatíveis.
