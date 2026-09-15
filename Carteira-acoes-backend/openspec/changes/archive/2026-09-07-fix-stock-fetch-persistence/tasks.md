## 1. Importação transacional de ações

- [x] 1.1 Ajustar o fluxo de compra por ticker para consultar e validar uma única cotação antes de persistir a ação importada.
- [x] 1.2 Persistir a ação importada e seu histórico inicial na transação da compra, normalizando a data/hora para UTC e evitando transações independentes nesse caminho.
- [x] 1.3 Garantir que qualquer erro de provedor, validação ou persistência reverta ação, histórico, posição e operação criados pela compra.

## 2. Testes e validação

- [x] 2.1 Ampliar os testes de serviço para validar os dados de cotação e histórico na importação inicial.
- [x] 2.2 Ampliar os testes de integração de compra por ticker para verificar ação, histórico, posição e operação persistidos após sucesso.
- [x] 2.3 Adicionar cenários de falha que comprovem a ausência de registros parciais e executar a suíte de testes afetada.
