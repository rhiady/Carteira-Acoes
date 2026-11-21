## MODIFIED Requirements

### Requirement: Operações apresentam contexto e confirmação compreensível
O sistema SHALL agrupar a criação de compra e venda em seções de ativo, ordem e custos opcionais, usando uma composição proporcional à complexidade da operação. Em desktop, os dados da operação e o resumo SHALL poder ocupar áreas distintas e legíveis; em mobile, SHALL reorganizar-se sem corte ou rolagem horizontal. Ele MUST mostrar o ativo, quantidade disponível quando aplicável, e um valor estimado claramente identificado como não oficial antes da confirmação. Na compra por ticker, o campo SHALL apresentar o texto digitado em maiúsculas durante a edição e manter a referência exclusiva por ID ou por ticker e mercado.

#### Scenario: Pessoa prepara uma compra
- **WHEN** uma pessoa informa ativo, quantidade e preço de uma compra
- **THEN** o sistema apresenta formulário e resumo com dimensões adequadas, o valor estimado claramente identificado e custos opcionais em uma área expansível

#### Scenario: Pessoa informa um ticker em minúsculas
- **WHEN** uma pessoa digita letras minúsculas no campo de ticker da compra
- **THEN** o campo passa a apresentar o ticker em maiúsculas sem remover o texto ou modificar o mercado selecionado

#### Scenario: Pessoa confirma uma venda
- **WHEN** uma pessoa informa uma venda válida de uma posição existente
- **THEN** o sistema mostra o ativo, a quantidade disponível, o valor líquido estimado, o contexto da posição e solicita confirmação explícita antes de enviar a operação

