## MODIFIED Requirements

### Requirement: Cadastro de corretora validada
O sistema SHALL cadastrar uma corretora somente após validar o CNPJ e um registro ativo correspondente na fonte da CVM. A comparação de CNPJ MUST ignorar caracteres não numéricos e aceitar a representação equivalente retornada pela fonte. O cadastro SHALL receber somente CNPJ. O CNPJ MUST ser único; a corretora MUST incluir os dados cadastrais e de endereço retornados pela consulta de CNPJ.

#### Scenario: Corretora validada com sucesso
- **WHEN** um cliente solicita cadastro com CNPJ válido e a fonte da CVM confirma um registro ativo para o mesmo CNPJ
- **THEN** o sistema persiste a corretora e retorna seus dados cadastrais

#### Scenario: CNPJ formatado de forma diferente pela fonte
- **WHEN** a fonte da CVM confirma um registro ativo cujo CNPJ possui pontuação ou outro formato equivalente ao CNPJ solicitado
- **THEN** o sistema reconhece a corretora como registrada e conclui o cadastro

#### Scenario: Corretora não registrada na CVM
- **WHEN** os dados de CNPJ são válidos mas não há um registro ativo correspondente na CVM
- **THEN** o sistema responde 422 com o erro de corretora não registrada na CVM e não persiste a corretora

### Requirement: Falhas de validação externa
O sistema SHALL não persistir uma corretora quando uma fonte obrigatória de CNPJ ou CVM falhar, estiver indisponível ou retornar dados que não possam comprovar o registro da corretora. A resposta MUST comunicar uma falha padronizada apropriada e MUST NOT classificar uma resposta externa inválida ou incompatível como ausência de registro na CVM.

#### Scenario: Indisponibilidade da validação
- **WHEN** a fonte obrigatória não responde dentro do limite configurado
- **THEN** o sistema retorna erro de integração e não cria a corretora

#### Scenario: Resposta CVM não interpretável
- **WHEN** a fonte da CVM responde sem os dados necessários para identificar o CNPJ ou determinar a validade do registro
- **THEN** o sistema retorna erro de integração e não cria a corretora
