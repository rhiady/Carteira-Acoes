## Why

Usuários podem ser cadastrados e consultados, mas ainda não podem corrigir nome, e-mail ou alterar opcionalmente a senha pela interface. A edição precisa respeitar o contrato `PUT /usuarios/{id}` e preservar a privacidade da senha.

## What Changes

- Adicionar modelo de atualização de usuário com senha opcional.
- Implementar `PUT /usuarios/{id}` no serviço Angular de usuários.
- Disponibilizar ação contextual `Editar usuário` na área de Usuários.
- Criar dialog responsivo com Reactive Forms, validação, loading, foco e feedback.
- Normalizar senha vazia removendo-a do payload.
- Atualizar a listagem com a resposta `UsuarioResponse` após sucesso.
- Tratar erros de validação, usuário inexistente, e-mail duplicado, método não permitido e falha inesperada.
- Manter senha fora de modelos de resposta, UI, logs e estado persistente.

## Capabilities

### New Capabilities

- `user-edit-experience`: edição segura e acessível de usuários existentes.

### Modified Capabilities

_Nenhuma._

## Impact

Afeta `UsuarioService`, modelos TypeScript, a listagem de usuários, dialogs compartilhados e testes unitários. Usa o endpoint existente do backend e não altera autenticação, permissões ou contratos de resposta.
