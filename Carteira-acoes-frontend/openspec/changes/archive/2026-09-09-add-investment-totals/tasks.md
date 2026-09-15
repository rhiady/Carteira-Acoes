## 1. Contrato e domínio

- [x] 1.1 Confirmar contrato real nos models, services, mappers e fixtures existentes.
- [x] 1.2 Atualizar DTO/domain de posição para os novos campos com nullability correta.
- [x] 1.3 Atualizar mapper sem recalcular nem converter ausência em zero.

## 2. Interface financeira

- [x] 2.1 Atualizar detalhe/linha de posição com investido atualmente, comprado, vendido e resultados contextuais.
- [x] 2.2 Atualizar detalhe da carteira com totais globais somente quando autoritativos e disponíveis.
- [x] 2.3 Avaliar Dashboard/Analytics e ajustar apenas onde houver dados completos e benefício claro.
- [x] 2.4 Preservar formatters BRL/USD, sinais, null, loading/error e responsividade.

## 3. Validação

- [x] 3.1 Adicionar ou ajustar testes de modelo, mapper e apresentação para cenário completo e ausências.
- [x] 3.2 Verificar que paginação não gera totais globais incorretos.
- [x] 3.3 Executar testes Angular e corrigir regressões.
- [x] 3.4 Executar build Angular e revisar visualmente as telas alteradas.
