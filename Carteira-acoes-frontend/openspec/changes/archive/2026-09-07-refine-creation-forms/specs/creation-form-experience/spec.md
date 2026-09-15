## Purpose

Define uma experiência de criação clara e acessível para cadastros e operações de investimento, mantendo os contratos publicados pela aplicação.

## ADDED Requirements

### Requirement: Formulários de criação e consulta orientam a entrada de dados
O sistema SHALL apresentar os fluxos de criação de usuário e carteira, e a tela de consulta de ação, com título, propósito, rótulos visíveis, indicação de campos obrigatórios e uma única ação primária por contexto. Cada campo SHALL fornecer texto de ajuda quando seu formato ou relação com outro dado não for autoexplicativo. A tela de ações MUST oferecer consulta, seleção e atualização de cotação sem expor criação direta de ação.

#### Scenario: Usuário corrige um cadastro incompleto
- **WHEN** uma pessoa tenta enviar um formulário de criação com dados obrigatórios ausentes ou inválidos
- **THEN** o sistema impede o envio, mantém os valores informados e apresenta a correção necessária junto ao respectivo campo

#### Scenario: Carteira depende de registros existentes
- **WHEN** uma pessoa cria uma carteira
- **THEN** o sistema identifica o usuário e a corretora selecionados antes do envio e explica quando uma dependência obrigatória ainda não está disponível

#### Scenario: Pessoa consulta uma ação
- **WHEN** uma pessoa pesquisa ou seleciona uma ação existente
- **THEN** o sistema apresenta o ativo e torna a atualização de sua cotação uma ação contextual, sem apresentar formulário de criação direta

### Requirement: Operações apresentam contexto e confirmação compreensível
O sistema SHALL agrupar a criação de compra e venda em seções de ativo, ordem e custos opcionais. Ele MUST mostrar o ativo, quantidade disponível quando aplicável, e um valor estimado claramente identificado como não oficial antes da confirmação.

#### Scenario: Pessoa prepara uma compra
- **WHEN** uma pessoa informa ativo, quantidade e preço de uma compra
- **THEN** o sistema apresenta o valor estimado, separa custos opcionais em uma área expansível e informa que o valor retornado pela API é o oficial

#### Scenario: Pessoa confirma uma venda
- **WHEN** uma pessoa informa uma venda válida de uma posição existente
- **THEN** o sistema mostra o ativo, a quantidade disponível, o valor líquido estimado e solicita confirmação explícita antes de enviar a operação

### Requirement: Formulários permanecem operáveis em diferentes larguras e tecnologias assistivas
O sistema SHALL reorganizar os campos em uma coluna em telas estreitas e preservar grupos, rótulos, instruções, erros, controles e ações sem corte ou rolagem horizontal. Controles interativos MUST possuir foco visível, nome acessível e área de interação utilizável; estados de sucesso, carregamento e erro MUST ser compreensíveis sem depender exclusivamente de cor.

#### Scenario: Pessoa usa teclado em uma tela estreita
- **WHEN** uma pessoa navega por um formulário de criação com teclado em uma largura de 375 px
- **THEN** todos os campos e ações são alcançáveis em ordem lógica, o foco permanece visível e a ação primária não fica oculta ou sobreposta

#### Scenario: Envio falha
- **WHEN** uma solicitação de criação ou operação falha
- **THEN** o sistema mantém os valores não enviados, anuncia uma mensagem de erro contextual e permite que a pessoa corrija ou tente novamente
