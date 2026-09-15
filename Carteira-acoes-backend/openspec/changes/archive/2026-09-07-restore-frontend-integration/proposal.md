## Why

O frontend não consegue consumir a API de forma confiável, bloqueando os fluxos de carteira já disponibilizados pelo backend. É necessário estabelecer e validar o contrato de acesso entre aplicações para que o ambiente de desenvolvimento funcione sem configurações manuais ou falhas do navegador.

## What Changes

- Configurar a API para aceitar, de forma explícita e segura, as origens HTTP do frontend suportadas em desenvolvimento.
- Assegurar que requisições CORS simples e preflight (`OPTIONS`) recebam os cabeçalhos e métodos necessários, inclusive quando a aplicação usar credenciais/autenticação.
- Tornar a origem permitida configurável por ambiente, sem liberar origens arbitrárias em produção.
- Documentar a URL base, o prefixo de API e as variáveis de ambiente que o frontend deve usar.
- Adicionar testes de integração que cubram o acesso do frontend e o bloqueio de uma origem não autorizada.

## Capabilities

### New Capabilities

- `frontend-api-integration`: Define o contrato de CORS e configuração que permite ao frontend autorizado consumir a API.

### Modified Capabilities

Nenhuma.

## Impact

- Configuração de segurança web e perfis de ambiente do backend.
- Endpoints HTTP públicos e respostas a preflight da API.
- Testes de integração e documentação de execução/variáveis para o frontend.
