## Context

Veja a motivação em `proposal.md`. O backend já expõe endpoints HTTP para o domínio de carteira, mas o navegador aplica CORS antes de tornar a resposta disponível ao frontend. A solução precisa preservar a segurança entre ambientes e não transformar a liberação de desenvolvimento em acesso irrestrito.

## Goals / Non-Goals

**Goals:**

- Centralizar a política CORS da API e aplicar a mesma regra aos endpoints e preflights.
- Externalizar as origens autorizadas para configuração por ambiente.
- Tornar a configuração necessária ao frontend reproduzível e coberta por testes de integração.

**Non-Goals:**

- Alterar regras de negócio, modelos de dados ou os contratos funcionais de carteira.
- Implementar autenticação nova ou substituir o cliente HTTP do frontend.
- Liberar acesso de qualquer origem como solução temporária.

## Decisions

### Política CORS única na borda HTTP

A integração será configurada no ponto global de segurança/web da API, para que todos os endpoints e requisições `OPTIONS` sigam a mesma política. Isso evita configurações divergentes por controlador.

Alternativa considerada: anotar cada endpoint individualmente. Foi descartada por ser frágil para endpoints novos e por não tratar o preflight de maneira uniforme.

### Origens como configuração explícita por ambiente

As origens permitidas serão lidas de uma propriedade/variável de ambiente, com valores seguros para desenvolvimento documentados. Produção informará a origem implantada por configuração de ambiente.

Alternativa considerada: codificar a URL do frontend no backend. Foi descartada porque acopla deploys e exige mudança de código para cada ambiente.

### Verificação por testes de integração HTTP

Os testes exercitarão uma requisição de origem permitida, um preflight e uma origem negada, verificando os cabeçalhos observáveis. Eles não dependerão de um servidor de frontend em execução.

Alternativa considerada: validar somente manualmente no navegador. Foi descartada pois não previne regressões de segurança ou configuração.

## Risks / Trade-offs

- [A origem real do frontend divergir da configuração] → Documentar a variável e validar a origem configurada em testes com valores representativos.
- [Credenciais e origem curinga serem combinadas indevidamente] → Usar lista explícita de origens e testar a ausência de permissão para origem desconhecida.
- [Proxy/reverse proxy alterar host, esquema ou prefixo] → Documentar URL base e prefixo da API, e validar no ambiente integrado após o deploy.

## Migration Plan

1. Introduzir a configuração CORS com uma origem de desenvolvimento explicitamente documentada.
2. Adicionar os testes de integração e a documentação do contrato de conexão.
3. Configurar a origem implantada em cada ambiente antes da publicação.
4. Validar no navegador uma chamada real do frontend após o deploy.

Rollback: remover ou restaurar a configuração de origem do ambiente e reimplantar a política anterior; nenhuma migração de dados é necessária.
