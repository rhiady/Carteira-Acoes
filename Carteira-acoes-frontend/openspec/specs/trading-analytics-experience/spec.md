# trading-analytics-experience Specification

## Purpose

Definir uma experiência transacional e analítica que ajude a pessoa a operar compra e venda com segurança e interpretar carteiras usando exclusivamente dados financeiros reais e autoritativos.

## Requirements

### Requirement: Compra e venda orientam a operação
Compra e Venda SHALL compartilhar uma progressão clara de ativo, carteira/posição, dados da ordem, resumo estimado e confirmação. A Venda SHALL mostrar quantidade disponível, preço médio e cotação quando disponíveis.

#### Scenario: Venda contextualizada
- **WHEN** uma pessoa inicia a venda de uma posição com quantidade, preço médio e cotação
- **THEN** a tela apresenta esses dados junto ao ativo e permite informar apenas a quantidade e preço da operação

#### Scenario: Compra válida
- **WHEN** uma pessoa informa ativo, carteira, quantidade e preço válidos
- **THEN** a tela apresenta um resumo com valor estimado antes da confirmação

### Requirement: Resumo e validação impedem operações ambíguas
O sistema SHALL calcular `quantidade * preco` apenas para apresentação quando ambos forem válidos, sem exibir `NaN`, e SHALL validar campos obrigatórios, valores positivos e identificação exclusiva da compra.

#### Scenario: Valor estimado
- **WHEN** quantidade é 10 e preço é 35
- **THEN** o resumo pode apresentar `R$ 350,00` como valor estimado, sem substituir valores retornados pela API

#### Scenario: Identificadores conflitantes
- **WHEN** a compra informa `acaoId` e também ticker/mercado
- **THEN** o envio é impedido até que exatamente um modo suportado seja escolhido

### Requirement: Submissão possui estados seguros
Durante Compra ou Venda pendente, a ação principal SHALL indicar processamento e bloquear novas submissões. Falhas SHALL priorizar `message`, usar fallback seguro quando necessário e preservar valores preenchidos; sucesso SHALL fornecer feedback inequívoco.

#### Scenario: Duplo clique
- **WHEN** uma operação está em processamento e a pessoa tenta confirmar novamente
- **THEN** nenhuma segunda requisição é enviada

#### Scenario: Erro do backend
- **WHEN** a operação falha com ou sem uma mensagem utilizável
- **THEN** a interface exibe a mensagem disponível ou `Não foi possível concluir a operação.` e mantém o formulário corrigível

### Requirement: Analytics apresenta contexto financeiro real
Analytics SHALL permitir selecionar a carteira quando houver mais de uma, exibir valor, resultados total/realizado/não realizado e quantidade quando disponíveis, e oferecer comparação preço médio versus cotação, composição por valor atual e resultado por ativo sem fabricar métricas autoritativas.

#### Scenario: Carteira com posições
- **WHEN** a carteira possui posições completas
- **THEN** Analytics apresenta visualizações úteis de composição, resultado e preço médio versus cotação com alternativa textual

#### Scenario: Moedas distintas
- **WHEN** a carteira possui BRL e USD
- **THEN** Analytics mantém seções e subtotais separados e não soma valores sem conversão autoritativa

### Requirement: Histórico e rankings respeitam suficiência dos dados
Histórico SHALL usar somente pontos retornados pelo backend e MAY exibir uma linha `Preço médio` quando houver posição correspondente. Rankings, pesos e agregações globais SHALL ser produzidos apenas com conjunto completo de posições; caso contrário, a interface SHALL indicar indisponibilidade.

#### Scenario: Histórico real com preço médio
- **WHEN** existem pontos históricos e `precoMedio` da posição analisada
- **THEN** o gráfico exibe os pontos reais e uma referência horizontal identificada como `Preço médio`

#### Scenario: Página parcial
- **WHEN** a resposta paginada não representa todas as posições
- **THEN** a interface não apresenta ranking ou composição global como completa

### Requirement: Experiência é responsiva e acessível
Compra, Venda e Analytics SHALL funcionar em desktop e mobile, com foco visível, labels, contraste, estados vazios/loading/erro, contexto textual para gráficos, reduced motion e sem comunicação de ganho/perda apenas por cor.

#### Scenario: Tela estreita
- **WHEN** a pessoa usa largura mobile
- **THEN** formulário, resumo, ações, tabelas e gráficos permanecem acessíveis sem overflow horizontal acidental
