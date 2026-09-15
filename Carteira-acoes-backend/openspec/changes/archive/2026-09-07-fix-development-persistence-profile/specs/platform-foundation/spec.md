## MODIFIED Requirements

### Requirement: Configuração segura por ambiente
O sistema SHALL disponibilizar perfis de desenvolvimento e teste; segredos, credenciais e chaves de provedores MUST ser recebidos por variáveis de ambiente e MUST NOT ser versionados. A execução local da aplicação MUST NOT selecionar o perfil de teste por padrão. Quando o perfil de desenvolvimento for selecionado, o sistema SHALL usar a fonte PostgreSQL configurada por ambiente e aplicar as migrations para persistir os cadastros. O ambiente de teste SHALL usar configuração própria em H2 em memória e permitir executar testes sem depender do banco de desenvolvimento ou de integrações externas reais.

#### Scenario: Execução local de desenvolvimento
- **WHEN** a aplicação é iniciada para desenvolvimento com o perfil `dev` e uma URL PostgreSQL configurada
- **THEN** os cadastros são persistidos no banco PostgreSQL configurado e permanecem disponíveis após reiniciar a aplicação

#### Scenario: Execução de testes
- **WHEN** a suíte automatizada é executada no perfil de teste
- **THEN** ela usa configuração H2 própria e substitui as integrações externas por dublês controlados, sem acessar o banco de desenvolvimento
