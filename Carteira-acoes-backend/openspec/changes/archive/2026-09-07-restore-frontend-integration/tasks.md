## 1. Configuração da integração HTTP

- [x] 1.1 Identificar a camada global de segurança/web que processa as requisições HTTP e adicionar a política CORS única para os endpoints da API e preflights.
- [x] 1.2 Criar a propriedade configurável por ambiente para a lista explícita de origens de frontend autorizadas, com valor de desenvolvimento documentado e sem curinga incompatível com credenciais.
- [x] 1.3 Ajustar a política para expor apenas métodos, cabeçalhos e credenciais necessários ao contrato HTTP da API.

## 2. Verificação automatizada

- [x] 2.1 Adicionar teste de integração para uma requisição de origem autorizada, verificando os cabeçalhos CORS observáveis.
- [x] 2.2 Adicionar teste de integração para preflight `OPTIONS` de uma operação permitida.
- [x] 2.3 Adicionar teste de integração que confirme a ausência de permissão CORS para uma origem não configurada.

## 3. Documentação e validação final

- [x] 3.1 Documentar URL base, prefixo da API, variável de origens autorizadas e exemplo de configuração do frontend para desenvolvimento.
- [ ] 3.2 Executar a suíte de testes relevante e validar no navegador uma chamada do frontend usando a origem configurada.
