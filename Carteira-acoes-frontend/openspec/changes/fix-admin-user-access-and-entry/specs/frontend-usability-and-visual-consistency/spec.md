## MODIFIED Requirements

### Requirement: Cada tela comunica contexto, prioridade e ação principal

O sistema SHALL tornar evidente onde o usuário está, qual informação é principal, quais ações estão disponíveis e quais informações são secundárias. Cada tela SHALL possuir no máximo uma ação primária visualmente dominante, mantendo Compra e Venda acessíveis quando aplicáveis. Administração e Usuários SHALL ter títulos, propósitos e destinos distintos: a primeira orienta a navegação de gestão, e a segunda apresenta a gestão da coleção de usuários.

#### Scenario: Usuário abre uma carteira
- **WHEN** a pessoa acessa o detalhe de uma carteira
- **THEN** identifica o nome, os principais valores financeiros, a ação principal e a seção de posições sem competir com dezenas de cards equivalentes

#### Scenario: Administrador muda de contexto
- **WHEN** uma pessoa com `ROLE_ADMIN` alterna entre Administração e Usuários
- **THEN** identifica o destino ativo, o propósito da tela e a próxima ação sem encontrar conteúdo duplicado

### Requirement: Estados e dialogs são consistentes e orientam continuidade

Loading, empty, error, success, disabled, forms e dialogs SHALL preservar layout, explicar o próximo passo quando aplicável, manter foco e não parecerem superfícies desconectadas do tema ativo. A listagem administrativa de usuários SHALL manter o contexto da coleção em carregamento, vazio, falha ou acesso negado e oferecer retry somente quando a repetição da consulta for segura.

#### Scenario: Coleção vazia
- **WHEN** não há carteiras, posições ou resultados
- **THEN** a interface explica a situação e apresenta a próxima ação relevante quando ela existir

#### Scenario: Falha na listagem de usuários
- **WHEN** a consulta administrativa de usuários falha ou é negada
- **THEN** a tela mantém título e contexto, explica a condição em texto e não deixa registros anteriores parecerem atuais
