## Why

O cadastro de uma corretora ligada ao Bradesco está sendo recusado com `CORRETORA_NAO_REGISTRADA_CVM`, embora a instituição deva ser reconhecida pela fonte regulatória. A validação atual depende de uma correspondência rígida com a lista retornada pela integração, o que pode transformar dados válidos, porém apresentados em formatos ou estados diferentes pela fonte, em um erro de negócio incorreto.

## What Changes

- Tornar a validação de registro de corretora na CVM compatível com os formatos de CNPJ e campos efetivamente fornecidos pela fonte pública.
- Validar explicitamente que o registro correspondente está ativo antes de aceitar o cadastro.
- Diferenciar uma corretora ausente ou inativa de uma falha/contrato incompatível da fonte externa, preservando respostas de erro acionáveis.
- Cobrir com testes o cadastro de uma corretora CVM válida e a rejeição de registros ausentes ou inativos.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `broker-validation`: revisar a validação da fonte CVM para aceitar corretamente corretoras com registro ativo e classificar adequadamente registros não encontrados, inativos e respostas externas inválidas.

## Impact

- `CvmFacade`, DTO e cliente da BrasilAPI, além do serviço de cadastro de corretoras.
- Testes unitários e de integração da validação de corretora.
- O contrato de `POST /corretoras` mantém o payload e o sucesso `201`, mas pode retornar uma classificação de erro mais precisa quando a consulta CVM não puder ser interpretada.
