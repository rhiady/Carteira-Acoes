## Purpose

Fornecer cotações atuais confiáveis para ações brasileiras e americanas, isolando provedores externos e suas falhas do domínio de carteira.

## ADDED Requirements

### Requirement: Consulta normalizada de cotação
O sistema SHALL obter a cotação de uma ação por um provedor compatível com seu mercado e SHALL normalizar o resultado em preço decimal e data/hora em UTC. A API de atualização de cotação SHALL atualizar os dados atuais da ação somente quando a consulta for bem-sucedida.

#### Scenario: Atualização bem-sucedida
- **WHEN** um cliente solicita atualizar a cotação de uma ação existente e o provedor retorna uma cotação válida
- **THEN** o sistema persiste o novo preço e a data/hora da cotação e retorna a ação atualizada

#### Scenario: Ação não encontrada
- **WHEN** um cliente solicita atualização para uma ação inexistente
- **THEN** o sistema retorna erro padronizado de ação não encontrada

### Requirement: Sem preço fictício em falhas
O sistema MUST NOT substituir uma cotação ausente por zero, valor estimado ou valor fictício. Quando a cotação não for encontrada, o provedor exigir autenticação, aplicar limite de uso ou estiver indisponível, o sistema SHALL retornar uma categoria de erro padronizada que permita ao cliente distinguir essas condições.

#### Scenario: Provedor indisponível
- **WHEN** a consulta de cotação falha por timeout ou erro transitório do provedor
- **THEN** o sistema retorna erro de API indisponível e não altera a cotação existente

#### Scenario: Limite de uso excedido
- **WHEN** o provedor responde que o limite de requisições foi atingido
- **THEN** o sistema retorna erro de limite de uso sem repetir imediatamente a chamada
