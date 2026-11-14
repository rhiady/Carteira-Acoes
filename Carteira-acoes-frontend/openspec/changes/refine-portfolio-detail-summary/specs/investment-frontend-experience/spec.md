## ADDED Requirements

### Requirement: Superfície da carteira compartilha estados compreensíveis
O detalhe da carteira SHALL manter loading, vazio, erro, retry, sucesso de atualização e indisponibilidade financeira associados ao contexto da carteira. Esses estados SHALL preservar conteúdo válido já exibido quando aplicável, anunciar o próximo passo e ser compreensíveis sem depender somente de cor.

#### Scenario: Falha ao carregar analytics
- **WHEN** o resumo ou os gráficos falham após a carteira ter sido identificada
- **THEN** a interface mantém o contexto da carteira, informa qual superfície falhou e oferece retry seguro sem apresentar dados inventados como atuais

#### Scenario: Carteira sem posições
- **WHEN** a carteira não possui posições ativas
- **THEN** o detalhe apresenta um estado vazio explicativo e uma ação explícita para comprar a primeira ação
