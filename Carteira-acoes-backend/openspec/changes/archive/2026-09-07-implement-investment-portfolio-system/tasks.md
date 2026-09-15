## 1. Fundação do projeto e banco de dados

- [x] 1.1 Adicionar dependências de Flyway, OpenFeign, Lombok e documentação OpenAPI compatíveis com Spring Boot 4.
- [x] 1.2 Configurar propriedades compartilhadas, perfis `dev` e `test`, UTC, timeout de clientes e variáveis de ambiente.
- [x] 1.3 Criar `.env.example`, atualizar `.gitignore` e documentar a configuração local sem expor segredos.
- [x] 1.4 Criar migrations Flyway para usuários, corretoras, ações, carteiras, posições, operações, constraints e índices.
- [x] 1.5 Validar migrations e mappings no banco de teste.

## 2. Modelo de domínio e infraestrutura comum

- [x] 2.1 Implementar entidades, enums e auditoria temporal para Usuario, Carteira, Acao, CarteiraAcao, Corretora e Operacao.
- [x] 2.2 Aplicar relações LAZY, IDs `Long`, colunas `DECIMAL(19,4)`, constraints e versionamento otimista da posição.
- [x] 2.3 Criar repositórios com paginação, busca por identificadores de negócio e consultas ordenadas de posições e operações.
- [x] 2.4 Criar DTOs, mappers, exceções de domínio e manipulador global com formato padronizado de erro.
- [x] 2.5 Cobrir regras financeiras, serialização sem entidades JPA e respostas de erro comuns com testes unitários.

## 3. Catálogo de usuários, carteiras e ações

- [x] 3.1 Implementar serviços e endpoints de criação, busca e paginação de usuários, garantindo e-mail único e senha ausente das respostas.
- [x] 3.2 Implementar serviços e endpoints de criação e consulta de carteiras, inclusive listagem por usuário e posições ativas.
- [x] 3.3 Implementar cadastro e endpoints de ações por ID e ticker, recebendo ticker e mercado e importando nome e cotação, com normalização de ticker e moeda derivada do mercado.
- [x] 3.4 Implementar testes de integração para catálogo, paginação, duplicidades e recursos inexistentes.

## 4. Integrações externas e corretoras

- [x] 4.1 Configurar clientes OpenFeign, DTOs de integração e conversão centralizada de erros HTTP externos.
- [x] 4.2 Implementar adapters de cotação para os provedores brasileiro e americano e o contrato comum de consulta.
- [x] 4.3 Implementar facades de CNPJ e CVM, com normalização dos dados de corretora importados da consulta de CNPJ.
- [x] 4.4 Implementar serviço e endpoints de corretora que recebem somente CNPJ e recusam persistência sem validação CVM e dados obrigatórios importados.
- [x] 4.5 Adicionar testes com dublês para sucesso, não encontrado, autenticação, rate limit, timeout e indisponibilidade das integrações.

## 5. Cotações de ações

- [x] 5.1 Implementar atualização de cotação por ação, com preço decimal e data/hora em UTC.
- [x] 5.2 Aplicar política de timeout, retry restrito e ausência de preço fictício para falhas de provedor.
- [x] 5.3 Cobrir atualização bem-sucedida e todas as categorias de erro externo com testes.

## 6. Operações e consistência de carteira

- [x] 6.1 Implementar criação imutável de operação e cálculo de valor bruto, líquido e valores opcionais.
- [x] 6.2 Implementar compra com preço informado e compra dependente de cotação, criando ou incrementando a posição na mesma transação.
- [x] 6.3 Implementar venda dependente de cotação, validação de saldo de ações e manutenção de posição zerada.
- [x] 6.4 Expor endpoints de compra, venda, busca e histórico paginado sem endpoints de edição de operação.
- [x] 6.5 Testar rollback em falhas, venda superior à posição, venda total, ordenação do histórico e concorrência otimista.

## 7. Qualidade e entrega operacional

- [x] 7.1 Adicionar logs seguros e estruturados para integrações, excluindo credenciais e dados sensíveis.
- [x] 7.2 Atualizar README com arquitetura, endpoints, execução, perfis e documentação de cada integração externa.
- [x] 7.3 Executar suíte completa de testes e corrigir regressões de validação, persistência e contratos HTTP.
- [x] 7.4 Validar a mudança OpenSpec em modo estrito e registrar qualquer limitação de ambiente restante.
