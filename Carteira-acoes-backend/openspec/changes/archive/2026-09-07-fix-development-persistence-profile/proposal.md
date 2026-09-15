## Why

Os cadastros parecem não persistir porque a aplicação inicia pelo perfil `test`, que usa H2 em memória, em vez do PostgreSQL configurado para desenvolvimento. Os dados desaparecem ao reiniciar a aplicação e não ficam visíveis no banco local esperado.

## What Changes

- Separar a configuração de execução local da configuração exclusiva de testes, impedindo que o perfil de teste seja ativado como padrão da aplicação.
- Garantir que uma execução de desenvolvimento use a fonte PostgreSQL configurada por variáveis de ambiente e aplique as migrations existentes.
- Manter a suíte automatizada isolada no perfil H2 de teste, sem depender do banco de desenvolvimento.
- Documentar de forma consistente o perfil e a URL de banco esperados para execução local.

## Capabilities

### New Capabilities

_Nenhuma._

### Modified Capabilities

- `platform-foundation`: define a seleção segura e previsível dos perfis de desenvolvimento e teste para que cadastros locais sejam persistidos no banco configurado.

## Impact

Afeta os arquivos de configuração Spring, o comando de execução local e a documentação de ambiente. Não altera endpoints, contratos de cadastro ou o esquema de banco.
