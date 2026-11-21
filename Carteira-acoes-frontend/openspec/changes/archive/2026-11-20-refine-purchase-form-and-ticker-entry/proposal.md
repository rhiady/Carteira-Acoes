## Why

A compra exige que a pessoa reconheça campos relacionados, escolha uma referência de ativo sem ambiguidade e revise o impacto financeiro antes da confirmação. A tela atual concentra esses elementos sem uma hierarquia transacional forte e deixa o ticker visualmente em minúsculas enquanto o mercado o reconhece em maiúsculas.

## What Changes

- Normalizar o texto digitado no campo de ticker para maiúsculas durante a entrada, preservando a validação e o envio existentes.
- Refatorar a tela de compra como um fluxo financeiro Swiss: identificação do ativo, ordem, custos opcionais, valor estimado e confirmação em sequência explícita.
- Melhorar a hierarquia, densidade, responsividade, foco e feedback sem criar dados financeiros, endpoints ou cálculos novos.
- Manter a referência exclusiva por ID ou ticker/mercado, o diálogo de confirmação e todos os contratos de compra atuais.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `creation-form-experience`: tornar a entrada por ticker normalizada e a compra mais clara, acessível e orientada pela ordem de decisão.
- `contextual-trading-experience`: reforçar a leitura contextual e a revisão da compra antes da confirmação, preservando dados e regras publicados.
- `frontend-usability-and-visual-consistency`: aplicar hierarquia e layout responsivo consistentes à tela de compra.

## Impact

- UI: `CompraPage`, seus testes e estilos de formulário estritamente necessários.
- API/backend: nenhum endpoint, payload, DTO ou cálculo financeiro novo.
- Dependências: nenhuma; reutiliza Angular Material, Reactive Forms, serviços, tokens e feedback existentes.
