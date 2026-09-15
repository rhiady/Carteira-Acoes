## 1. Inspeção do fluxo atual

- [x] 1.1 Inspecionar entidade, DTOs, controller, service e repository de usuário, identificando o campo de email e o formato atual da senha.
- [x] 1.2 Verificar o mecanismo existente de tratamento de validações/exceções e a convenção OpenAPI usada pelo backend.

## 2. Contrato e persistência de senha

- [x] 2.1 Criar ou reutilizar request de login com validações obrigatórias de email, formato de email e senha não vazia.
- [x] 2.2 Criar response seguro contendo apenas `id`, `nome` e `email`.
- [x] 2.3 Criar ou reutilizar busca de usuário por email.
- [x] 2.4 Se a senha estiver em texto puro, introduzir `PasswordEncoder` e ajustar criação/atualização de usuário para persistirem senha codificada; se já houver hash, reutilizar o encoder existente.

## 3. Autenticação

- [x] 3.1 Implementar/reutilizar `AuthService` para localizar o usuário, validar a senha e mapear email inexistente e senha incorreta para o mesmo erro externo.
- [x] 3.2 Implementar `AuthController` com `POST /auth/login`, respostas `200`, `400` e `401`, sem modificar a proteção dos endpoints atuais.
- [x] 3.3 Garantir que erros inesperados continuem resultando em `500` pelo mecanismo global existente e não revelem detalhes sensíveis.

## 4. OpenAPI e testes

- [x] 4.1 Documentar request, resposta `200`, erro `400` e erro `401` do endpoint no OpenAPI.
- [x] 4.2 Criar teste de login válido retornando `200` e dados básicos do usuário.
- [x] 4.3 Criar testes de email inexistente e senha incorreta retornando `401` com mensagem externa equivalente.
- [x] 4.4 Criar testes de email inválido e senha vazia/ausente retornando `400`.
- [x] 4.5 Criar teste que confirme que senha e hash não aparecem no response de sucesso ou erro.

## 5. Verificação final

- [x] 5.1 Executar a suíte de testes do backend e corrigir falhas relacionadas à mudança. Os testes da autenticação passaram; a suíte completa apresentou falhas preexistentes de auto-attach do Mockito/Byte Buddy em outro teste.
- [x] 5.2 Executar o build do backend e confirmar que nenhuma alteração foi feita no frontend ou deploy.
