## 1. Diagnóstico de persistência

- [x] 1.1 Reproduzir compras e vendas pelos endpoints HTTP com dados mínimos válidos e identificar o ponto que impede a persistência relatada.
- [x] 1.2 Verificar validações de payload, resolução de carteira/ação, cotação opcional, restrições de banco e limites transacionais envolvidos no cenário reproduzido.

## 2. Correção do fluxo

- [x] 2.1 Aplicar a correção mínima para que compras válidas persistam operação e posição e retornem HTTP 201 com identificador consultável.
- [x] 2.2 Aplicar a correção mínima para que vendas válidas persistam operação e posição atualizada e retornem HTTP 201 com identificador consultável.
- [x] 2.3 Padronizar o erro do impedimento identificado, preservando rollback completo e sem retornar sucesso para operações não persistidas.

## 3. Cobertura e validação

- [x] 3.1 Adicionar testes de integração HTTP para compra e venda bem-sucedidas, verificando operação e posição gravadas após a resposta.
- [x] 3.2 Adicionar testes para o cenário de falha reproduzido e para regressões de validação, cotação e atomicidade.
- [x] 3.3 Documentar, se necessário, o payload e os códigos de erro ajustados e executar os testes relevantes e a validação OpenSpec.
