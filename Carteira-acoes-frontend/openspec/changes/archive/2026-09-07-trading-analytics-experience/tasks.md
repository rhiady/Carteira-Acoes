## 1. Preparação

- [x] 1.1 Inventariar Compra, Venda, Analytics, rotas, services, models e componentes reutilizáveis.
- [x] 1.2 Confirmar contratos de compra/venda, posições, resumo e histórico sem inventar endpoints.
- [x] 1.3 Definir tokens e estrutura Swiss/financial editorial com foco em transação e leitura financeira.

## 2. Compra e Venda

- [x] 2.1 Redesenhar Compra em seções de ativo, carteira, ordem, resumo e confirmação.
- [x] 2.2 Redesenhar Venda com posição, quantidade disponível, preço médio e cotação quando disponíveis.
- [x] 2.3 Implementar resumo estimado reativo e null safety para quantidade/preço.
- [x] 2.4 Revisar validações, modo exclusivo de identificação da compra e mensagens junto aos campos.
- [x] 2.5 Garantir loading, prevenção de dupla submissão, confirmação, sucesso e preservação em erro.
- [x] 2.6 Validar Compra e Venda em mobile e por teclado.

## 3. Analytics

- [x] 3.1 Criar/refatorar rota e seletor de carteira com loading durante troca.
- [x] 3.2 Implementar resumo de valor, total, realizado, não realizado e quantidade por moeda.
- [x] 3.3 Implementar comparação preço médio versus cotação e variação percentual segura.
- [x] 3.4 Implementar composição por ativo e resultado por ativo com alternativas textuais.
- [x] 3.5 Implementar rankings apenas com conjunto completo e estados de dados insuficientes.
- [x] 3.6 Integrar histórico real e referência horizontal `Preço médio` quando houver posição correspondente.
- [x] 3.7 Revisar tooltips, legendas, empty states e comportamento mobile dos gráficos.

## 4. Qualidade e runtime

- [x] 4.1 Adicionar testes de resumo, validação, duplicidade, nulos, moedas e agregações.
- [x] 4.2 Executar testes e build de produção.
- [x] 4.3 Validar Compra, Venda e Analytics em runtime com dados reais/fixtures confirmadas.
- [x] 4.4 Verificar desktop/mobile, teclado, foco, contraste, reduced motion e AXE.
- [x] 4.5 Corrigir inconsistências e revisar ausência de dados fictícios antes de concluir.
