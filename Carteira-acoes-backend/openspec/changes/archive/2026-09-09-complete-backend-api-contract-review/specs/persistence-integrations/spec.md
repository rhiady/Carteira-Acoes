## Purpose

Estabelece a revisão completa do modelo persistente e das integrações externas, garantindo compatibilidade entre entidades, PostgreSQL, Flyway, consultas e transações.

## ADDED Requirements

### Requirement: Modelo persistente compatível com PostgreSQL
Cada entity e campo MUST corresponder ao schema final produzido pelas migrations quanto a tipo, nullability, precisão, escala, relacionamento, cardinalidade, cascade, fetch, unicidade, índice e semântica temporal. Dinheiro MUST ser persistido em tipo decimal com precisão suficiente, nunca como ponto flutuante.

#### Scenario: Schema reproduzido por Flyway
- **WHEN** todas as migrations são aplicadas em um PostgreSQL vazio
- **THEN** o schema resultante é compatível com todas as entities sem depender de comportamento específico do H2

### Requirement: Evolução segura por migrations
Migrations aplicadas MUST NOT ser reescritas para corrigir o histórico. Toda correção necessária SHALL ser adicionada em nova migration, preservando ordem, dados existentes, constraints e rollback operacional documentado.

#### Scenario: Divergência encontrada
- **WHEN** a auditoria encontra diferença entre entity e migration já aplicada
- **THEN** a correção usa uma nova migration testada no PostgreSQL real e não modifica arquivos históricos aplicados

### Requirement: Consultas corretas e contextualizadas
Todos os repositories MUST ser revisados quanto a filtros, joins, paginação, ordenação, ownership, duplicação e N+1. Correções de fetch MUST ser localizadas em fluxos comprovadamente afetados e consultas financeiras MUST restringir recursos pelo proprietário quando aplicável.

#### Scenario: Consulta financeira por identificador
- **WHEN** uma carteira, posição ou operação é buscada por ID no contexto autenticado
- **THEN** a consulta ou regra de acesso impede que um recurso pertencente a outro usuário seja retornado

### Requirement: Integrações externas resilientes
Cada client de cotação, cadastro e câmbio MUST tratar timeout, rede, status não 2xx, body vazio, JSON inválido, campo ausente e valor inválido com erro padronizado e logging sem segredos. Falha externa MUST NOT corromper estado local nem substituir dados válidos por valores fictícios.

#### Scenario: Resposta externa inválida
- **WHEN** um provedor retorna erro, payload vazio ou dados inválidos
- **THEN** a API traduz a falha, preserva o estado válido anterior e não persiste cotação, histórico, posição ou operação parcial

### Requirement: Cotações, histórico e câmbio rastreáveis
Atualização individual e em lote MUST preservar mercado, moeda, preço decimal, timestamp e histórico somente para respostas válidas. Conversão USD/BRL MUST expor taxa e momento de referência, e o histórico MUST possuir ordenação, filtros, paginação e ausência de dados semanticamente definidos.

#### Scenario: Atualização parcial em lote
- **WHEN** uma atualização em lote obtém sucesso para algumas ações e falha para outras
- **THEN** somente sucessos são persistidos e historizados, as demais ações permanecem intactas e os contadores da resposta são consistentes

#### Scenario: Conversão identificável
- **WHEN** um valor USD é apresentado também em BRL
- **THEN** a response preserva o valor original e informa taxa USD/BRL e timestamp utilizados na conversão
