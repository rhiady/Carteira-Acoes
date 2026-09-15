## Context

Veja a motivação em `proposal.md`. A configuração-base ativa explicitamente `test`, fazendo a execução normal selecionar H2 em memória. Ao mesmo tempo, o ambiente local disponível usa o banco PostgreSQL `cotacao`; os dados criados no H2 não permanecem após o processo terminar nem aparecem nesse banco.

## Goals / Non-Goals

**Goals:**

- Tornar `dev` o perfil padrão para iniciar a API fora da suíte automatizada.
- Concentrar a configuração PostgreSQL no perfil de desenvolvimento e alinhá-la ao banco local documentado.
- Manter testes explicitamente isolados com H2.

**Non-Goals:**

- Criar, apagar ou migrar dados de negócio existentes.
- Alterar entidades, repositórios, endpoints ou contratos de cadastro.
- Embutir credenciais de banco no repositório.

## Decisions

### Perfil de desenvolvimento como padrão, teste somente explícito

A configuração-base definirá `dev` como perfil padrão, sem ativar `test`. Os testes continuarão a selecionar `test` por sua configuração própria, e operadores poderão sobrescrever o perfil pela variável `SPRING_PROFILES_ACTIVE`.

Alternativa considerada: não definir perfil padrão. Foi descartada porque a execução local sem variáveis voltaria a depender de propriedades-base ambíguas e poderia não carregar a configuração de desenvolvimento.

### Fonte de dados de desenvolvimento parametrizada

O perfil `dev` manterá URL, usuário e senha recebidos por ambiente; o valor padrão da URL será o banco PostgreSQL local identificado para a aplicação. A documentação refletirá o mesmo alvo e instruirá a configurar explicitamente o ambiente quando outro banco for usado.

Alternativa considerada: usar H2 em arquivo como padrão. Foi descartada porque diverge do PostgreSQL usado em desenvolvimento e não valida as migrations no banco alvo.

### Verificação por perfil e persistência após reinício

Os testes de configuração verificarão que o perfil de teste continua selecionando H2. A validação manual/documentada de desenvolvimento criará um registro no PostgreSQL, reiniciará a aplicação e o consultará novamente.

## Risks / Trade-offs

- [A URL padrão pode não existir na máquina de outro desenvolvedor] → permitir sobrescrita por `DB_URL` e documentar o comando de inicialização.
- [Execução sem PostgreSQL falha ao iniciar] → comportamento explícito e preferível a aceitar cadastros efêmeros sem informar o operador.
- [Configuração de teste herda propriedades de desenvolvimento] → manter o perfil `test` com URL e driver H2 completos e cobertura automatizada.

## Migration Plan

1. Atualizar as configurações e a documentação de execução local.
2. Executar testes no perfil `test` e iniciar a API sem perfil ativo para confirmar a seleção de `dev`.
3. Se for necessário retornar temporariamente a H2, iniciar com `SPRING_PROFILES_ACTIVE=test`; não há migração ou rollback de dados necessário.
