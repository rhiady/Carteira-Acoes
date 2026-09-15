# portfolio-catalog Specification

## Purpose

Permitir que o sistema mantenha os proprietários, carteiras e ações que formam a base consultável do acompanhamento de investimentos.
## Requirements
### Requirement: Cadastro e consulta de usuários
O sistema SHALL cadastrar usuários com nome, e-mail único e senha obrigatórios; a senha MUST NOT ser retornada por nenhuma API. O sistema SHALL expor criação, busca por identificador e listagem paginada de usuários. O sistema SHALL permitir a atualização integral de nome e e-mail de um usuário existente por `PUT /usuarios/{id}`, aceitando alteração opcional da senha, sem retornar ou persistir senha em texto puro. O e-mail informado na atualização MUST permanecer único entre os usuários e o sistema MUST rejeitar identificador inexistente ou dados inválidos com erro padronizado.

#### Scenario: Cadastro válido de usuário
- **WHEN** um cliente envia nome, e-mail válido não utilizado e senha
- **THEN** o sistema cria o usuário e responde sem expor a senha

#### Scenario: E-mail duplicado
- **WHEN** um cliente cadastra um e-mail já existente
- **THEN** o sistema responde com erro de conflito ou validação padronizado e não cria outro usuário

#### Scenario: Atualização válida de usuário
- **WHEN** um cliente envia `PUT /usuarios/{id}` com nome e e-mail válidos e, opcionalmente, uma nova senha
- **THEN** o sistema atualiza o usuário identificado e responde com os dados atuais sem expor a senha

#### Scenario: Atualização sem alterar senha
- **WHEN** um cliente atualiza nome e e-mail sem informar nova senha
- **THEN** o sistema preserva a senha existente e retorna o usuário atualizado sem expô-la

#### Scenario: E-mail já utilizado na atualização
- **WHEN** um cliente altera o e-mail para um endereço pertencente a outro usuário
- **THEN** o sistema responde com erro de conflito ou validação padronizado e não altera o usuário

#### Scenario: Usuário inexistente na atualização
- **WHEN** um cliente envia atualização para um identificador de usuário inexistente
- **THEN** o sistema responde com erro padronizado de recurso não encontrado

#### Scenario: Dados inválidos na atualização
- **WHEN** um cliente envia nome, e-mail ou senha que não atendem às validações do contrato
- **THEN** o sistema responde HTTP 400 com erro padronizado e não altera o usuário

### Requirement: Gestão de carteiras
O sistema SHALL criar carteiras nomeadas vinculadas a exatamente um usuário existente e a exatamente uma corretora existente e validada pelo sistema. O cadastro MUST informar o identificador da corretora, que SHALL ser retornado nas consultas de carteira. O sistema MUST rejeitar uma corretora inexistente; o sistema SHALL permitir que a mesma corretora seja vinculada a diferentes carteiras. O sistema SHALL permitir buscar uma carteira, listar carteiras e listar as carteiras de um usuário. Uma carteira MUST apresentar somente suas posições ativas ao ser consultada pelo recurso de posições.

#### Scenario: Criação para usuário existente
- **WHEN** um cliente cria uma carteira informando um usuário existente e o identificador de uma corretora validada existente
- **THEN** o sistema persiste a carteira associada ao usuário e à corretora informada

#### Scenario: Corretora ausente ou inexistente
- **WHEN** um cliente tenta criar uma carteira sem identificador de corretora ou com um identificador inexistente
- **THEN** o sistema rejeita a requisição com erro de validação e não persiste a carteira

#### Scenario: Corretora exibida na consulta
- **WHEN** um cliente consulta uma carteira ou uma lista de carteiras
- **THEN** cada carteira retornada inclui o identificador de sua corretora

#### Scenario: Usuário inexistente
- **WHEN** um cliente cria ou consulta uma carteira usando usuário inexistente
- **THEN** o sistema responde com erro padronizado de recurso não encontrado

### Requirement: Catálogo de ações por mercado
O sistema SHALL consultar ações por identificador ou ticker e SHALL listar ações paginadamente. O sistema SHALL expor `POST /acoes` para criar uma ação diretamente a partir de `ticker` e `mercado`, sem exigir carteira, posição ou operação, respondendo HTTP 201 com a ação criada e o cabeçalho `Location`. Na criação direta ou pela compra, o sistema SHALL importar nome da empresa e cotação atual do provedor compatível e persistir ticker único em maiúsculas, mercado `BRASIL` ou `EUA` e a moeda derivada BRL ou USD. O cliente MUST NOT informar nome da empresa, moeda, preço ou data/hora da cotação. O sistema MUST rejeitar ticker duplicado, ticker não localizado ou resposta sem nome ou cotação válida sem persistir a ação; uma criação direta bem-sucedida MUST NOT criar posição nem operação.

#### Scenario: Criação de ação por compra
- **WHEN** uma compra válida informa ticker e mercado ainda não cadastrados
- **THEN** o sistema cria a ação com seus dados de mercado e a associa somente à posição criada ou atualizada para a carteira da compra

#### Scenario: Cadastro direto indisponível
- **WHEN** um cliente envia `ticker` e `mercado` válidos para `POST /acoes` e o provedor retorna dados válidos
- **THEN** o sistema cria e retorna a ação importada sem criar carteira, posição ou operação

#### Scenario: Ação brasileira válida
- **WHEN** uma compra válida informa ticker do mercado BRASIL ainda não cadastrado
- **THEN** o sistema cria a ação com moeda BRL, ticker em maiúsculas, nome da empresa e cotação importados

#### Scenario: Cadastro direto com ticker duplicado
- **WHEN** um cliente envia para `POST /acoes` um ticker já cadastrado
- **THEN** o sistema retorna erro de conflito e não altera a ação existente

#### Scenario: Combinação de mercado e moeda inválida
- **WHEN** uma compra ou cadastro direto informa ticker inexistente ou o provedor não retorna dados válidos para o mercado
- **THEN** o sistema rejeita a solicitação sem persistir ação, posição ou operação

### Requirement: Posições ativas da carteira
O sistema SHALL listar as ações de uma carteira apenas quando a quantidade da posição for maior que zero. `GET /carteiras/{id}/acoes` SHALL retornar uma página de posições enriquecidas, com paginação padrão `page=0`, `size=20` e `sort=id,desc`. O endpoint SHALL aceitar ordenação pública somente por `ticker`, `quantidade` e `id`, em direção ascendente ou descendente, sem exigir que clientes conheçam nomes internos de persistência. Campos de ordenação não permitidos MUST ser rejeitados com erro 400 padronizado. Cada posição SHALL conter `carteiraId`, `acaoId`, ticker, nome da empresa, mercado, moeda, quantidade, cotação atual, data/hora da cotação, valor atual, preço médio de venda quando aplicável, lucro/prejuízo realizado, não realizado e total, todos calculados pelas operações históricas com precisão decimal. Quando nenhuma venda tiver sido registrada para a posição, `precoMedioVenda` SHALL ser `null`, não zero. Para posições de ações do mercado EUA cuja taxa USD/BRL esteja disponível, a resposta SHALL incluir `cotacaoEmBrl`, `valorAtualEmBrl`, `taxaUsdBrl` e `dataHoraTaxaUsdBrl`, preservando todos os valores financeiros originais em USD. Quantidades e valores monetários MUST ser representados com precisão decimal; campos monetários originais de cada posição SHALL estar na moeda da própria ação.

#### Scenario: Posição zerada não é exibida
- **WHEN** uma carteira contém uma posição com quantidade igual a zero
- **THEN** a consulta de posições ativas não inclui essa posição

#### Scenario: Posição enriquecida
- **WHEN** um cliente consulta posições ativas de uma carteira
- **THEN** cada item retornado inclui os indicadores financeiros oficiais calculados pelo backend e a moeda da ação

#### Scenario: Preço médio de venda sem venda
- **WHEN** um cliente consulta uma posição ativa que nunca teve venda registrada
- **THEN** a posição retorna `precoMedioVenda` como `null`

#### Scenario: Posição americana com equivalente em BRL
- **WHEN** um cliente consulta uma posição ativa de ação do mercado EUA e a taxa USD/BRL está disponível
- **THEN** a posição retorna a cotação e o valor atual originais em USD e os equivalentes `cotacaoEmBrl` e `valorAtualEmBrl`

#### Scenario: Ordenação por ticker ascendente
- **WHEN** um cliente consulta `GET /carteiras/{id}/acoes?sort=ticker,asc`
- **THEN** o sistema retorna HTTP 200 com as posições ordenadas por ticker em ordem ascendente

#### Scenario: Ordenação por ticker descendente
- **WHEN** um cliente consulta `GET /carteiras/{id}/acoes?sort=ticker,desc`
- **THEN** o sistema retorna HTTP 200 com as posições ordenadas por ticker em ordem descendente

#### Scenario: Paginação com ordenação por ticker
- **WHEN** um cliente consulta posições ativas com `page`, `size` e `sort=ticker,asc`
- **THEN** o sistema aplica a ordenação por ticker antes de retornar a página solicitada

#### Scenario: Ordenação inválida
- **WHEN** um cliente consulta posições ativas com campo de ordenação diferente de `ticker`, `quantidade` ou `id`
- **THEN** o sistema responde HTTP 400 com erro padronizado e não retorna erro 500

#### Scenario: Consulta sem ordenação explícita
- **WHEN** um cliente consulta `GET /carteiras/{id}/acoes` sem parâmetro `sort`
- **THEN** o sistema retorna HTTP 200 usando a ordenação padrão do endpoint

#### Scenario: Indicadores coerentes com o histórico
- **WHEN** uma posição possui compras e vendas parciais
- **THEN** os campos de preço médio e lucro/prejuízo da resposta correspondem ao processamento cronológico de todas as operações

#### Scenario: DTO contém preço médio de aquisição
- **WHEN** um cliente consulta uma posição com compras registradas
- **THEN** cada item retorna `precoMedio` com o custo médio de aquisição calculado pelas operações

### Requirement: Response financeiro completo de posições
`GET /carteiras/{id}/acoes` SHALL refletir os campos financeiros existentes `carteiraId`, `acaoId`, `ticker`, `nomeEmpresa`, `mercado`, `moeda`, `quantidade`, `cotacaoAtual`, `dataHoraCotacao`, `valorAtual`, `precoMedio`, `cotacaoEmBrl`, `valorAtualEmBrl`, `taxaUsdBrl`, `dataHoraTaxaUsdBrl`, `precoMedioVenda`, `lucroPrejuizoRealizado`, `lucroPrejuizoNaoRealizado` e `lucroPrejuizo`, com tipos, nullability e serialização documentados sem fabricar cotação ou resultado.

#### Scenario: Cotação ausente
- **WHEN** uma posição não possui cotação atual válida
- **THEN** os campos dependentes da cotação ficam ausentes ou nulos conforme o contrato, sem cálculo fictício

### Requirement: Resumo financeiro segmentado da carteira
O sistema SHALL expor `GET /carteiras/{id}/resumo` com o identificador e nome da carteira, a quantidade de ativos com posição positiva e uma coleção de subtotais por moeda. Cada subtotal SHALL informar a moeda, `valorAtual` e `lucroPrejuizo`; o sistema MUST NOT somar valores em BRL e USD nem retornar um total financeiro consolidado sem conversão cambial. O `lucroPrejuizo` de cada subtotal SHALL ser a soma dos resultados totais das posições daquela moeda, com precisão decimal.

#### Scenario: Carteira com uma moeda
- **WHEN** uma carteira possui posições ativas somente em BRL
- **THEN** o resumo retorna um único subtotal BRL com o valor atual e o lucro/prejuízo calculados

#### Scenario: Carteira multimoeda
- **WHEN** uma carteira possui posições ativas em BRL e USD
- **THEN** o resumo retorna subtotais distintos para BRL e USD sem somar seus valores

#### Scenario: Carteira inexistente
- **WHEN** um cliente consulta o resumo de uma carteira inexistente
- **THEN** o sistema retorna erro padronizado de carteira não encontrada

#### Scenario: Resumo consistente com posições
- **WHEN** uma carteira possui posições com lucro/prejuízo realizado e não realizado
- **THEN** o subtotal de cada moeda soma o lucro/prejuízo total das posições correspondentes sem conversão implícita
