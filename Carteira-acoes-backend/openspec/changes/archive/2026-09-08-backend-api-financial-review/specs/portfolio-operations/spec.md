## ADDED Requirements

### Requirement: Operações financeiras atômicas e determinísticas
Compra e Venda SHALL validar carteira, ação, saldo, quantidade e preço dentro de uma transação; falha em qualquer etapa SHALL reverter operação e posição. Cálculos SHALL usar precisão decimal adequada e respeitar os indicadores financeiros oficiais existentes.

#### Scenario: Venda acima do saldo
- **WHEN** uma venda excede a quantidade disponível
- **THEN** a API rejeita a operação e a quantidade, posição e histórico permanecem inalterados

#### Scenario: Compras sucessivas
- **WHEN** são registradas compras de 10 unidades a 30 e 10 a 40
- **THEN** a posição resulta em 20 unidades e preço médio 35 quando a regra vigente for média ponderada

