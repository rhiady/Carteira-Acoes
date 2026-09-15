## Purpose

Estabelecer as garantias de persistência, configuração, observabilidade e respostas HTTP necessárias para operar o backend de carteira com segurança e previsibilidade.

## ADDED Requirements

### Requirement: Integridade persistente e temporal
O sistema SHALL persistir identificadores como inteiros longos, quantidades e valores financeiros como decimais com precisão adequada, e datas em UTC. O banco MUST assegurar unicidade de e-mail, CNPJ, ticker e par carteira-ação, além das chaves estrangeiras obrigatórias.

#### Scenario: Dado duplicado
- **WHEN** uma requisição tenta persistir e-mail, CNPJ, ticker ou par carteira-ação já existente
- **THEN** o sistema rejeita a operação sem violar a integridade dos dados existentes

### Requirement: Respostas de erro padronizadas
O sistema SHALL retornar falhas em um corpo padronizado contendo ao menos timestamp em UTC, status HTTP, código de erro, mensagem e caminho da requisição. Erros de validação de negócio, recurso não encontrado e integração externa MUST ser distinguíveis pelo cliente.

#### Scenario: Quantidade insuficiente
- **WHEN** uma venda excede a quantidade da posição
- **THEN** o sistema retorna HTTP 422 com corpo padronizado que identifica quantidade insuficiente

### Requirement: Configuração segura por ambiente
O sistema SHALL disponibilizar perfis de desenvolvimento e teste; segredos, credenciais e chaves de provedores MUST ser recebidos por variáveis de ambiente e MUST NOT ser versionados. O ambiente de teste SHALL permitir executar testes sem depender de integrações externas reais.

#### Scenario: Execução de testes
- **WHEN** a suíte automatizada é executada no perfil de teste
- **THEN** ela usa configuração própria e substitui as integrações externas por dublês controlados

### Requirement: Observabilidade e documentação das integrações
O sistema SHALL registrar para chamadas externas apenas provedor, operação, identificador consultado, status, duração e erro; logs MUST NOT conter senha, token, chave de API, cabeçalho de autorização ou credenciais de banco. A documentação do projeto SHALL informar finalidade, URL base, autenticação, variáveis, limites e falhas esperadas de cada integração.

#### Scenario: Registro de chamada externa
- **WHEN** uma consulta a provedor externo é concluída ou falha
- **THEN** o log permite identificar a operação sem revelar segredo algum
