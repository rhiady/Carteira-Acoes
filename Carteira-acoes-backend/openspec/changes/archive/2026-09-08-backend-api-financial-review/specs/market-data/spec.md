## ADDED Requirements

### Requirement: Histórico e cotação reais
Atualizações de cotação SHALL persistir somente dados válidos retornados por provedores e o histórico SHALL ser temporalmente ordenável, sem duplicar ou fabricar pontos quando a ação não existir ou o provedor falhar.

#### Scenario: Falha de provedor
- **WHEN** o provedor retorna erro, body inválido ou timeout
- **THEN** a cotação válida anterior e seu histórico permanecem intactos

