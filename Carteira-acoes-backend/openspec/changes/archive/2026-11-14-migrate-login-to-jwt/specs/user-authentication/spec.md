## MODIFIED Requirements

### Requirement: Login por email e senha
O sistema MUST expor `POST /api/auth/login` aceitando JSON com `username` e `password`, localizar o usuário pela identidade configurada, validar a senha informada e retornar um token de acesso assinado.

#### Scenario: Login válido
- **WHEN** a requisição contém identidade cadastrada e senha correspondente
- **THEN** o sistema MUST responder `200 OK` com `token`, `tipo` igual a `Bearer` e `expiresIn`, sem expor a senha ou seu hash

### Requirement: Validação do request de login
O sistema MUST rejeitar requests sem `username`, sem `password` ou com senha vazia.

#### Scenario: Identidade ausente
- **WHEN** a requisição não contém `username` ou o valor está em branco
- **THEN** o sistema MUST responder `400 Bad Request`

#### Scenario: Email inválido
- **WHEN** a identidade enviada não está em formato válido de email
- **THEN** o sistema MUST responder `400 Bad Request`

#### Scenario: Senha vazia ou ausente
- **WHEN** a requisição não contém `password` ou contém senha vazia
- **THEN** o sistema MUST responder `400 Bad Request`

### Requirement: Tratamento uniforme de credenciais inválidas
O sistema MUST responder `401 Unauthorized` com mensagem externa equivalente a credenciais inválidas tanto para identidade inexistente quanto para senha incorreta, sem revelar qual caso ocorreu.

#### Scenario: Email inexistente
- **WHEN** a requisição contém identidade não cadastrada
- **THEN** o sistema MUST responder `401 Unauthorized` com erro de credenciais inválidas

#### Scenario: Senha incorreta
- **WHEN** a requisição contém identidade cadastrada e senha incorreta
- **THEN** o sistema MUST responder `401 Unauthorized` com a mesma mensagem externa usada para identidade inexistente

### Requirement: Resposta segura do usuário
O sistema MUST NOT incluir a senha, hash da senha ou qualquer segredo de autenticação na resposta de login.

#### Scenario: Response não expõe senha
- **WHEN** o login é concluído com sucesso
- **THEN** o corpo da resposta MUST conter somente o token e seus metadados públicos, sem campo ou valor de senha

### Requirement: Compatibilidade e escopo da autenticação
O sistema MUST manter os endpoints de negócio funcionando com o cabeçalho `Authorization: Bearer <token>` e MUST NOT depender de sessão HTTP para autenticar requisições.

#### Scenario: Endpoint protegido com token válido
- **WHEN** um endpoint protegido recebe um token JWT válido
- **THEN** o sistema MUST autenticar a requisição usando a identidade e as authorities do token

#### Scenario: Endpoint protegido sem token
- **WHEN** um endpoint protegido é chamado sem token ou com token inválido
- **THEN** o sistema MUST responder `401 Unauthorized` sem executar a operação

#### Scenario: Endpoints existentes
- **WHEN** um endpoint existente é chamado com um token válido e nas condições funcionais previamente suportadas
- **THEN** seu comportamento funcional MUST permanecer compatível com o comportamento anterior
