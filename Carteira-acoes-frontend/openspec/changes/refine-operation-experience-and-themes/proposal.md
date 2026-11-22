## Why

O tema claro atual foi substituído por uma base escura e os tokens dos dois modos se sobrepõem, enquanto os fluxos de compra, venda e histórico concentram informação financeira em blocos pouco hierarquizados. Isso torna a leitura de uma ordem e a troca de tema menos previsíveis justamente nas telas em que decisões financeiras são tomadas.

## What Changes

- Restabelecer os temas claro e escuro como modos completos, persistentes e semanticamente equivalentes em toda a aplicação, inclusive sobreposições do Angular Material.
- Consolidar tokens de superfície, texto, borda, foco e ações para impedir que regras globais contradigam o tema ativo.
- Redesenhar compra e venda como fluxos de revisão progressiva: contexto do ativo, dados da ordem, custos opcionais e um resumo estimado claramente não autoritativo.
- Refinar histórico e detalhe de operação para priorizar tipo, ativo, valores autoritativos, custos, data e uma ação de inspeção inequívoca, sem alterar cálculos ou contratos existentes.
- Preservar o tema preferido e todos os dados/formulários enquanto a pessoa alterna entre os modos; manter foco, contraste WCAG AA, navegação por teclado e layout responsivo.

## Capabilities

### New Capabilities

_Nenhuma._

### Modified Capabilities

- `dark-theme-support`: ampliar o suporte de tema para claro e escuro em todas as superfícies, controles e overlays da aplicação.
- `contextual-trading-experience`: tornar os fluxos de compra e venda mais claros, revisáveis e coerentes visualmente sem modificar os pedidos à API.
- `trading-analytics-experience`: reorganizar a leitura do histórico e detalhe de operações sem alterar a origem ou o significado dos dados financeiros.
- `frontend-usability-and-visual-consistency`: estabelecer consistência de tema, contraste, foco, densidade e responsividade para as telas afetadas.

## Impact

- UI: tokens e estilos globais, `ThemeService`, alternador de tema, shell, páginas de compra, venda, histórico e detalhe de operação, além de componentes compartilhados de operação.
- Verificação: testes de tema, páginas de operação, estados do Material overlay, responsividade e AXE nos dois modos.
- API/backend: nenhum endpoint, payload ou cálculo financeiro é criado, modificado ou replicado no cliente.
- Dependências: nenhuma; reutilizar Angular Material, sinais, componentes, modelos e serviços existentes.
