# broker-validation Specification

## Purpose

Garantir que corretoras armazenadas sejam instituições identificáveis e validadas em fontes públicas cadastrais antes de integrarem o sistema.
## Requirements
### Requirement: Cadastro de corretora validada
O sistema SHALL cadastrar uma corretora somente após validar CNPJ e registro válido na fonte da CVM. O cadastro SHALL receber somente CNPJ. O CNPJ MUST ser único e, para persistência, apenas o CNPJ e a razão social (nome da instituição) MUST estar presentes. Os demais dados cadastrais e de endereço retornados pela consulta de CNPJ MUST ser opcionais e podem ser persistidos como ausentes.

#### Scenario: Corretora validada com sucesso
- **WHEN** um cliente solicita cadastro com CNPJ válido e a fonte da CVM confirma o registro
- **THEN** o sistema persiste a corretora e retorna seus dados cadastrais disponíveis

#### Scenario: Corretora validada com dados complementares ausentes
- **WHEN** um cliente solicita cadastro com CNPJ válido, a fonte da CVM confirma o registro e a consulta de CNPJ não retorna um ou mais campos cadastrais ou de endereço complementares
- **THEN** o sistema persiste a corretora com CNPJ e razão social e retorna `null` nos campos complementares ausentes

#### Scenario: Corretora sem razão social
- **WHEN** um cliente solicita cadastro com CNPJ válido e a consulta de CNPJ não retorna razão social
- **THEN** o sistema responde erro padronizado de validação e não persiste a corretora

#### Scenario: Corretora não registrada na CVM
- **WHEN** os dados de CNPJ são válidos mas não há registro válido na CVM
- **THEN** o sistema responde 422 e não persiste a corretora

### Requirement: Consulta de corretoras
O sistema SHALL permitir listar corretoras paginadamente e recuperá-las por identificador ou CNPJ.

#### Scenario: Consulta por CNPJ existente
- **WHEN** um cliente consulta um CNPJ de corretora persistida
- **THEN** o sistema retorna a corretora correspondente

#### Scenario: Consulta inexistente
- **WHEN** um cliente consulta identificador ou CNPJ não persistido
- **THEN** o sistema retorna erro padronizado de recurso não encontrado

### Requirement: Falhas de validação externa
O sistema SHALL não persistir uma corretora quando uma fonte obrigatória de CNPJ ou CVM falhar, estiver indisponível ou retornar dados não encontrados; a resposta MUST comunicar uma falha padronizada apropriada.

#### Scenario: Indisponibilidade da validação
- **WHEN** a fonte obrigatória não responde dentro do limite configurado
- **THEN** o sistema retorna erro de integração e não cria a corretora

### Requirement: Erros de corretora coerentes
Falhas de duplicidade, validação cadastral, inexistência e indisponibilidade de provedor de corretora SHALL ser mapeadas a status HTTP coerentes e ao corpo padronizado de erro, sem virar HTTP 500 quando forem condições conhecidas.

#### Scenario: Corretora duplicada
- **WHEN** o cliente tenta cadastrar CNPJ já persistido
- **THEN** a API responde HTTP 409 e preserva o cadastro existente
