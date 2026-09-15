## MODIFIED Requirements

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
