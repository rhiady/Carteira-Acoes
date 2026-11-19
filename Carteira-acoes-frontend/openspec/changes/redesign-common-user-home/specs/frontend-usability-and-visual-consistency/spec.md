## MODIFIED Requirements

### Requirement: Cada tela comunica contexto, prioridade e ação principal

O sistema SHALL tornar evidente onde o usuário está, qual informação é principal, quais ações estão disponíveis e quais informações são secundárias. Cada tela SHALL possuir no máximo uma ação primária visualmente dominante, mantendo Compra e Venda acessíveis quando aplicáveis. A home comum SHALL priorizar carteira em foco, valores autoritativos, resultado e gráficos antes de atalhos ou explicações genéricas; `Minha conta` SHALL priorizar identidade, segurança e sessão.

#### Scenario: Usuário abre uma carteira
- **WHEN** a pessoa acessa o detalhe de uma carteira
- **THEN** identifica o nome, os principais valores financeiros, a ação principal e a seção de posições sem competir com dezenas de cards equivalentes

#### Scenario: Usuário comum abre a home
- **WHEN** a pessoa acessa `/inicio`
- **THEN** identifica a carteira em foco, os principais valores, a ação primária e os gráficos disponíveis sem navegar para outra tela de resumo

#### Scenario: Usuário abre Minha conta
- **WHEN** a pessoa acessa `/minha-conta`
- **THEN** identifica seus dados, controles de segurança e encerramento de sessão sem índices decorativos ou ações administrativas

### Requirement: Layout usa espaço e densidade de forma consistente

O sistema SHALL usar uma escala coerente de espaçamento, containers adequados ao tipo de tela, tipografia hierárquica e agrupamentos que reduzam cards, bordas e caixas redundantes sem comprimir conteúdo financeiro. A home comum SHALL usar uma composição visual orientada pelos dados, com resumo e gráficos em regiões proporcionais à sua importância, evitando grades de cards idênticos e painéis de navegação que ocupem o primeiro viewport.

#### Scenario: Dashboard em desktop
- **WHEN** a home financeira é exibida em viewport amplo
- **THEN** carteira em foco, valor atual, resultado, ação principal e pelo menos uma visualização financeira ocupam a hierarquia antes de informações secundárias

#### Scenario: Home em mobile
- **WHEN** a home financeira é exibida em viewport estreito
- **THEN** selector, métricas, ações e gráficos reorganizam-se em fluxo vertical sem corte, sobreposição ou rolagem horizontal da página

### Requirement: Navegação e tabelas são escaneáveis

A navegação SHALL usar nomes claros, estado ativo identificável e estratégia equivalente em desktop/mobile. Tabelas financeiras SHALL alinhar identificadores à esquerda, números à direita, usar divisores discretos e oferecer hover/foco perceptíveis sem excesso de linhas. Para `ROLE_USER`, destinos financeiros e utilidades pessoais SHALL formar grupos distintos, e links equivalentes a `/usuario`, `/inicio` e `/dashboard` MUST NOT aparecer simultaneamente.

#### Scenario: Usuário navega no mobile
- **WHEN** a pessoa usa a navegação e consulta uma tela financeira em viewport estreito
- **THEN** encontra a rota ativa, ações principais, `Minha conta` e `Sair` sem overflow horizontal acidental ou controles sem nome

#### Scenario: Pessoa percorre a navegação comum
- **WHEN** a pessoa examina os destinos disponíveis
- **THEN** encontra uma única home financeira, os fluxos de investimento e uma área pessoal separada sem opções redundantes

