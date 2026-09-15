## 1. Contrato de compra e catálogo

- [x] 1.1 Alterar o contrato de compra para aceitar exatamente uma identificação de ação: `acaoId` ou o par `ticker` e `mercado`, com validação e documentação OpenAPI.
- [x] 1.2 Remover o endpoint `POST /acoes`, preservando somente as operações de consulta e atualização de cotação do catálogo.
- [x] 1.3 Atualizar DTOs e documentação de clientes para migrar do cadastro direto para compra com ticker e mercado.

## 2. Fluxo transacional de compra

- [x] 2.1 Extrair/reutilizar a resolução de ação por ticker e mercado para que o serviço de compra localize ou crie a ação dentro de sua transação.
- [x] 2.2 Criar ou atualizar `CarteiraAcao` exclusivamente para a carteira da compra depois de resolver a ação.
- [x] 2.3 Garantir rollback de ação nova, posição e operação quando falhar validação, cotação externa, persistência ou cálculo.
- [x] 2.4 Tratar conflito de unicidade quando compras concorrentes tentarem criar o mesmo ticker sem criar posições ou operações inconsistentes.

## 3. Testes e verificação

- [x] 3.1 Cobrir compra por `acaoId`, compra que cria ação/posição por ticker e mercado e compra que reutiliza ação já cadastrada.
- [x] 3.2 Cobrir todas as identificações inválidas, carteira inexistente, ticker externo inválido e rollback completo.
- [x] 3.3 Verificar que o cadastro direto de ação não está disponível e que consultas ao catálogo continuam disponíveis.
- [x] 3.4 Executar a suíte automatizada e validar a mudança OpenSpec em modo estrito.
