## Why

O README atual contém apenas instruções mínimas de configuração e execução, dificultando a adoção local e o entendimento dos contratos da API. Uma documentação operacional completa reduz erros de ambiente e torna as integrações externas auditáveis.

## What Changes

- Documentar a arquitetura em camadas e os principais domínios da API.
- Descrever pré-requisitos, variáveis de ambiente, perfis `dev` e `test`, banco de dados e execução local.
- Catalogar os endpoints públicos, seus dados de entrada e os principais retornos e falhas.
- Explicar as integrações Brapi, Alpha Vantage e BrasilAPI, incluindo finalidade, URL base, credenciais, limites e comportamento diante de falhas.
- Incluir orientações para executar a suíte de testes e acessar a documentação OpenAPI.

## Capabilities

### New Capabilities

Nenhuma. Esta mudança é documental e não altera comportamento observável da API.

### Modified Capabilities

Nenhuma. Esta mudança é documental e não altera requisitos de produto.

## Impact

- Atualiza `README.md`.
- Não altera endpoints, modelo de dados, dependências ou contratos de integração.
