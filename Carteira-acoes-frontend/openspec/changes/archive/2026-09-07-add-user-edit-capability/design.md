## Context

See `proposal.md` and `specs/user-edit-experience/spec.md`. O frontend já possui `UsuarioService`, modelos de resposta sem senha, listagem paginada e dialogs Angular Material para criação.

## Goals / Non-Goals

**Goals:**

- Reutilizar a arquitetura `core`, `features` e `shared` existente.
- Concentrar transporte HTTP no `UsuarioService`.
- Reutilizar o padrão de dialogs, tokens visuais e feedback da aplicação.
- Manter o payload de atualização mínimo e compatível com o backend.
- Atualizar a listagem sem refresh manual após `200 OK`.

**Non-Goals:**

- Não exibir, buscar, armazenar ou inferir a senha atual.
- Não editar ID, datas, permissões ou outros campos não publicados.
- Não adicionar PATCH, fallback de método HTTP ou endpoints novos.
- Não implementar regras autoritativas de validação no frontend.

## Decisions

### Modelo separado para atualização

Criar `UsuarioUpdateRequest` com `nome`, `email` e `senha?: string`. O modelo `Usuario` continuará representando apenas a resposta do backend. Um modelo separado deixa explícita a semântica de senha opcional e impede que respostas contenham senha.

### Dialog compartilhado

Adicionar o dialog de edição ao conjunto de componentes compartilhados de usuários. Ele receberá o usuário atual via `MAT_DIALOG_DATA`, iniciará nome/e-mail preenchidos e senha vazia, e usará `MatDialogRef` para retornar o `UsuarioResponse` atualizado.

### Normalização no limite de requisição

O dialog validará nome e e-mail localmente. Antes de chamar o serviço, fará `trim` nos campos textuais e omitirá `senha` quando vazia ou composta apenas por whitespace. O serviço enviará `PUT /usuarios/{id}` com JSON.

### Atualização otimista somente após resposta

A listagem substituirá o item pelo objeto retornado pelo backend após sucesso. Em erro, o dialog permanece aberto, preserva os valores digitados e apresenta `message` da API ou o fallback seguro.

### Tratamento específico de erros

O componente poderá reconhecer status/códigos publicados para 404 e 409, mas sempre priorizará a mensagem retornada. Não haverá retry com outro método para 405.

## Risks / Trade-offs

- [O backend pode rejeitar a alteração] → preservar o dialog aberto e os valores não enviados.
- [A resposta pode não conter todos os campos esperados] → usar o `UsuarioResponse` retornado como fonte da atualização e reportar erro de transporte conforme o interceptor.
- [Senha pode vazar por estado transitório] → manter campo não persistido, nunca logar o payload e limpar o formulário ao fechar com sucesso.

## Migration Plan

1. Adicionar modelo e método de serviço.
2. Implementar dialog e ação na listagem.
3. Adicionar testes de payload, validações, sucesso e erros.
4. Executar testes, build e revisão de acessibilidade.
5. Rollback consiste em remover a ação/dialog e o método PUT; nenhuma migração de dados é necessária.
