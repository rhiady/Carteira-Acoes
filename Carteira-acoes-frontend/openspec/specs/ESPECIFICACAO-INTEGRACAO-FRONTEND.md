# Especificação de integração do frontend

## 1. Dependências

O frontend Angular já possui as dependências necessárias para esta integração:

- `@angular/common` — `HttpClient`;
- `@angular/forms` — formulário de login;
- `@angular/router` — guardas de rota e redirecionamento;
- `rxjs` — observables dos serviços HTTP.

Não é necessário adicionar uma biblioteca para decodificar JWT. O frontend deve
usar o endpoint `/api/auth/me` para confirmar o usuário e suas permissões.

As dependências Maven do backend (`spring-security`, OAuth2 Resource Server,
JWT, BCrypt e validação) não devem ser replicadas no `package.json` do frontend.

## 2. URL da API

Use uma variável de ambiente/configuração para a URL base, sem fixá-la nos
serviços:

```ts
export const environment = {
  apiUrl: 'http://localhost:8080'
};
```

As rotas abaixo devem ser chamadas como `${apiUrl}/api/...`.

## 3. Login

### Requisição

`POST /api/auth/login`

Não exige token.

```json
{
  "login": "admin@carteira.local",
  "password": "senha-do-usuario"
}
```

Regras:

- `login` é obrigatório;
- `password` é obrigatória;
- o backend normaliza o login removendo espaços nas pontas e convertendo para
  minúsculas.

### Resposta `200 OK`

```json
{
  "token": "eyJ...",
  "tipo": "Bearer",
  "expiresIn": 3600
}
```

`expiresIn` está em segundos. Para chamadas autenticadas, enviar:

```http
Authorization: Bearer eyJ...
```

O token deve ser removido do armazenamento quando o usuário fizer logout ou
quando a API responder `401`.

## 4. Usuário autenticado

### Requisição

`GET /api/auth/me`

Exige `Authorization: Bearer <token>`.

### Resposta `200 OK`

```json
{
  "id": 1,
  "login": "admin@carteira.local",
  "authorities": ["ROLE_ADMIN"]
}
```

O campo `id` é o identificador oficial do usuário fornecido pelo backend. O
frontend não deve criar, inferir ou extrair esse valor do JWT.

Papéis possíveis:

- `ROLE_ADMIN` — acesso administrativo;
- `ROLE_USER` — acesso de usuário comum.

O frontend deve usar `authorities` para proteger a interface administrativa,
mas a autorização real continua sendo feita pelo backend.

## 5. Interceptor HTTP

Todas as requisições autenticadas devem receber o token automaticamente. Não
enviar o header para o login.

Exemplo de comportamento:

```ts
const token = authService.getToken();
const requestWithAuth = token
  ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
  : request;
```

Ao receber `401 UNAUTHORIZED`, limpar a sessão e redirecionar para `/login`.
Ao receber `403 FORBIDDEN`, exibir mensagem de acesso negado sem apagar a
sessão automaticamente.

## 6. Erros da API

As respostas de erro seguem este formato:

```json
{
  "timestamp": "2026-11-16T23:00:00Z",
  "status": 401,
  "error": "UNAUTHORIZED",
  "message": "Usuário não autenticado.",
  "path": "/api/auth/me"
}
```

Principais códigos:

| HTTP | `error` | Uso |
|---:|---|---|
| 400 | `VALIDATION_ERROR` | Campos inválidos ou ausentes |
| 401 | `UNAUTHORIZED` | Token ausente, inválido ou expirado |
| 401 | `CREDENCIAIS_INVALIDAS` | Login ou senha incorretos |
| 403 | `FORBIDDEN` | Usuário autenticado sem permissão |
| 409 | `DATA_INTEGRITY_VIOLATION` | Conflito/regra de integridade |

## 7. Rotas públicas e protegidas

### Públicas

- `POST /api/auth/login`;
- `GET /api/acoes/**`;
- `GET /api/corretoras/**`;
- documentação Swagger/OpenAPI.

### Protegidas para qualquer usuário autenticado

As demais rotas exigem o header Bearer, salvo as rotas administrativas
listadas abaixo.

### Exclusivas de administrador

- `POST /api/usuarios`;
- `PUT /api/usuarios/*/papel`;
- `POST /api/acoes`;
- `POST /api/acoes/atualizar-cotacoes`;
- `POST /api/acoes/*/cotacao`;
- `DELETE /api/acoes/**`;
- `POST /api/corretoras`;
- `DELETE /api/corretoras/**`;
- operações de gerenciamento de usuários, em `/api/usuarios/**`.

## 8. CORS e desenvolvimento local

O backend aceita por padrão os frontends locais:

- `http://localhost:4200`;
- `http://localhost:5173`;
- `http://localhost:3000`.

São permitidos os métodos `GET`, `POST`, `PUT`, `PATCH`, `DELETE` e `OPTIONS`,
com os headers `Content-Type` e `Authorization`.

## 9. Fluxo esperado no frontend

1. Exibir o formulário de login.
2. Enviar `POST /api/auth/login`.
3. Armazenar o `token` retornado e iniciar a sessão.
4. Chamar `GET /api/auth/me` para carregar `login` e `authorities`.
5. Adicionar o Bearer às demais requisições via interceptor.
6. Proteger rotas administrativas com `ROLE_ADMIN`.
7. Remover o token no logout, em `401` ou quando `expiresIn` for atingido.

Não existe endpoint de logout no backend; o logout é feito localmente pelo
frontend removendo o token e o estado do usuário.
