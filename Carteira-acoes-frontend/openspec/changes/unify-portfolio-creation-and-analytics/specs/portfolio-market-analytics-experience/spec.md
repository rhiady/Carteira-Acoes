## Purpose

Permitir que investidores e administradores leiam análises de carteira e operações por mercado, com interação útil, números autoritativos e separação explícita entre BRL e USD.

## ADDED Requirements

### Requirement: Analytics pode ser filtrado por mercado

O sistema SHALL oferecer um filtro acessível entre `Todos`, `Brasil` e `EUA` nas superfícies de gráficos de carteira e operações. A seleção SHALL limitar posições, operações, resumos visuais, legendas e textos ao mercado correspondente e SHALL manter a moeda retornada pela API.

#### Scenario: Pessoa seleciona mercado EUA
- **WHEN** uma carteira contém posições ou operações de Brasil e EUA e a pessoa seleciona `EUA`
- **THEN** os gráficos e resumos exibem somente registros de `mercado: EUA` e seus valores em USD, sem somar ou converter BRL

#### Scenario: Pessoa retorna para todos os mercados
- **WHEN** a pessoa seleciona `Todos`
- **THEN** a interface volta a apresentar os mercados disponíveis separadamente, sem consolidar moedas incompatíveis em um único número

### Requirement: Gráficos expõem interação e alternativa textual

Os gráficos SHALL oferecer tooltip com ticker, unidade monetária e valor formatado, foco/hover perceptível, legenda compreensível e resumo textual equivalente. A interação SHALL respeitar reduced motion e não poderá ser necessária para descobrir um valor essencial.

#### Scenario: Pessoa inspeciona um ponto
- **WHEN** a pessoa passa o mouse ou navega por teclado sobre uma barra, ponto ou segmento
- **THEN** recebe o ticker, a métrica, a moeda e o valor autoritativo formatado, além de uma alternativa textual no conteúdo da página

#### Scenario: Gráfico sem dados suficientes
- **WHEN** não existem registros para o filtro escolhido ou algum valor autoritativo necessário está ausente
- **THEN** a interface mostra um estado vazio/indisponível contextual e não cria barras, percentuais ou totais fictícios

### Requirement: Números financeiros respeitam o contrato publicado

O sistema SHALL apresentar valores e resultados usando os campos retornados pelos endpoints de resumo, posição e operação. SHALL distinguir zero de ausência, preservar sinais negativos e SHALL identificar quando uma consulta paginada é parcial antes de apresentar ranking ou agregação global.

#### Scenario: Resultado negativo
- **WHEN** a API retorna um resultado negativo
- **THEN** a interface apresenta o sinal antes do valor monetário e comunica positivo/negativo também por texto ou símbolo, não apenas por cor

#### Scenario: Valor ausente
- **WHEN** a API retorna `null` para uma métrica financeira
- **THEN** a interface apresenta `—` ou indisponível e não substitui o valor por zero
