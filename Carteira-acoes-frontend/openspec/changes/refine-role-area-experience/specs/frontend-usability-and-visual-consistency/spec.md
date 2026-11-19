## MODIFIED Requirements

### Requirement: Cada tela comunica contexto, prioridade e ação principal

O sistema SHALL tornar evidente onde o usuário está, qual informação é principal, quais ações estão disponíveis e quais informações são secundárias. Cada tela SHALL possuir no máximo uma ação primária visualmente dominante, mantendo Compra e Venda acessíveis quando aplicáveis. As superfícies de entrada de usuário comum e administrador SHALL explicitar o contexto de uso e a autoridade atual sem usar texto decorativo como substituto de informação.

#### Scenario: Usuário abre uma carteira
- **WHEN** a pessoa acessa o detalhe de uma carteira
- **THEN** identifica o nome, os principais valores financeiros, a ação principal e a seção de posições sem competir com dezenas de cards equivalentes

#### Scenario: Usuário comum abre sua área
- **WHEN** a pessoa autenticada com `ROLE_USER` acessa a entrada comum
- **THEN** identifica que está na área de seus investimentos, encontra a próxima ação relevante e não vê comandos administrativos

#### Scenario: Administrador abre sua área
- **WHEN** a pessoa autenticada com `ROLE_ADMIN` acessa a entrada administrativa
- **THEN** identifica que está na área de gestão, encontra a ação administrativa publicada mais relevante e não vê métricas financeiras inventadas

### Requirement: Layout usa espaço e densidade de forma consistente

O sistema SHALL usar uma escala coerente de espaçamento, containers adequados ao tipo de tela, tipografia hierárquica e agrupamentos que reduzam cards, bordas e caixas redundantes sem comprimir conteúdo financeiro. As áreas comum e administrativa SHALL priorizar conteúdo funcional real e compactar branding ou índices que não ajudem na decisão atual.

#### Scenario: Dashboard em desktop
- **WHEN** o dashboard é exibido em viewport amplo
- **THEN** patrimônio, resultado, capital investido, posições principais e ações rápidas ocupam a hierarquia visual antes de métricas secundárias

#### Scenario: Entrada de papel é exibida em desktop
- **WHEN** a pessoa abre a entrada comum ou administrativa em viewport amplo
- **THEN** o contexto, a ação primária e o conteúdo real ocupam a primeira leitura sem um painel decorativo dominar o espaço útil

### Requirement: Navegação e tabelas são escaneáveis

A navegação SHALL usar nomes claros, estado ativo identificável e estratégia equivalente em desktop/mobile. Tabelas financeiras SHALL alinhar identificadores à esquerda, números à direita, usar divisores discretos e oferecer hover/foco perceptíveis sem excesso de linhas. Navegações de papéis diferentes SHALL manter rótulos, ordem e visibilidade coerentes com a autoridade da pessoa.

#### Scenario: Usuário navega no mobile
- **WHEN** a pessoa usa a navegação e consulta uma tabela em viewport estreito
- **THEN** encontra a rota ativa, ações principais e dados essenciais sem overflow horizontal acidental ou controles sem nome

#### Scenario: Pessoa alterna entre rotas autorizadas
- **WHEN** a pessoa percorre a navegação da sua área
- **THEN** reconhece a rota ativa, não encontra links de outra autoridade e consegue voltar ou sair sem ficar presa em menu ou diálogo

### Requirement: Estados e dialogs são consistentes e orientam continuidade

Loading, empty, error, success, disabled, forms e dialogs SHALL preservar layout, explicar o próximo passo quando aplicável, manter foco e não parecerem superfícies desconectadas do tema ativo. Falhas de validação SHALL associar mensagens aos controles corretos, evitar anúncios duplicados e mover o foco para o primeiro erro acionável quando a submissão falhar.

#### Scenario: Coleção vazia
- **WHEN** não há carteiras, posições ou resultados
- **THEN** apresenta uma explicação acessível, dimensões proporcionais e a próxima ação relevante quando ela existir

#### Scenario: Formulário falha na validação
- **WHEN** a pessoa submete um formulário com campos inválidos
- **THEN** cada mensagem possui relação acessível com seu campo, o primeiro erro acionável recebe foco ou é indicado por resumo, e nenhuma mensagem é anunciada repetidamente sem necessidade

### Requirement: Não são criados dados ou métricas fictícias

Refinamentos visuais SHALL consumir apenas dados reais já disponíveis e SHALL manter intactas regras financeiras, Compra, Venda, cotações, posições, analytics e contratos. As áreas administrativas SHALL mostrar somente dados e estados publicados pelo backend ou estados explicitamente indisponíveis.

#### Scenario: Dados financeiros ausentes
- **WHEN** uma métrica autoritativa não está disponível
- **THEN** a interface mantém o estado indisponível sem preencher com números inventados

#### Scenario: Admin sem métrica publicada
- **WHEN** a entrada administrativa não recebe uma métrica de resumo pelo contrato existente
- **THEN** apresenta contexto e ações administrativas sem simular números, gráficos ou atividade recente
