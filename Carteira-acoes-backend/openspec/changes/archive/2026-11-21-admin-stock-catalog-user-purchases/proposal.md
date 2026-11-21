## Why

Hoje uma compra pode informar ticker e mercado, permitindo que o fluxo de uma carteira também crie ações no catálogo global. Isso permite que um usuário comum introduza ativos sem curadoria administrativa e mistura a disponibilização de um ativo com uma operação financeira pessoal.

## What Changes

- Restringir a criação e importação de ações globais ao administrador, pelo fluxo de catálogo sem carteira, posição, quantidade ou operação financeira.
- Fazer a importação administrativa usar os dados e a cotação válidos do provedor para disponibilizar o ticker no catálogo.
- **BREAKING**: exigir que compras de `ROLE_USER` referenciem exclusivamente uma `acaoId` já existente; ticker e mercado não podem criar, localizar ou ser usados como referência alternativa nesse fluxo.
- Manter a compra do usuário comum em carteira própria, com quantidade obrigatória e preço unitário opcional: quando ausente, a cotação válida é usada; quando informado, o preço manual é preservado na operação.
- Preservar ownership das carteiras, a imutabilidade das operações e os cálculos financeiros atuais.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `portfolio-catalog`: tornar o catálogo de ações a única fronteira de criação/importação de ticker pelo administrador, sem operação financeira.
- `portfolio-operations`: limitar a compra de usuário comum a uma ação existente identificada por `acaoId`, mantendo preço manual ou cotação válida.
- `user-access-data-isolation`: explicitar que usuários comuns operam apenas ações previamente disponibilizadas no catálogo administrativo, sempre em carteiras próprias.

## Impact

- Backend: `AcaoResource`/`AcaoService`, `OperacaoResource`/`OperacaoService`, DTO de compra, autorização, validações e testes de integração.
- API: o fluxo de compra de usuário comum deixa de aceitar ticker/mercado como referência ou mecanismo implícito de criação de ação; `POST /acoes` permanece administrativo e cria somente catálogo.
- Frontend: a tela de compra de usuário comum deverá selecionar apenas ações existentes e sugerir a cotação, permitindo substituição manual do preço.
- Dados e dependências: não haverá carteira administrativa, posição administrativa, estoque nem nova dependência; ações e operações existentes são preservadas.
