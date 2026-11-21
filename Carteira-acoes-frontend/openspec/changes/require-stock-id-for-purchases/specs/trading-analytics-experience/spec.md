## MODIFIED Requirements

### Requirement: Resumo e validação impedem operações ambíguas
O sistema SHALL calcular `quantidade * preco` apenas para apresentação quando ambos forem válidos, sem exibir `NaN`, e SHALL validar campos obrigatórios, valores positivos e a seleção de uma ação cadastrada por `acaoId` antes da compra.

#### Scenario: Valor estimado
- **WHEN** quantidade é 10 e preço é 35
- **THEN** o resumo pode apresentar `R$ 350,00` como valor estimado, sem substituir valores retornados pela API

#### Scenario: Ação não selecionada
- **WHEN** a compra não informa `acaoId`
- **THEN** o envio é impedido até que uma ação cadastrada seja selecionada

#### Scenario: Referência de compra exclusiva
- **WHEN** a compra é enviada
- **THEN** ela identifica o ativo somente por `acaoId`, sem ticker ou mercado

#### Scenario: Identificadores conflitantes
- **WHEN** a pessoa preenche o formulário de compra
- **THEN** a interface oferece somente a seleção da ação cadastrada e não permite combinar `acaoId` com ticker ou mercado
