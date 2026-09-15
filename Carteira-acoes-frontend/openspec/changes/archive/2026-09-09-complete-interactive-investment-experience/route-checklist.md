# Route checklist

- [x] `/login` — autenticação
- [x] `/cadastro` — criação de conta
- [x] `/inicio` — visão inicial contextualizada
- [x] `/dashboard` — resumo financeiro
- [x] `/acoes` — lista de ações
- [x] `/acoes/:id` — detalhe de ação
- [x] `/usuarios` — lista de usuários
- [x] `/usuarios/novo` — criação de usuário
- [x] `/usuarios/:id` — detalhe de usuário
- [x] `/corretoras` — lista de corretoras
- [x] `/corretoras/nova` — criação de corretora
- [x] `/corretoras/buscar` — busca de corretora
- [x] `/corretoras/:id` — detalhe de corretora
- [x] `/carteiras` — lista de carteiras
- [x] `/carteiras/nova` — criação de carteira
- [x] `/carteiras/:id` — detalhe de carteira
- [x] `/carteiras/:id/comprar` — compra contextual
- [x] `/carteiras/:id/vender` — venda contextual
- [x] `/carteiras/:id/operacoes` — histórico da carteira
- [x] `/operacoes` — operações do usuário
- [x] `/operacoes/:id` — detalhe da operação
- [x] `**` — fallback para Dashboard

## Baseline

- Testes frontend: 70 passando.
- Build de produção: passando; warning de orçamento inicial pré-existente.
- Backend/deploy: não alterados.

## Runtime final

- 88 combinações de rota e viewport validadas em Chrome renderizado: 1440 × 1000, 1280 × 800, 768 × 1024 e 375 × 812, sem overflow de página ou conteúdo cortado após as correções.
- AXE executado nas 22 rotas/fallbacks em desktop amplo e mobile, além do dialog de confirmação de venda: nenhuma violação final.
- Fluxo de novo usuário e fluxo autenticado completo executados com fixtures compatíveis com os DTOs publicados, incluindo compra, venda parcial e atualização de posição/resultado.
- Backend local indisponível durante a validação (`127.0.0.1:8080` e proxy de desenvolvimento sem upstream); nenhuma verificação foi registrada como integração real com backend.
- Suíte final: 71 testes passando. Build de produção passando com o warning de orçamento inicial já conhecido.
