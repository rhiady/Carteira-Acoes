## 1. Integração Twelve Data

- [x] 1.1 Criar o cliente HTTP do Twelve Data para `GET /quote`, configurado por `TWELVE_DATA_URL` e autenticado pelo header `Authorization`.
- [x] 1.2 Criar o DTO mínimo da resposta de cotação e o adaptador do mercado EUA, convertendo `close`, `name` e `timestamp` em `Cotacao` validada em UTC.
- [x] 1.3 Trocar a seleção do provedor EUA no `DefaultCotacaoAdapter` e manter a Brapi inalterada para `BRASIL`.
- [x] 1.4 Remover os clientes, DTOs e adaptador Alpha Vantage que não forem mais referenciados.

## 2. Tratamento de falhas e observabilidade

- [x] 2.1 Registrar a consulta Twelve Data na telemetria existente sem incluir a chave de API.
- [x] 2.2 Mapear resposta ausente ou incompleta, ticker inexistente, autenticação, limite de uso e indisponibilidade para as categorias de erro externas existentes.

## 3. Testes e configuração

- [x] 3.1 Substituir os testes da integração Alpha Vantage por testes do adaptador Twelve Data para resposta válida, nome, preço, timestamp UTC e resposta incompleta.
- [x] 3.2 Atualizar os testes de seleção de mercado e de preservação da cotação persistida diante das falhas do Twelve Data.
- [x] 3.3 Atualizar `.env.example` e README com `TWELVE_DATA_URL` e `TWELVE_DATA_API_KEY`, removendo instruções e variáveis Alpha Vantage.
- [x] 3.4 Executar a suíte de testes e a validação do OpenSpec, corrigindo regressões encontradas.
