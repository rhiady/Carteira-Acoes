## ADDED Requirements

### Requirement: Erros de corretora coerentes
Falhas de duplicidade, validação cadastral, inexistência e indisponibilidade de provedor de corretora SHALL ser mapeadas a status HTTP coerentes e ao corpo padronizado de erro, sem virar HTTP 500 quando forem condições conhecidas.

#### Scenario: Corretora duplicada
- **WHEN** o cliente tenta cadastrar CNPJ já persistido
- **THEN** a API responde HTTP 409 e preserva o cadastro existente

