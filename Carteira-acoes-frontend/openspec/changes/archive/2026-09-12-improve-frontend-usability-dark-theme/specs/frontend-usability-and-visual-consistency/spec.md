## Purpose

Tornar a aplicação financeira mais intuitiva, escaneável e consistente, com hierarquia visual clara, densidade confortável e adaptação refinada aos temas claro e escuro sem inventar dados.

## ADDED Requirements

### Requirement: Cada tela comunica contexto, prioridade e ação principal

O sistema SHALL tornar evidente onde o usuário está, qual informação é principal, quais ações estão disponíveis e quais informações são secundárias. Cada tela SHALL possuir no máximo uma ação primária visualmente dominante, mantendo Compra e Venda acessíveis quando aplicáveis.

#### Scenario: Usuário abre uma carteira
- **WHEN** a pessoa acessa o detalhe de uma carteira
- **THEN** identifica o nome, os principais valores financeiros, a ação principal e a seção de posições sem competir com dezenas de cards equivalentes

### Requirement: Layout usa espaço e densidade de forma consistente

O sistema SHALL usar uma escala coerente de espaçamento, containers adequados ao tipo de tela, tipografia hierárquica e agrupamentos que reduzam cards, bordas e caixas redundantes sem comprimir conteúdo financeiro.

#### Scenario: Dashboard em desktop
- **WHEN** o dashboard é exibido em viewport amplo
- **THEN** patrimônio, resultado, capital investido, posições principais e ações rápidas ocupam a hierarquia visual antes de métricas secundárias

### Requirement: Navegação e tabelas são escaneáveis

A navegação SHALL usar nomes claros, estado ativo identificável e estratégia equivalente em desktop/mobile. Tabelas financeiras SHALL alinhar identificadores à esquerda, números à direita, usar divisores discretos e oferecer hover/foco perceptíveis sem excesso de linhas.

#### Scenario: Usuário navega no mobile
- **WHEN** a pessoa usa a navegação e consulta uma tabela em viewport estreito
- **THEN** encontra a rota ativa, ações principais e dados essenciais sem overflow horizontal acidental ou controles sem nome

### Requirement: Estados e dialogs são consistentes e orientam continuidade

Loading, empty, error, success, disabled, forms e dialogs SHALL preservar layout, explicar o próximo passo quando aplicável, manter foco e não parecerem superfícies desconectadas do tema ativo.

#### Scenario: Coleção vazia
- **WHEN** não há carteiras, posições ou resultados
- **THEN** a interface explica a situação e apresenta a próxima ação relevante quando ela existir

### Requirement: Não são criados dados ou métricas fictícias

Refinamentos visuais SHALL consumir apenas dados reais já disponíveis e SHALL manter intactas regras financeiras, Compra, Venda, cotações, posições, analytics e contratos.

#### Scenario: Dados financeiros ausentes
- **WHEN** uma métrica autoritativa não está disponível
- **THEN** a interface mantém o estado indisponível sem preencher com números inventados
