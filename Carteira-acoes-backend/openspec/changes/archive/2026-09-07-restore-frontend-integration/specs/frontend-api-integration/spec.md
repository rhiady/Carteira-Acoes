## Purpose

Permitir que um frontend autorizado consuma a API HTTP em cada ambiente, com CORS previsível, seguro e verificável pelo navegador.

## ADDED Requirements

### Requirement: Acesso CORS para frontend autorizado
O sistema SHALL aceitar requisições HTTP originadas pelas origens de frontend explicitamente configuradas para o ambiente. Para uma origem autorizada, o sistema SHALL responder a requisições simples e preflight com os cabeçalhos CORS necessários aos métodos e cabeçalhos públicos da API, incluindo credenciais quando o contrato de autenticação do ambiente as exigir.

#### Scenario: Requisição de origem autorizada
- **WHEN** o frontend usa uma origem configurada e chama um endpoint público da API
- **THEN** a resposta inclui a permissão CORS para essa origem e o navegador pode disponibilizar a resposta ao frontend

#### Scenario: Preflight de operação da API
- **WHEN** o navegador envia uma requisição `OPTIONS` antes de uma operação permitida a partir de uma origem autorizada
- **THEN** o sistema retorna uma resposta de preflight bem-sucedida com os métodos e cabeçalhos permitidos aplicáveis

### Requirement: Isolamento de origens não autorizadas
O sistema MUST NOT conceder permissão CORS a uma origem que não esteja explicitamente configurada no ambiente, e MUST NOT usar uma origem curinga quando o acesso com credenciais estiver habilitado.

#### Scenario: Origem não autorizada
- **WHEN** um navegador envia uma requisição com cabeçalho `Origin` diferente das origens configuradas
- **THEN** a resposta não concede permissão CORS a essa origem

### Requirement: Contrato de configuração do consumidor HTTP
O sistema SHALL disponibilizar documentação de execução que informe a URL base e o prefixo da API, a variável de ambiente usada para configurar as origens autorizadas e os valores necessários para o ambiente de desenvolvimento. A configuração de produção SHALL ser fornecida por ambiente e MUST NOT exigir alteração de código para trocar uma origem autorizada.

#### Scenario: Configuração de desenvolvimento documentada
- **WHEN** uma pessoa configura o frontend para o ambiente de desenvolvimento seguindo a documentação
- **THEN** ela consegue apontar o cliente HTTP para a API e configurar uma origem autorizada sem inferir valores do código-fonte
