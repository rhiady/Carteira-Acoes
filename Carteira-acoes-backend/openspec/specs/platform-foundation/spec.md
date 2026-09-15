# platform-foundation Specification

## Purpose

Estabelecer as garantias de persistência, configuração, observabilidade e respostas HTTP necessárias para operar o backend de carteira com segurança e previsibilidade.
## Requirements
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
O sistema SHALL disponibilizar perfis de desenvolvimento e teste; segredos, credenciais e chaves de provedores MUST ser recebidos por variáveis de ambiente e MUST NOT ser versionados. A execução local da aplicação MUST NOT selecionar o perfil de teste por padrão. Quando o perfil de desenvolvimento for selecionado, inclusive pelo Docker Compose, o sistema SHALL usar a fonte PostgreSQL configurada por ambiente, aplicar as migrations e preservar os dados no volume nomeado `carteira-acoes-deploy-data` entre reinícios. O Compose SHALL iniciar o serviço `backend` somente após o PostgreSQL estar saudável, publicar a API na porta 8080, o frontend na porta 80 e o PostgreSQL na porta 5433. O frontend SHALL encaminhar `/api/` para `http://backend:8080/` e aceitar a origem pública `http://localhost`. O ambiente de teste SHALL usar configuração própria em H2 em memória e permitir executar testes sem depender do banco de desenvolvimento ou de integrações externas reais.

#### Scenario: Execução local de desenvolvimento
- **WHEN** a aplicação é iniciada para desenvolvimento com o perfil `dev` e uma URL PostgreSQL configurada
- **THEN** os cadastros são persistidos no banco PostgreSQL configurado e permanecem disponíveis após reiniciar a aplicação

#### Scenario: Execução de testes
- **WHEN** a suíte automatizada é executada no perfil de teste
- **THEN** ela usa configuração H2 própria e substitui as integrações externas por dublês controlados, sem acessar o banco de desenvolvimento

#### Scenario: Inicialização pelo Compose
- **WHEN** o ambiente é iniciado pelo Docker Compose em uma máquina limpa
- **THEN** o PostgreSQL passa pelo healthcheck antes do `backend` iniciar, as migrations são aplicadas, a API fica disponível na porta 8080 e o frontend na porta 80

#### Scenario: Persistência e exposição configurada
- **WHEN** os serviços são reiniciados sem remover o volume nomeado
- **THEN** os dados do PostgreSQL permanecem disponíveis, o banco é acessível na porta 5433 e o volume usado é `carteira-acoes-deploy-data`

#### Scenario: Persistência e isolamento do banco
- **WHEN** os serviços são reiniciados sem remover o volume nomeado
- **THEN** os dados do PostgreSQL permanecem disponíveis no volume `carteira-acoes-deploy-data` e o banco continua acessível na porta 5433

#### Scenario: Proxy e origem do frontend
- **WHEN** o navegador acessa o frontend em `http://localhost` e solicita um recurso em `/api/`
- **THEN** o Nginx encaminha a solicitação para `http://backend:8080/` e o backend aceita a origem `http://localhost`

### Requirement: Observabilidade e documentação das integrações
O sistema SHALL registrar para chamadas externas apenas provedor, operação, identificador consultado, status, duração e erro; logs MUST NOT conter senha, token, chave de API, cabeçalho de autorização ou credenciais de banco. A documentação do projeto SHALL informar finalidade, URL base, autenticação, variáveis, limites e falhas esperadas de cada integração.

#### Scenario: Registro de chamada externa
- **WHEN** uma consulta a provedor externo é concluída ou falha
- **THEN** o log permite identificar a operação sem revelar segredo algum

### Requirement: Integridade transacional e configuração segura
Operações que gravam operação e posição SHALL possuir boundary transacional; entidades e migrations SHALL manter constraints compatíveis; credenciais e segredos SHALL vir de configuração externa e nunca ser expostos em logs ou erros HTTP.

#### Scenario: Falha após gravação parcial
- **WHEN** uma compra ou venda falha durante a persistência relacionada
- **THEN** nenhuma entidade parcial permanece persistida
