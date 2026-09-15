## Why

Compra e Venda ainda precisam comunicar melhor o contexto e a confirmação de uma operação, enquanto Analytics deve ajudar a interpretar valor, resultado, concentração e preço médio sem depender de cálculos mentais ou dados inventados. Esta change concentra as duas áreas de maior impacto para transformar a aplicação em uma experiência financeira mais útil e segura.

## What Changes

- Redesenhar Compra e Venda como fluxos transacionais equivalentes, com ativo, carteira, quantidade, preço, resumo estimado, validações, confirmação e prevenção de dupla submissão.
- Integrar `precoMedio`, cotação, quantidade e contexto da posição na Venda quando o backend fornecer esses dados.
- Criar uma área de Analytics com seleção de carteira, resumo financeiro, composição, resultados por ativo, comparação preço médio versus cotação, rankings condicionais e histórico real.
- Diferenciar realizado, não realizado e total, mantendo valores autoritativos do backend e moedas separadas.
- Reutilizar a direção Swiss/financial editorial, componentes compartilhados, estados acessíveis e responsividade mobile.
- Preservar integralmente os endpoints e payloads atuais, sem fabricar séries, câmbio, métricas autoritativas ou capacidades backend.

## Capabilities

### New Capabilities

- `trading-analytics-experience`: experiência integrada e segura para operações de compra/venda e análise financeira de carteiras.

### Modified Capabilities

Nenhuma. A nova capacidade coordena os contratos existentes sem substituir as specs consolidadas.

## Impact

- Features Angular de operações, carteiras, ações e dashboard/analytics.
- Models, services, mappers, formatters e componentes compartilhados de formulários, dialogs, gráficos e estados.
- Endpoints existentes de compra, venda, carteiras, posições, resumos e histórico de cotações.
- Testes unitários, build de produção, validação de acessibilidade e inspeção runtime desktop/mobile.
