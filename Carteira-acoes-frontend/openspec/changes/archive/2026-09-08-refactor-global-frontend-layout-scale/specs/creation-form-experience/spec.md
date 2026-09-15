## MODIFIED Requirements

### Requirement: Operações apresentam contexto e confirmação compreensível
O sistema SHALL agrupar a criação de compra e venda em seções de ativo, ordem e custos opcionais, usando uma composição proporcional à complexidade da operação. Em desktop, os dados da operação e o resumo SHALL poder ocupar áreas distintas e legíveis; em mobile, SHALL reorganizar-se sem corte ou rolagem horizontal. Ele MUST mostrar o ativo, quantidade disponível quando aplicável, e um valor estimado claramente identificado como não oficial antes da confirmação.

#### Scenario: Pessoa prepara uma compra
- **WHEN** uma pessoa informa ativo, quantidade e preço de uma compra
- **THEN** o sistema apresenta formulário e resumo com dimensões adequadas, o valor estimado claramente identificado e custos opcionais em uma área expansível

#### Scenario: Pessoa confirma uma venda
- **WHEN** uma pessoa informa uma venda válida de uma posição existente
- **THEN** o sistema mostra o ativo, a quantidade disponível, o valor líquido estimado, o contexto da posição e solicita confirmação explícita antes de enviar a operação

### Requirement: Formulários permanecem operáveis em diferentes larguras e tecnologias assistivas
O sistema SHALL reorganizar os campos em uma coluna em telas estreitas e preservar grupos, rótulos, instruções, erros, controles e ações sem corte ou rolagem horizontal. Controles interativos MUST possuir foco visível, nome acessível e área de interação utilizável; estados de sucesso, carregamento e erro MUST ser compreensíveis sem depender exclusivamente de cor.

#### Scenario: Pessoa usa teclado em uma tela estreita
- **WHEN** uma pessoa navega por um formulário de criação ou operação com teclado em uma largura de 375 px
- **THEN** todos os campos e ações são alcançáveis em ordem lógica, o foco permanece visível e a ação primária não fica oculta ou sobreposta

#### Scenario: Envio falha
- **WHEN** uma solicitação de criação ou operação falha
- **THEN** o sistema mantém os valores não enviados, anuncia uma mensagem de erro contextual e permite correção ou nova tentativa
