## MODIFIED Requirements

### Requirement: Cadastro e consulta de usuários
O sistema SHALL cadastrar usuários com nome, e-mail único e senha obrigatórios; a senha MUST NOT ser retornada por nenhuma API. O sistema SHALL expor criação, busca por identificador e listagem paginada de usuários. O sistema SHALL permitir a atualização integral de nome e e-mail de um usuário existente por `PUT /usuarios/{id}`, aceitando alteração opcional da senha, sem retornar ou persistir senha em texto puro. O e-mail informado na atualização MUST permanecer único entre os usuários e o sistema MUST rejeitar identificador inexistente ou dados inválidos com erro padronizado.

#### Scenario: Cadastro válido de usuário
- **WHEN** um cliente envia nome, e-mail válido não utilizado e senha
- **THEN** o sistema cria o usuário e responde sem expor a senha

#### Scenario: E-mail duplicado
- **WHEN** um cliente cadastra um e-mail já existente
- **THEN** o sistema responde com erro de conflito ou validação padronizado e não cria outro usuário

#### Scenario: Atualização válida de usuário
- **WHEN** um cliente envia `PUT /usuarios/{id}` com nome e e-mail válidos e, opcionalmente, uma nova senha
- **THEN** o sistema atualiza o usuário identificado e responde com os dados atuais sem expor a senha

#### Scenario: Atualização sem alterar senha
- **WHEN** um cliente atualiza nome e e-mail sem informar nova senha
- **THEN** o sistema preserva a senha existente e retorna o usuário atualizado sem expô-la

#### Scenario: E-mail já utilizado na atualização
- **WHEN** um cliente altera o e-mail para um endereço pertencente a outro usuário
- **THEN** o sistema responde com erro de conflito ou validação padronizado e não altera o usuário

#### Scenario: Usuário inexistente na atualização
- **WHEN** um cliente envia atualização para um identificador de usuário inexistente
- **THEN** o sistema responde com erro padronizado de recurso não encontrado

#### Scenario: Dados inválidos na atualização
- **WHEN** um cliente envia nome, e-mail ou senha que não atendem às validações do contrato
- **THEN** o sistema responde HTTP 400 com erro padronizado e não altera o usuário
