# creation-form-experience Specification

## Purpose
Define uma experiência de criação clara e acessível para cadastros e operações de investimento, mantendo os contratos publicados pela aplicação.

## Requirements

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
- **THEN** o sistema mantém os valores não enviados, anuncia uma mensagem de erro contextual e permite que a pessoa corrija ou tente novamente

### Requirement: Registration and wallet creation respect implicit ownership
The registration form SHALL contain name, email, password and confirmation only. Wallet creation SHALL not allow choosing an arbitrary owner when the authenticated identity is known; it SHALL preserve required broker selection and use the current user's identity according to the published compatible backend contract.

#### Scenario: Authenticated person creates a wallet
- **WHEN** the person opens wallet creation while authenticated
- **THEN** the form does not offer a user selector and submits the current user's identity through the existing wallet contract

#### Scenario: Registration validation fails
- **WHEN** a required registration field is empty, email is invalid or passwords differ
- **THEN** submission is prevented and each correction is identified accessibly without logging sensitive values
