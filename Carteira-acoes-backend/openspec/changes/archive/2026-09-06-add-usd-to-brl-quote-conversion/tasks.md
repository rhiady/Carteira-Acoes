## 1. Integração de câmbio

- [x] 1.1 Adicionar configuração e cliente HTTP para consultar a cotação de venda USD/BRL da PTAX.
- [x] 1.2 Criar o modelo e serviço de conversão decimal que obtenham a taxa mais recente disponível e exponham seu instante de referência.
- [x] 1.3 Tratar taxa ausente, resposta inválida e indisponibilidade da PTAX sem produzir valor convertido estimado.

## 2. Contratos e respostas de mercado

- [x] 2.1 Adicionar campos opcionais de cotação em BRL, taxa e data/hora de câmbio à resposta de ação.
- [x] 2.2 Enriquecer somente respostas de ações do mercado EUA com os campos de conversão, preservando a cotação e moeda originais.
- [x] 2.3 Adicionar campos opcionais de cotação e valor atual em BRL à resposta de posição e calcular esses campos para ativos EUA.

## 3. Cobertura e documentação

- [x] 3.1 Testar consulta de taxa, uso da taxa mais recente e falhas da PTAX.
- [x] 3.2 Testar respostas de ação e posição EUA com conversão, sem conversão para BRASIL e com fallback de campos ausentes.
- [x] 3.3 Documentar os novos campos e a referência PTAX no README.
- [x] 3.4 Executar a suíte de testes relevante e validar a mudança OpenSpec.
