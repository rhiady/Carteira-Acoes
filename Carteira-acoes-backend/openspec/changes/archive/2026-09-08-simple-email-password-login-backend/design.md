## Context

O backend já possui um domínio de usuário e deve reutilizar suas entidades, repositório, tratamento de erros e convenções de documentação/testes. A implementação precisa primeiro verificar se a senha está em hash; caso esteja, o login deve reutilizar o mecanismo existente. Caso esteja em texto puro, a introdução de um `PasswordEncoder` deve cobrir criação, atualização e validação sem ampliar o escopo de autenticação.

## Goals / Non-Goals

**Goals:**

- Separar o fluxo em controller, service e consulta de usuário pelo email, preservando abstrações existentes quando adequadas.
- Centralizar a decisão de credenciais inválidas para produzir resposta indistinguível entre email inexistente e senha incorreta.
- Usar DTOs de request/response para impedir serialização acidental de senha.
- Integrar validação, tratamento de erro, OpenAPI e testes automatizados.

**Non-Goals:**

- Criar sessão, token, filtro de autenticação global ou autorização.
- Alterar frontend, deploy ou endpoints não relacionados ao login.

## Decisions

- **Contrato dedicado de entrada e saída:** criar/reutilizar DTOs específicos de login e de usuário autenticado. Isso evita retornar diretamente a entidade, alternativa que poderia expor campos sensíveis.
- **Busca por email no repositório:** adicionar/reutilizar uma consulta por email na abstração de persistência já existente. O service deve converter ausência do usuário e senha não correspondente para a mesma falha externa.
- **Senha conforme armazenamento atual:** preservar o mecanismo de hash existente. Se não houver hash, adotar `PasswordEncoder` e atualizar os pontos de criação e alteração de senha para persistirem valores codificados; não fazer migração ampla além do necessário para o fluxo.
- **Erro HTTP padronizado:** seguir o mecanismo de exceções/erros já usado pelo backend para representar `400`, `401` e `500`, garantindo o código e a mensagem do contrato sem vazar detalhes internos.
- **OpenAPI no controller:** documentar request, resposta de sucesso e erros de validação/autenticação na mesma convenção já utilizada no projeto.

## Risks / Trade-offs

- [Senhas existentes em texto puro podem não ser compatíveis com um encoder imediatamente] → inspecionar o armazenamento antes da implementação e definir a transição mínima necessária nos pontos de criação/atualização.
- [Serialização acidental da entidade pode expor hash ou senha] → manter response dedicado e testar explicitamente que o corpo não contém senha nem hash.
- [Tratamento de exceção existente pode formatar erros de modo diferente do exemplo] → adaptar o contrato à estrutura global já adotada, preservando `401` e mensagem externa equivalente.

## Migration Plan

1. Implementar o endpoint e os testes sem alterar os consumidores atuais.
2. Se necessário, passar novos cadastros e atualizações de senha pelo encoder; manter o comportamento dos endpoints existentes.
3. Executar testes e build antes da entrega.
4. Rollback consiste em remover o endpoint/serviço e, caso encoder seja introduzido, reverter a configuração e a alteração de persistência conforme a estratégia de migração verificada durante a implementação.
