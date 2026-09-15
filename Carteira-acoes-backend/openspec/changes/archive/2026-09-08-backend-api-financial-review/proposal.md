## Why

Os contratos HTTP, regras financeiras e integrações do backend precisam ser comparados com a implementação real para evitar respostas incompatíveis, exposição de dados sensíveis, erros HTTP incorretos e divergências em posições, preço médio e lucro/prejuízo. A revisão consolida critérios verificáveis antes de qualquer correção e prioriza riscos de corrupção financeira e quebra de contrato.

## What Changes

- Mapear controllers, DTOs, validações, exceptions, handlers, paginação, ordenação e OpenAPI.
- Auditar usuários, corretoras, carteiras, ações, operações e o response de posições.
- Confirmar e testar identificação de ações em Compra, fluxos de Compra/Venda, saldo, atomicidade, concorrência, BigDecimal, arredondamento e resultados financeiros.
- Revisar entities, repositories, migrations Flyway, constraints, histórico de cotações, integrações externas, câmbio, BRL/USD e timestamps.
- Corrigir apenas inconsistências confirmadas no backend e adicionar testes para regras críticas.
- Executar testes, build e validação de inicialização/runtime quando aplicável.
- Documentar problemas fora do backend sem modificá-los.

## Capabilities

### New Capabilities

- `backend-api-contracts`: contratos HTTP, validações, erros, paginação, ordenação e documentação OpenAPI do backend.

### Modified Capabilities

- `portfolio-operations`: regras de Compra, Venda, posições, preço médio e lucro/prejuízo.
- `portfolio-catalog`: contratos de carteiras e ações, incluindo consultas e posições.
- `broker-validation`: criação, consultas, validações e duplicidade de corretoras.
- `currency-conversion`: cotação, conversão BRL/USD, câmbio e timestamps.
- `market-data`: cotações atuais e histórico, incluindo falhas de integração.
- `platform-foundation`: persistência, transações, configuração e qualidade do backend.

## Impact

Afeta exclusivamente código, testes e documentação OpenAPI do backend Spring, incluindo controllers, DTOs, services, repositories, entities, handlers, clients externos e migrations novas quando necessárias. Não altera frontend Angular, deploy, `docker-compose.yml`, Nginx ou infraestrutura compartilhada.
