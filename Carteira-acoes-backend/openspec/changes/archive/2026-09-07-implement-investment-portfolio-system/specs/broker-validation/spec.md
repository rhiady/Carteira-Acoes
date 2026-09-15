## Purpose

Garantir que corretoras armazenadas sejam instituições identificáveis e validadas em fontes públicas cadastrais antes de integrarem o sistema.

## ADDED Requirements

### Requirement: Cadastro de corretora validada
O sistema SHALL cadastrar uma corretora somente após validar CNPJ e registro válido na fonte da CVM. O cadastro SHALL receber somente CNPJ. O CNPJ MUST ser único; a corretora MUST incluir os dados cadastrais e de endereço retornados pela consulta de CNPJ.

#### Scenario: Corretora validada com sucesso
- **WHEN** um cliente solicita cadastro com CNPJ válido e a fonte da CVM confirma o registro
- **THEN** o sistema persiste a corretora e retorna seus dados cadastrais

#### Scenario: Corretora não registrada na CVM
- **WHEN** os dados de CNPJ e CEP são válidos mas não há registro válido na CVM
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
