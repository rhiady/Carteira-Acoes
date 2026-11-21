## Why

O administrador chega à área de gestão, mas não consegue concluir a consulta de usuários nem cadastrar/importar ações no catálogo global. Esse fluxo administrativo não é uma operação financeira e não deve exigir carteira, posição ou operação.

## What Changes

- Restaurar a listagem administrativa de usuários com paginação e estados de carregamento, vazio e erro.
- Disponibilizar para `ROLE_ADMIN` o cadastro/importação de uma ação global por `POST /api/acoes`, enviando somente ticker e mercado.
- Manter esse cadastro separado de carteira, posição e operação, e preservar a compra financeira por `acaoId` como fluxo distinto.
- Cobrir rotas, listagem de usuários, envio do contrato de ação global e falhas de API com testes focados.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `user-access-experience`: explicitar a listagem administrativa de usuários, mantendo-a fora da experiência de usuário comum.
- `investment-api-integration`: confirmar a listagem administrativa de usuários e o cadastro/importação de ações globais pelo contrato publicado.

## Impact

- Código afetado: rotas administrativas, páginas de usuários, catálogo de ações, serviço/modelo de ações e testes relacionados.
- API/backend: reutiliza `GET /api/usuarios` e `POST /api/acoes` com `{ ticker, mercado }`; não cria operação financeira nem presume endpoint novo.
- Segurança: `ROLE_USER` continua sem acesso a usuários e ao cadastro de ações globais; a autorização do backend permanece a fonte final.
- Dependências: nenhuma.
