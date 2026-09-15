## ADDED Requirements

### Requirement: Response financeiro completo de posições
`GET /carteiras/{id}/acoes` SHALL refletir os campos financeiros existentes `carteiraId`, `acaoId`, `ticker`, `nomeEmpresa`, `mercado`, `moeda`, `quantidade`, `cotacaoAtual`, `dataHoraCotacao`, `valorAtual`, `precoMedio`, `cotacaoEmBrl`, `valorAtualEmBrl`, `taxaUsdBrl`, `dataHoraTaxaUsdBrl`, `precoMedioVenda`, `lucroPrejuizoRealizado`, `lucroPrejuizoNaoRealizado` e `lucroPrejuizo`, com tipos, nullability e serialização documentados sem fabricar cotação ou resultado.

#### Scenario: Cotação ausente
- **WHEN** uma posição não possui cotação atual válida
- **THEN** os campos dependentes da cotação ficam ausentes ou nulos conforme o contrato, sem cálculo fictício

