# user-self-password-change Specification

## Purpose

Permitir que a pessoa autenticada redefina a própria senha com segurança, sem informar a senha antiga ou depender de fluxo de e-mail.

## Requirements

### Requirement: Redefinição usa o contrato autenticado publicado

O sistema SHALL enviar `PUT /api/auth/senha` com Bearer e o payload `{ password }`. A requisição MUST NOT enviar id, login ou senha antiga; a identidade é determinada pelo token autenticado.

#### Scenario: Senha válida é redefinida

- **WHEN** a pessoa informa uma nova senha válida
- **THEN** a interface envia somente `{ password }`, confirma o sucesso, limpa o campo sensível e preserva a sessão autenticada

### Requirement: Validação e erros preservam a sessão quando aplicável

O sistema SHALL validar senha entre 8 e 72 caracteres, bloquear submissão duplicada e apresentar mensagens de validação seguras. Uma resposta `400` SHALL preservar a sessão; uma resposta `401` SHALL limpar a sessão e redirecionar para login; uma resposta `403` SHALL apresentar acesso negado sem apagar a sessão.

#### Scenario: Senha inválida

- **WHEN** a senha está ausente, em branco ou fora do tamanho aceito
- **THEN** a interface impede ou relata a falha sem expor detalhes técnicos e sem alterar a sessão

#### Scenario: Sessão expirada

- **WHEN** `PUT /api/auth/senha` retorna `401 Unauthorized`
- **THEN** a sessão é removida e a pessoa é direcionada para login

### Requirement: Senhas permanecem privadas

O sistema MUST tratar a nova senha como dado sensível: não deve armazená-la, exibi-la, registrá-la em logs ou permitir que a pessoa escolha outra conta para alterar.

#### Scenario: Formulário concluído

- **WHEN** a redefinição termina com sucesso ou a pessoa sai do formulário
- **THEN** a senha digitada não permanece em estado persistido nem é exibida pela interface
