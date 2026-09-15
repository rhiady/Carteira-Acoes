## MODIFIED Requirements

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
