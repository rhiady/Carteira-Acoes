## 1. Autorização do backend

- [x] 1.1 Auditar a configuração de segurança e o resource de usuários para garantir que todos os endpoints de `/api/usuarios/**` exijam `ROLE_ADMIN`.
- [x] 1.2 Preservar `POST /api/auth/login` e `GET /api/auth/me` para usuários comuns, sem liberar listagem ou consulta administrativa.

## 2. Testes de autorização

- [x] 2.1 Adicionar teste de `ROLE_USER` tentando listar usuários e verificar `403 Forbidden` sem dados na resposta.
- [x] 2.2 Adicionar teste de `ROLE_USER` tentando consultar outro usuário por ID e verificar `403 Forbidden`.
- [x] 2.3 Adicionar testes de `ROLE_USER` tentando cadastrar e alterar usuários, incluindo senha e papel, verificando `403 Forbidden` e ausência de alteração persistida.
- [x] 2.4 Preservar ou adicionar teste de `ROLE_ADMIN` executando as operações administrativas permitidas.

## 3. Proteção da interface

- [x] 3.1 Ocultar menu e rotas de cadastro/listagem/edição de usuários para sessões sem `ROLE_ADMIN`, usando as authorities retornadas por `/api/auth/me`.
- [x] 3.2 Tratar `403 Forbidden` no frontend sem expor dados administrativos e sem apagar uma sessão que continua autenticada.

## 4. Verificação

- [x] 4.1 Executar os testes de segurança e de administração de usuários.
- [x] 4.2 Validar que o frontend continua acessando login e `/api/auth/me` como usuário comum.
