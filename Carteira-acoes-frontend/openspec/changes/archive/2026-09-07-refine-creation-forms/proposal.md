## Why

Os fluxos de criação de usuários, carteiras, ações e operações ainda apresentam campos e validações de maneira pouco estruturada, o que aumenta a chance de erro e dificulta o uso em telas pequenas. A aplicação já possui uma linguagem visual financeira; estas telas precisam aplicá-la de forma consistente, com orientação contextual e feedback acessível.

## What Changes

- Reorganizar os formulários de usuário, carteira e operação em etapas e grupos semânticos, com uma ação principal inequívoca.
- Melhorar a tela de ações para tornar consulta, seleção e atualização de cotação mais claras, sem expor criação direta.
- Exibir contexto e dependências de cada criação — como usuário e corretora da carteira, ou ativo e saldo da operação — antes do envio.
- Padronizar rótulos, textos de ajuda, validação inline, resumo de erros, estados de envio e confirmação de sucesso/erro.
- Tornar os formulários responsivos, preservando leitura, foco por teclado e alvos de interação adequados em larguras móveis e desktop.
- Aplicar os tokens visuais existentes aos formulários e distinguir ações de compra, venda e cadastros sem depender somente de cor.

## Capabilities

### New Capabilities

- `creation-form-experience`: experiência consistente, responsiva e acessível para os fluxos de criação de recursos e operações financeiras.

### Modified Capabilities

_Nenhuma._

## Impact

- Telas Angular de cadastro de usuários, carteiras e ações; formulários de compra e venda; componentes compartilhados de feedback e estilos globais.
- Nenhuma alteração em endpoints, DTOs, regras de negócio do backend ou novas dependências externas.
