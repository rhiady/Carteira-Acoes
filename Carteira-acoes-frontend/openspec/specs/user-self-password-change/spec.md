# Especificação: redefinição da própria senha

## Objetivo

Permitir que um usuário autenticado defina uma nova senha sem informar a senha
antiga e sem depender de fluxo de e-mail.

## Endpoint

```http
PUT /api/auth/senha
Authorization: Bearer <token>
Content-Type: application/json
```

Payload:

```json
{
  "password": "nova-senha"
}
```

O frontend não deve enviar `id`, `login` ou senha antiga. O backend identifica o
usuário pelo token JWT.

## Respostas

### Sucesso

```http
204 No Content
```

Após o sucesso, exibir confirmação e direcionar o usuário para a área
autenticada. O token atual continua sendo usado.

### Erros

- `400 Bad Request`: senha ausente, em branco ou com menos de 8 caracteres;
- `401 Unauthorized`: token ausente, inválido ou expirado;
- `403 Forbidden`: não esperado para esta rota autenticada, mas deve ser tratado
  como acesso negado sem apagar a sessão.

Formato de erro:

```json
{
  "timestamp": "2026-11-17T00:00:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Dados da requisição são inválidos.",
  "path": "/api/auth/senha"
}
```

## Comportamento da tela

- disponibilizar um campo para a nova senha;
- validar no frontend que a senha possui entre 8 e 72 caracteres;
- enviar a requisição com o token pelo interceptor HTTP existente;
- desabilitar o botão enquanto a requisição estiver em andamento;
- limpar o campo após sucesso;
- não armazenar nem exibir a senha digitada;
- em `401`, limpar a sessão e redirecionar para login;
- em `400`, exibir a mensagem de validação sem alterar a sessão.

## Serviço Angular

O serviço pode usar o `HttpClient` já existente, sem nova dependência:

```ts
redefineSenha(password: string) {
  return this.http.put<void>(apiConfig.endpoint('/auth/senha'), { password });
}
```

Não criar rota pública de redefinição e não permitir que o usuário escolha o ID
da conta alterada.
