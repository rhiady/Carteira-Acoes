## 1. Persistência e contratos de cotações

- [x] 1.1 Criar migration Flyway para `historico_cotacao`, sua chave estrangeira e índices de consulta por ação e data/hora.
- [x] 1.2 Implementar entidade, repositório, mapper e `HistoricoCotacaoResponse` com preços decimais e timestamps UTC.
- [x] 1.3 Alterar importação e atualização individual de ação para gravar a cotação atual e o histórico na mesma transação somente após resposta válida.
- [x] 1.4 Implementar `GET /acoes/{id}/historico` com paginação padrão, ordenação cronológica e filtros inclusivos de início/fim.
- [x] 1.5 Implementar atualização global com unidade transacional isolada por ação e `AtualizacaoCotacoesResponse` contendo contadores consistentes e timestamp UTC.
- [x] 1.6 Testar importação/atualização com histórico, filtros e ordenação, ação inexistente, cotação inválida e atualização global parcial e total.

## 2. Cálculo financeiro de operações

- [x] 2.1 Ajustar a normalização de custos e o cálculo de valores de operação para distinguir custo total de compra de receita líquida de venda.
- [x] 2.2 Criar consultas de operações ordenadas por data/hora e ID para uma carteira e ação, com acesso eficiente para páginas de posições.
- [x] 2.3 Implementar componente de serviço para calcular custo médio móvel, custo remanescente, preço médio de venda e resultados realizado, não realizado e total a partir do histórico imutável.
- [x] 2.4 Preservar validações de identificação exclusiva da compra, quantidade mínima e custos não negativos; assegurar que a venda continua usando cotação externa.
- [x] 2.5 Testar cenários de compras intercaladas, vendas parciais/totais, custos, preço médio ponderado, resultado negativo e ordenação com timestamps iguais.

## 3. Consultas enriquecidas de carteira

- [x] 3.1 Criar `PosicaoAcaoResponse` e adaptar `GET /carteiras/{id}/acoes` para retornar página de posições ativas com cotação, moeda e indicadores calculados.
- [x] 3.2 Criar `CarteiraResumoResponse` e `ResumoPorMoedaResponse`, agrupando valor atual e lucro/prejuízo por moeda sem total multimoeda.
- [x] 3.3 Implementar `GET /carteiras/{id}/resumo`, incluindo quantidade de ativos e tratamento padronizado de carteira inexistente.
- [x] 3.4 Testar posições enriquecidas, exclusão de posição zerada, resumo BRL, resumo USD e carteira multimoeda sem soma entre moedas.

## 4. Integração e verificação

- [x] 4.1 Atualizar testes de recursos e serviços para os novos contratos HTTP, paginação e formato de erro existente.
- [x] 4.2 Executar a suíte Maven completa com perfil de testes e corrigir regressões de migrations, mapeamentos e operações concorrentes.
- [x] 4.3 Validar a mudança OpenSpec em modo estrito e revisar que os endpoints expõem somente valores financeiros calculados pelo backend.
