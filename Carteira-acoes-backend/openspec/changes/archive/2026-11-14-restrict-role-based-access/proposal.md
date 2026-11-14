## Why

As rotas de catálogo atualmente permitem que usuários autenticados executem operações administrativas sobre ações e corretoras. A autorização precisa refletir o papel persistido no JWT, preservando para usuários comuns somente as operações de suas próprias carteiras, compras, vendas e vínculos.

## What Changes

- Restringir criação, alteração e exclusão de ações a usuários com `ROLE_ADMIN`.
- Restringir criação, alteração e exclusão de corretoras a usuários com `ROLE_ADMIN`.
- Manter consultas de catálogo públicas ou autenticadas conforme o contrato atual, sem ampliar permissões de escrita.
- Permitir que usuários comuns autenticados criem e mantenham suas próprias carteiras, vínculos de ações e operações de compra/venda, respeitando as verificações de ownership existentes.
- Rejeitar acesso de usuários comuns às operações administrativas com `403 Forbidden`, sem executar mutações.
- Garantir que usuários sem JWT recebam `401 Unauthorized` nas rotas protegidas.
- Cobrir as regras com testes de integração para `ADMIN`, `USER` e usuário anônimo.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `portfolio-catalog`: alterar autorização das operações de escrita de ações e corretoras para exigir `ROLE_ADMIN`.
- `user-access-data-isolation`: explicitar que usuários comuns podem operar somente suas próprias carteiras e dados financeiros.
- `jwt-authentication`: alinhar o uso das authorities do JWT e os contratos de `401`/`403` às regras de autorização por papel.

## Impact

Afeta `SecurityConfig`, resources de ações e corretoras, resources de carteira e operações, testes de integração, anotações OpenAPI e eventualmente regras de serviço quando a proteção atual não cobrir uma rota. Não altera o formato do JWT nem permite que o cliente escolha ou eleve o próprio papel.

Assumptions:

- Todas as mutações de catálogo (`POST`, `PUT`/`PATCH` e `DELETE`) são administrativas.
- Usuários comuns podem ler o catálogo conforme as permissões públicas atuais.
- “Vincular” significa criar ou atualizar a composição de uma carteira própria; a operação deve continuar sujeita às regras de ownership.
