## Why

Uma carteira atualmente não registra onde seus ativos são mantidos, o que impede identificá-la por corretora no cadastro e nas respostas da API. O frontend em refatoração precisa dessa informação como parte obrigatória da carteira e vinculada ao catálogo de corretoras validadas pelo sistema.

## What Changes

- **BREAKING** Exigir o identificador de uma corretora ao criar uma carteira.
- Vincular cada carteira a exatamente uma corretora cadastrada e validada pelo sistema.
- Retornar o identificador da corretora nos dados de carteira.
- Permitir que uma corretora seja vinculada a diferentes carteiras.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `portfolio-catalog`: As carteiras passam a exigir e expor o vínculo com uma corretora validada.

## Impact

- Entidades, migração Flyway, DTOs, mapper e serviço de carteiras.
- Contrato de `POST /carteiras` e respostas de carteira.
- Testes do catálogo de carteiras.
