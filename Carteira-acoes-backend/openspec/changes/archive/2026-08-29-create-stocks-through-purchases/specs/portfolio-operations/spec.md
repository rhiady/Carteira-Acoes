## MODIFIED Requirements

### Requirement: Registro de compra
O sistema SHALL registrar uma compra somente para carteira existente quando a quantidade for positiva. A compra SHALL identificar uma ação existente por `acaoId` OU informar conjuntamente `ticker` e `mercado`; MUST rejeitar identificação ausente, combinação parcial de ticker/mercado ou as duas formas de identificação na mesma requisição. Quando `acaoId` for informado, a ação deverá existir. Quando ticker e mercado forem informados, o sistema SHALL reutilizar a ação correspondente se ela existir ou criá-la a partir do provedor de mercado, dentro da mesma transação da compra. Se o preço unitário for informado, SHALL usá-lo; se estiver ausente, SHALL obter cotação válida externamente. A compra MUST criar ou aumentar somente a posição da ação na carteira informada e registrar os valores bruto e líquido calculados.

#### Scenario: Compra de ação existente por identificador
- **WHEN** um cliente compra quantidade positiva para uma carteira existente informando `acaoId` válido e preço unitário válido
- **THEN** o sistema registra a compra e aumenta somente a posição daquela carteira sem consultar cotação externa

#### Scenario: Compra com preço informado
- **WHEN** um cliente compra quantidade positiva de uma ação existente com preço unitário válido
- **THEN** o sistema registra a compra e aumenta a posição sem consultar cotação externa

#### Scenario: Compra cria ação e posição
- **WHEN** um cliente compra quantidade positiva para uma carteira existente informando ticker e mercado ainda não cadastrados
- **THEN** o sistema importa a ação, cria sua posição naquela carteira e registra a compra na mesma transação

#### Scenario: Compra reutiliza ação por ticker
- **WHEN** um cliente compra informando ticker e mercado de uma ação já cadastrada
- **THEN** o sistema reutiliza a ação existente e altera somente a posição da carteira informada

#### Scenario: Identificação de ação inválida
- **WHEN** uma compra informa `acaoId` junto com ticker/mercado, omite todos esses campos ou informa apenas um entre ticker e mercado
- **THEN** o sistema responde com erro de validação e não persiste ação, posição ou operação

#### Scenario: Compra sem preço e cotação indisponível
- **WHEN** um cliente compra sem informar preço e a cotação não pode ser obtida
- **THEN** o sistema não registra operação nem altera a posição
