## 1. Contrato de operações

- [x] 1.1 Atualizar o DTO de resposta de operação para expor ticker, nome da empresa, mercado e moeda da ação.
- [x] 1.2 Ajustar o mapeamento/listagem de `GET /operacoes/carteiras/{carteiraId}` para retornar uma página com os dados enriquecidos em `content`.
- [x] 1.3 Garantir que o carregamento das operações inclua os dados da ação sem gerar consultas extras por item quando possível.

## 2. Contrato de posições

- [x] 2.1 Alterar o cálculo/DTO de posição para retornar `precoMedioVenda` como `null` quando não houver venda registrada.
- [x] 2.2 Preservar o cálculo ponderado de `precoMedioVenda` quando existirem vendas.
- [x] 2.3 Confirmar que `lucroPrejuizo`, realizado e não realizado continuam sendo calculados pelo backend e expostos sem mudança semântica.

## 3. Frontend e integração

- [ ] 3.1 Ajustar a tela de Operações para consumir o array em `response.content` e renderizar os campos enriquecidos do ativo.
- [ ] 3.2 Ajustar a exibição de posição para tratar `precoMedioVenda: null` como não aplicável.
- [ ] 3.3 Garantir que a cor de lucro/prejuízo use somente `lucroPrejuizo`: negativo vermelho, positivo verde e zero neutro.

## 4. Validação

- [x] 4.1 Adicionar ou atualizar testes de integração para verificar que o histórico de operações por carteira é paginado e contém os dados enriquecidos da ação.
- [x] 4.2 Adicionar ou atualizar testes para `precoMedioVenda = null` sem vendas e média ponderada quando existirem vendas.
- [x] 4.3 Executar os testes relevantes e `openspec validate "fix-operations-report-contract" --strict`.
