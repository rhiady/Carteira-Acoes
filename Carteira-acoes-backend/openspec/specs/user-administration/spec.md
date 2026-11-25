# user-administration Specification

## Purpose

Oferecer uma API administrativa segura para manter identidades, papéis e situação de usuários sem expor credenciais nem remover registros necessários para auditoria.

## Requirements

### Requirement: Prefixo uniforme da API
O sistema MUST expor todos os endpoints de negócio sob o prefixo `/api`, incluindo `/api/acoes`, `/api/carteiras`, `/api/corretoras`, `/api/operacoes` e `/api/usuarios`, mantendo autenticação sob `/api/auth`.

#### Scenario: Acesso por rota padronizada
- **WHEN** um cliente chama um endpoint de negócio usando o prefixo `/api`
- **THEN** o sistema MUST encaminhar a requisição ao resource correspondente

#### Scenario: Acesso por rota sem prefixo
- **WHEN** um cliente chama uma rota de negócio sem o prefixo `/api`
- **THEN** o sistema MUST rejeitar a rota e MUST NOT oferecer alias implícito

### Requirement: Autorização administrativa dos endpoints de usuário
O sistema MUST expor a administração de usuários em `/api/usuarios` e MUST
exigir `ROLE_ADMIN` para todos os endpoints dessa classe, sem exceções para
criação, leitura ou alteração. Usuários com `ROLE_USER` MUST NOT conseguir
listar, consultar, cadastrar ou alterar qualquer usuário por esses endpoints.

#### Scenario: Administrador acessa a API
- **WHEN** uma requisição para `/api/usuarios` ou `/api/usuarios/{id}` contém um token com `ROLE_ADMIN`
- **THEN** o sistema MUST processar a operação solicitada

#### Scenario: Usuário sem papel administrativo acessa a API
- **WHEN** uma requisição para qualquer endpoint de `/api/usuarios` não contém `ROLE_ADMIN`
- **THEN** o sistema MUST responder `403 Forbidden` e MUST NOT executar a operação

#### Scenario: Usuário comum tenta listar usuários
- **WHEN** uma requisição `GET /api/usuarios` contém um token sem `ROLE_ADMIN`
- **THEN** o sistema MUST responder `403 Forbidden` e MUST NOT retornar dados de usuários

#### Scenario: Usuário comum tenta consultar outro usuário
- **WHEN** uma requisição `GET /api/usuarios/{id}` contém um token sem `ROLE_ADMIN`
- **THEN** o sistema MUST responder `403 Forbidden` e MUST NOT revelar se o usuário solicitado existe

#### Scenario: Usuário comum tenta alterar ou cadastrar usuário
- **WHEN** uma requisição para qualquer endpoint de criação ou alteração em `/api/usuarios` não contém `ROLE_ADMIN`
- **THEN** o sistema MUST responder `403 Forbidden` e MUST NOT executar a operação

### Requirement: Operações administrativas de usuários
O sistema MUST implementar `GET /api/usuarios`, `GET /api/usuarios/{id}`, `POST /api/usuarios`, `PUT /api/usuarios/{id}`, `PUT /api/usuarios/{id}/papel` e `PUT /api/usuarios/{id}/senha`, e MUST NOT oferecer DELETE para identidades.

#### Scenario: Administrador lista e consulta usuários
- **WHEN** o administrador chama os endpoints GET válidos
- **THEN** o sistema MUST retornar os usuários em `200 OK`, sem senha ou hash de senha

#### Scenario: Administrador cadastra usuário
- **WHEN** o administrador envia username, senha e papel válidos para POST
- **THEN** o sistema MUST criar o usuário ativo e responder `201 Created` com a representação pública sem credenciais

#### Scenario: Tentativa de remover usuário
- **WHEN** o cliente chama DELETE em `/api/usuarios` ou `/api/usuarios/{id}`
- **THEN** o sistema MUST rejeitar a operação e MUST preservar o registro

### Requirement: Persistência segura da senha
O sistema MUST codificar a senha com `PasswordEncoder` antes de salvar um novo usuário ou uma nova senha, e MUST NOT persistir o valor recebido em texto puro.

#### Scenario: Criação codifica a senha antes do save
- **WHEN** um usuário é criado com uma senha válida
- **THEN** o valor persistido MUST ser um hash BCrypt diferente da senha informada

#### Scenario: Troca de senha gera novo hash
- **WHEN** o administrador define uma nova senha, inclusive quando reutiliza a mesma senha
- **THEN** o sistema MUST salvar um hash codificado e MUST NOT retornar a senha nem o hash na resposta

### Requirement: Alteração cadastral e desativação auditável
O update administrativo MUST alterar somente username e ativo, MUST validar a unicidade do username excluindo o próprio usuário, MUST preservar a senha e o papel, e MUST preservar o registro quando `ativo=false`.

#### Scenario: Administrador altera cadastro
- **WHEN** o administrador envia username e ativo válidos para PUT
- **THEN** o sistema MUST persistir username e ativo e responder `200 OK` sem alterar a senha ou o papel existentes

#### Scenario: Username duplicado
- **WHEN** o username enviado já pertence a outro usuário
- **THEN** o sistema MUST responder `409 Conflict` e MUST preservar os dados anteriores

#### Scenario: Usuário desativado
- **WHEN** o administrador atualiza um usuário com `ativo=false`
- **THEN** o sistema MUST manter o registro para auditoria e MUST impedir novos logins dessa identidade

### Requirement: Alteração de papel separada
O sistema MUST aceitar a alteração de `papel` somente em `PUT /api/usuarios/{id}/papel`, sem permitir que o update cadastral altere esse campo.

#### Scenario: Administrador altera papel
- **WHEN** um administrador envia um papel válido para o endpoint separado de papel
- **THEN** o sistema MUST persistir o novo papel e responder `200 OK` com a representação pública

#### Scenario: Update cadastral não altera papel
- **WHEN** o cliente envia dados cadastrais sem o endpoint de papel
- **THEN** o sistema MUST preservar o papel persistido

### Requirement: Resposta pública sem credenciais
As respostas administrativas MUST conter apenas os campos públicos do usuário e MUST NOT expor senha, hash, token ou qualquer segredo de autenticação.

#### Scenario: Resposta de qualquer operação de usuário
- **WHEN** uma operação administrativa retorna uma representação de usuário
- **THEN** o corpo MUST omitir senha e hash, inclusive quando a operação foi criação ou troca de senha

### Requirement: Política de complexidade para senhas administrativas
O sistema MUST aceitar uma senha em criação administrativa ou troca administrativa somente quando ela tiver no mínimo 8 caracteres, pelo menos uma letra maiúscula e pelo menos uma letra minúscula.

#### Scenario: Administrador informa senha válida
- **WHEN** um administrador cria um usuário ou troca sua senha usando uma senha de pelo menos 8 caracteres contendo letras maiúsculas e minúsculas
- **THEN** o sistema conclui a operação e armazena a senha de forma codificada

#### Scenario: Administrador informa senha inválida
- **WHEN** um administrador cria um usuário ou troca sua senha usando uma senha menor que 8 caracteres, sem letra maiúscula ou sem letra minúscula
- **THEN** o sistema responde HTTP 400 com erro de validação e não altera nem cria o usuário

#### Scenario: Login preserva compatibilidade
- **WHEN** um usuário tenta fazer login com uma senha já armazenada que não atende à nova política, mas informa a credencial correta
- **THEN** o sistema avalia a credencial normalmente e não rejeita o login apenas pela composição da senha
