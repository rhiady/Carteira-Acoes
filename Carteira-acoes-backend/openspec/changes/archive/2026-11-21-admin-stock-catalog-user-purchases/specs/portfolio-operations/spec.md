## MODIFIED Requirements

### Requirement: Registro de compra
O sistema SHALL registrar uma compra de usuário comum somente para carteira própria existente quando a quantidade for no mínimo `0.0001`. A compra de `ROLE_USER` MUST identificar exclusivamente uma ação global existente por `acaoId`; `ticker` e `mercado` MUST ser ausentes e MUST NOT localizar, reutilizar ou criar ações nesse fluxo. Uma `acaoId` inexistente, ausência de `acaoId` ou presença de ticker/mercado SHALL produzir erro de validação ou recurso não encontrado sem persistir ação, posição ou operação. Se o preço unitário for informado, SHALL usá-lo; se estiver ausente, SHALL obter cotação válida externamente. Corretagem, impostos e valor adicional serão custos opcionais. Uma compra bem-sucedida SHALL persistir a operação e criar ou aumentar somente a posição da ação na carteira informada, registrar os valores bruto e líquido calculados e responder HTTP 201 com o identificador da operação. Quando não puder ser persistida, a API SHALL retornar um erro padronizado que identifique a categoria da falha e MUST NOT retornar sucesso.

#### Scenario: Compra de ação disponibilizada por identificador
- **WHEN** um usuário comum compra quantidade positiva para carteira própria informando `acaoId` existente e preço unitário válido
- **THEN** o sistema registra a compra e aumenta somente a posição daquela carteira sem consultar cotação externa

#### Scenario: Compra usa cotação quando não há preço manual
- **WHEN** um usuário comum compra quantidade positiva de uma ação existente sem informar preço unitário e a cotação é válida
- **THEN** o sistema registra a operação usando a cotação e aumenta somente a posição da carteira própria

#### Scenario: Compra com preço manual
- **WHEN** um usuário comum compra quantidade positiva de uma ação existente com preço unitário válido
- **THEN** o sistema registra e preserva o preço informado sem consultar cotação externa

#### Scenario: Usuário comum tenta criar ou referenciar ação por ticker
- **WHEN** um usuário comum envia compra com ticker e mercado, com ou sem `acaoId`
- **THEN** o sistema rejeita a requisição e não persiste ação, posição ou operação

#### Scenario: Ação inexistente
- **WHEN** um usuário comum envia compra com `acaoId` que não existe
- **THEN** o sistema responde com erro padronizado e não persiste posição ou operação

#### Scenario: Compra sem preço e cotação indisponível
- **WHEN** uma compra ocorre sem preço e a cotação não pode ser obtida
- **THEN** a API retorna erro padronizado de indisponibilidade e não registra operação nem altera a posição

#### Scenario: Compra de ação existente por identificador
- **WHEN** um cliente compra quantidade positiva para carteira própria informando `acaoId` válido e preço unitário válido
- **THEN** o sistema registra a compra e aumenta somente a posição daquela carteira sem consultar cotação externa

#### Scenario: Compra com preço informado
- **WHEN** um cliente compra quantidade positiva de uma ação existente com preço unitário válido
- **THEN** o sistema registra a compra e aumenta a posição sem consultar cotação externa

#### Scenario: Compra cria ação e posição
- **WHEN** uma compra informa ticker e mercado de ação ainda não cadastrada
- **THEN** o sistema rejeita a requisição e não cria ação, posição ou operação

#### Scenario: Compra reutiliza ação por ticker
- **WHEN** uma compra informa ticker e mercado de ação já cadastrada
- **THEN** o sistema rejeita a requisição e não altera a posição da carteira

#### Scenario: Compra persistida pela API
- **WHEN** um cliente envia uma compra válida para `POST /operacoes/compras` com `acaoId` existente
- **THEN** a API responde HTTP 201 com o identificador da operação, e a operação e a posição correspondente podem ser consultadas após a resposta

#### Scenario: Identificação de ação inválida
- **WHEN** uma compra omite `acaoId` ou informa ticker ou mercado
- **THEN** o sistema responde com erro de validação e não persiste ação, posição ou operação
