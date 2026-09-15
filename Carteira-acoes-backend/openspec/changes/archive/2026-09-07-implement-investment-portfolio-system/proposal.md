## Why

O projeto precisa de um backend consistente para registrar e acompanhar carteiras de ações brasileiras e americanas, preservando o histórico de operações e a posição atual de cada carteira. A especificação final já define regras de domínio, integrações e contratos que devem virar uma implementação verificável.

## What Changes

- Criar o domínio de usuários, carteiras, ações, posições e operações de compra e venda.
- Expor APIs paginadas para cadastro e consulta, com DTOs, validação e respostas de erro padronizadas; o cadastro de ação recebe ticker e mercado, e o de corretora recebe somente CNPJ.
- Persistir o modelo por migrações Flyway, com integridade, precisão financeira, UTC e controle otimista da posição.
- Integrar consultas de cotação e de dados cadastrais por APIs externas, isoladas por adapters e facades, importando nome e preço da ação e todos os dados disponíveis da corretora.
- Tornar compra e venda transacionais para que histórico e posição nunca sejam persistidos parcialmente.
- Configurar perfis de execução, variáveis de ambiente, documentação das integrações e testes automatizados.

## Capabilities

### New Capabilities
- `portfolio-catalog`: cadastro e consulta paginada de usuários, carteiras e ações de Brasil e EUA.
- `broker-validation`: cadastro de corretoras condicionado às validações de CNPJ, CEP e registro na CVM.
- `market-data`: consulta de cotações por provedores externos, normalização de falhas e atualização de cotações das ações.
- `portfolio-operations`: registro imutável e transacional de compras e vendas, com atualização consistente das posições.
- `platform-foundation`: migrações, configuração de ambiente, tratamento de erros, observabilidade segura e documentação operacional.

### Modified Capabilities

- Nenhuma; não há capacidades OpenSpec estruturadas existentes para alterar.

## Impact

- Afeta o backend Spring Boot completo: entidades JPA, DTOs, mappers, repositórios, serviços, resources, exceções, migrações e testes.
- Introduz endpoints REST para usuários, carteiras, corretoras, ações e operações.
- Adiciona dependências e configuração para PostgreSQL/MySQL, H2 em testes, Flyway e OpenFeign, além de variáveis para provedores externos.
