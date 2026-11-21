## MODIFIED Requirements

### Requirement: Compra e venda orientam a operação
Compra e Venda SHALL compartilhar uma progressão clara de ativo, carteira/posição, dados da ordem, resumo estimado e confirmação. A Venda SHALL mostrar quantidade disponível, preço médio e cotação quando disponíveis. Para uma compra contextual de usuário comum, a posição selecionada SHALL apresentar a cotação autoritativa como preço unitário inicial editável; a pessoa SHALL poder substituí-la antes da confirmação, e o valor enviado será o preço efetivamente preenchido.

#### Scenario: Venda contextualizada
- **WHEN** uma pessoa inicia a venda de uma posição com quantidade, preço médio e cotação
- **THEN** a tela apresenta esses dados junto ao ativo e permite informar apenas a quantidade e preço da operação

#### Scenario: Compra válida
- **WHEN** uma pessoa informa ativo, carteira, quantidade e preço válidos
- **THEN** a tela apresenta um resumo com valor estimado antes da confirmação

#### Scenario: Compra comum inicia com a cotação da posição
- **WHEN** uma pessoa comum escolhe uma posição que possui cotação atual
- **THEN** a ordem inicia com essa cotação no campo de preço unitário e a pessoa pode substituir o valor sem alterar a referência do ativo

#### Scenario: Cotação da posição indisponível
- **WHEN** uma posição escolhida não possui cotação atual
- **THEN** o preço unitário permanece disponível para preenchimento manual e a interface identifica que não há sugestão de cotação
