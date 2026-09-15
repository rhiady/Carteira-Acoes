## Context

O recurso de usuários já possui criação, consulta por id e listagem paginada. O modelo usa senha armazenada de forma protegida e `UsuarioResponse` não deve expor esse campo.

## Goals / Non-Goals

**Goals:**

- Reutilizar o fluxo de validação e mapeamento existente para atualizar um usuário.
- Manter atualização transacional e impedir colisão de e-mail.
- Preservar a senha quando ela não for enviada e aplicar o mesmo mecanismo de proteção quando for alterada.

**Non-Goals:**

- Não adicionar autenticação/autorização de usuário nesta mudança.
- Não permitir alteração de identificador, carteiras ou outros relacionamentos.
- Não criar PATCH parcial com semântica diferente de `PUT`.

## Decisions

- **Endpoint `PUT /usuarios/{id}`:** mantém o padrão REST já usado pelo recurso e torna explícita a atualização integral dos campos editáveis.
- **DTO separado para atualização:** evita reutilizar o DTO de criação quando senha é opcional e permite validar o contrato de cada operação sem tornar o cadastro permissivo.
- **E-mail único validado no serviço/repositório:** o próprio usuário pode manter seu e-mail; somente colisões com outro id são rejeitadas.
- **Senha opcional:** campo ausente preserva o hash atual; senha presente é validada e processada pelo mesmo componente de hashing já adotado no cadastro.

## Risks / Trade-offs

- [Risco] Clientes podem enviar o DTO de criação esperando o mesmo comportamento → Mitigação: documentar claramente o DTO de atualização e cobrir o endpoint com testes de contrato.
- [Risco] Atualização de e-mail pode conflitar com restrição única do banco → Mitigação: validar previamente e manter tratamento padronizado de conflito na persistência.
