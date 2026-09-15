## 1. Inventário e baseline

- [x] 1.1 Mapear controllers, endpoints, DTOs, entities, services, repositories, handlers, clients externos e schemas OpenAPI.
- [x] 1.2 Inventariar testes existentes e executar baseline de `./mvnw test`, registrando a contaminação entre contextos H2; o teste isolado passou.
- [x] 1.3 Comparar migrations Flyway com entities, constraints, índices, tipos decimais e timestamps sem reescrever migrations aplicadas; Flyway validou as 11 migrations em H2.

## 2. Contratos HTTP e validações

- [x] 2.1 Comparar cada endpoint existente com request, response, status, nullability, serialização e documentação OpenAPI.
- [x] 2.2 Auditar Usuários, incluindo `PUT /usuarios/{id}`, preservação de senha, unicidade de email e ausência de senha em responses/logs.
- [x] 2.3 Auditar Corretoras, Carteiras e Ações quanto a IDs, CNPJ, ticker, mercado, moeda, duplicidade e recursos inexistentes.
- [x] 2.4 Auditar Bean Validation para nulos, vazios, email, CNPJ, ticker, mercado, quantidade e preço zero/negativo.
- [x] 2.5 Auditar paginação e ordenação, corrigindo no backend campos inválidos e metadados incoerentes.
- [x] 2.6 Auditar exceptions e handlers para respostas 400, 404, 405, 409, 422 e falhas externas, evitando HTTP 500 indevido.

## 3. Operações e finanças

- [x] 3.1 Confirmar contrato de identificação de ação em Compra e Venda e rejeitar combinações ausentes, parciais ou ambíguas.
- [x] 3.2 Validar Compra, primeira compra, compras sucessivas, média ponderada, custos, moeda e precisão BigDecimal.
- [x] 3.3 Validar Venda parcial, total, acima do saldo, posição zerada, custo médio remanescente e preço médio de venda nulo quando aplicável.
- [x] 3.4 Validar lucro/prejuízo realizado, não realizado e total, inclusive custos e cotação ausente.
- [x] 3.5 Confirmar atomicidade de operação/posição e investigar concorrência de compras e vendas; corrigir somente risco reproduzível.
- [x] 3.6 Adicionar ou ajustar testes para os cenários financeiros dos AC-01 a AC-08.

## 4. Posições, mercado e integrações

- [x] 4.1 Validar o response de `GET /carteiras/{id}/acoes` com todos os campos financeiros, tipos, nullability e serialização.
- [x] 4.2 Validar cotações atuais, histórico ordenado, ação inexistente, histórico vazio, duplicidade e ausência de pontos fabricados.
- [x] 4.3 Validar falhas, timeouts, bodies inválidos e respostas não-2xx dos clientes externos sem apagar dados válidos.
- [x] 4.4 Validar conversão BRL/USD, taxa e timestamps, mantendo valores originais e ausência explícita quando não houver câmbio.
- [x] 4.5 Adicionar ou ajustar testes de cotações, histórico, câmbio e respostas de posição.

## 5. Persistência e configuração

- [x] 5.1 Revisar relacionamentos, cardinalidade, foreign keys, nullability, cascade, fetch, N+1 relevante e queries paginadas/ordenadas.
- [x] 5.2 Corrigir constraints ou schema somente por migration incremental quando necessário e validar banco limpo e existente.
- [x] 5.3 Verificar transações de Compra, Venda, atualização individual e atualização em lote.
- [x] 5.4 Verificar datasource, Flyway, perfis, H2, Open Session in View, variáveis `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` e ausência de secrets hardcoded.

## 6. OpenAPI, runtime e qualidade

- [x] 6.1 Atualizar OpenAPI para refletir implementação corrigida de usuários, Compra, Venda, posições, indicadores financeiros, BRL/USD e erros.
- [x] 6.2 Executar `./mvnw test` após as correções e investigar qualquer regressão.
- [x] 6.3 Executar o build do projeto e validar inicialização, datasource, Flyway, Hibernate e Tomcat em runtime quando o ambiente permitir.
- [x] 6.4 Validar endpoints principais em runtime e classificar warnings separados de erros fatais.
- [x] 6.5 Produzir relatório `BACKEND REVIEW` com severidade, arquivo/classe, impacto, correção e status de cada achado.
- [x] 6.6 Confirmar que nenhuma alteração foi feita no frontend, `deploy`, `docker-compose.yml`, Nginx ou infraestrutura compartilhada.
