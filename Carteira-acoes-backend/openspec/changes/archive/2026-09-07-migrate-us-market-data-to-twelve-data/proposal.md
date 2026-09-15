## Why

Alpha Vantage será substituída como fonte de dados para ações dos EUA, eliminando sua configuração e as consultas separadas de cotação e perfil. Twelve Data passa a atender a consulta necessária para cadastro e atualização de preços sem alterar os contratos públicos da aplicação.

## What Changes

- Substituir Alpha Vantage por Twelve Data na obtenção de cotação e nome de empresa de ações do mercado EUA.
- Usar a credencial e a URL configuráveis do Twelve Data, removendo a dependência de `ALPHA_VANTAGE_URL` e `ALPHA_VANTAGE_API_KEY`.
- Preservar a Brapi como provedor exclusivo do mercado Brasil e manter os endpoints e modelos de resposta da API.
- Classificar as respostas e falhas do Twelve Data nas categorias de erro externo já expostas pela aplicação.

## Capabilities

### New Capabilities

_Nenhuma._

### Modified Capabilities

- `market-data`: a fonte compatível para ações dos EUA passa a ser Twelve Data, com preço e nome de empresa válidos obtidos em uma consulta de cotação.

## Impact

- Clientes Feign, DTOs e adaptadores de integração de mercado dos EUA.
- Seleção de provedor em `DefaultCotacaoAdapter`, telemetria de chamadas externas e testes de integração/unitários.
- Configuração de ambiente e documentação: novas variáveis `TWELVE_DATA_URL` e `TWELVE_DATA_API_KEY`.
