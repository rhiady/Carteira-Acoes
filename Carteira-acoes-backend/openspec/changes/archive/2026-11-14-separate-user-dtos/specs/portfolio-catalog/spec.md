## MODIFIED Requirements

### Requirement: Cadastro e consulta de usuários
O sistema SHALL cadastrar usuários por `POST /usuarios` usando contrato explícito com `login` e `password`, criando sempre `USER`, sem receber ou devolver a entidade de persistência. A senha MUST ser armazenada como hash e MUST NOT ser retornada por nenhuma API. O sistema SHALL expor busca por identificador e listagem paginada usando respostas contendo somente `id`, `login`, `papel` e `ativo`. O sistema SHALL permitir a atualização cadastral por `PUT /usuarios/{id}` usando somente `login` e `ativo`, sem aceitar senha ou papel nesse contrato. A alteração de senha SHALL ocorrer separadamente por `PUT /usuarios/{id}/senha`, usando somente `password`. A alteração de papel SHALL ocorrer separadamente por `PUT /usuarios/{id}/papel`, protegida para `ROLE_ADMIN`. O sistema MUST rejeitar dados inválidos ou identificadores inexistentes com erro padronizado.

#### Scenario: Cadastro válido sem expor persistência
- **WHEN** um cliente envia `POST /usuarios` com `login` e `password` válidos
- **THEN** o sistema cria um usuário `USER`, armazena somente o hash da senha e responde apenas com `id`, `login`, `papel` e `ativo`

#### Scenario: Cadastro válido de usuário
- **WHEN** um cliente envia nome de usuário, senha e papel válidos não utilizados
- **THEN** o sistema cria o usuário e responde sem expor a senha

#### Scenario: Senha ausente da resposta
- **WHEN** um cliente consulta ou cria um usuário
- **THEN** a resposta não contém `senha`, `password` nem o hash persistido

#### Scenario: Atualização cadastral não altera senha
- **WHEN** um cliente envia `PUT /usuarios/{id}` com `login` e `ativo`
- **THEN** o sistema atualiza somente os dados cadastrais e preserva a senha existente

#### Scenario: Alteração de senha isolada
- **WHEN** um cliente envia `PUT /usuarios/{id}/senha` com `password` válido
- **THEN** o sistema atualiza a senha de forma codificada sem alterar `login`, `papel` ou `ativo`

#### Scenario: Cliente tenta incluir senha ou papel na atualização cadastral
- **WHEN** um cliente envia senha no contrato de `PUT /usuarios/{id}`
- **THEN** o sistema não usa esses campos para alterar a credencial ou o papel

#### Scenario: E-mail duplicado
- **WHEN** um cliente cadastra um `login` já existente
- **THEN** o sistema responde com erro de conflito ou validação padronizado e não cria outro usuário

#### Scenario: Atualização válida de usuário
- **WHEN** um cliente envia `PUT /usuarios/{id}` com `login` e `ativo` válidos
- **THEN** o sistema atualiza o usuário identificado e responde sem expor a senha

#### Scenario: Atualização sem alterar senha
- **WHEN** um cliente atualiza dados cadastrais sem informar senha
- **THEN** o sistema preserva a senha existente e retorna o usuário atualizado sem expô-la

#### Scenario: E-mail já utilizado na atualização
- **WHEN** um cliente altera o `login` para um valor pertencente a outro usuário
- **THEN** o sistema responde com erro de conflito ou validação padronizado e não altera o usuário

#### Scenario: Usuário inexistente na atualização
- **WHEN** um cliente envia atualização para um identificador de usuário inexistente
- **THEN** o sistema responde com erro padronizado de recurso não encontrado

#### Scenario: Dados inválidos na atualização
- **WHEN** um cliente envia `login` ou `ativo` que não atendem às validações do contrato
- **THEN** o sistema responde HTTP 400 com erro padronizado e não altera o usuário

#### Scenario: Papel e estado inválidos
- **WHEN** um cliente não autorizado tenta alterar `papel` ou envia `ativo` inválido
- **THEN** o sistema responde HTTP 400 com erro padronizado e não altera o usuário

#### Scenario: Administrador promove usuário
- **WHEN** um administrador envia `PUT /usuarios/{id}/papel` com `papel=ADMIN`
- **THEN** o sistema altera o papel do usuário sem alterar sua senha ou login

#### Scenario: Usuário comum não promove usuário
- **WHEN** um usuário comum envia `PUT /usuarios/{id}/papel`
- **THEN** o sistema responde `403 Forbidden` e preserva o papel persistido
