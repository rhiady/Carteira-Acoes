## Why

A mesma tela de compra atende hoje tanto à manutenção administrativa quanto à decisão de compra de quem já possui uma carteira, deixando o fluxo do usuário comum excessivamente técnico. A lista de carteiras também distribui contexto financeiro e ações em cartões repetitivos, dificultando a leitura rápida das carteiras disponíveis.

## What Changes

- Diferenciar a compra por papel: administrador mantém uma compra completa, com escolha explícita de referência do ativo e mercado; usuário comum parte das posições da própria carteira em uma lista contextual.
- Na lista contextual do usuário comum, mostrar cada posição com sua cotação autoritativa como preço inicial editável, para informar quantidade e opcionalmente substituir o preço antes da confirmação.
- Preservar a compra por ticker/mercado quando o administrador precisar registrar um ativo que ainda não está selecionado, sem criar endpoint, regra financeira ou acesso a carteira de terceiros.
- Redesenhar a listagem de carteiras para destacar nome, valores por moeda, resultado, ativos e ação de continuidade, reduzindo identificação técnica e superfícies redundantes em desktop e mobile.
- Expor `Carteiras` na área administrativa para que o administrador alcance a compra completa de uma carteira própria, sem liberar seleção de carteira de terceiros.
- Manter loading, vazio, erro, confirmação, teclado, foco visível e compatibilidade com os fluxos de carteira existentes.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `contextual-trading-experience`: diferenciar os pontos de entrada e a composição de compra para administrador e usuário comum, mantendo a carteira contextual e o contrato publicado.
- `trading-analytics-experience`: definir a sugestão de preço de compra a partir da cotação autoritativa da posição e a substituição explícita desse preço pelo usuário.
- `investment-focused-experience`: tornar a lista de carteiras uma visão financeira mais escaneável, sem perder ações contextuais ou separar moedas.
- `frontend-usability-and-visual-consistency`: aprimorar hierarquia, densidade e comportamento responsivo da compra comum e da listagem de carteiras.

## Impact

- Affected UI: `AdminPage`, navegação administrativa, `CompraPage`, entrada de compra no detalhe de carteira, `CarteirasPage`, estilos de operações/carteiras e testes de formulário/listagem.
- Affected data flow: reutilização de `CarteiraService.positions`, `AcaoService` e `OperacaoService.buy`; `CompraRequest` continua exigindo `carteiraId` e exatamente uma referência de ativo.
- Authorization: administrador continua restrito à carteira da rota até que exista um contrato explícito para compra em carteira de terceiro; usuário comum vê somente posições e carteiras de sua identidade autenticada.
- Dependencies: nenhuma nova dependência, endpoint, DTO, cálculo financeiro local ou conversão de moeda.
