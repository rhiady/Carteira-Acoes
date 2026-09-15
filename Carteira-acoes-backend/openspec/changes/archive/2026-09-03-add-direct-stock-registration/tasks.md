## 1. Contrato de cadastro direto

- [x] 1.1 Criar o DTO validado com `ticker` e `mercado` para `POST /acoes`.
- [x] 1.2 Adicionar `POST /acoes` com resposta HTTP 201, cabeçalho `Location` e a ação importada.
- [x] 1.3 Atualizar a documentação da API com o corpo e os resultados do cadastro direto.

## 2. Importação e persistência

- [x] 2.1 Extrair ou reutilizar o fluxo de importação de ação para atender compra e cadastro direto sem duplicar consulta, validação ou histórico.
- [x] 2.2 Implementar a semântica de conflito para ticker já cadastrado no endpoint direto, preservando o reuso de ticker pela compra.
- [x] 2.3 Garantir transação única para ação e histórico inicial, sem criar carteira, posição ou operação no cadastro direto.

## 3. Testes e validação

- [x] 3.1 Adicionar testes de serviço para importação direta, normalização e erros de duplicidade ou resposta externa inválida.
- [x] 3.2 Adicionar teste de integração para `POST /acoes`, incluindo status 201, `Location`, histórico e ausência de posição/operação.
- [x] 3.3 Executar a suíte afetada e validar os cenários de falha sem persistência parcial.
