## Context

O repositório contém o esqueleto de uma aplicação Spring Boot com JPA, validação, web MVC e perfis de configuração, mas ainda não possui domínio, migrations ou APIs de negócio. A especificação em `openspec/specs/Spec Final — Sistema de Acompanhamento de Carteira de Ações.md` é a fonte funcional; `proposal.md` define as capacidades que a tornam implementável.

## Goals / Non-Goals

**Goals:**

- Implementar uma API REST em camadas Resource → Service → Repository para o domínio de carteira.
- Garantir consistência entre histórico de operações e posição atual mesmo sob concorrência ou falha externa.
- Isolar provedores externos, persistência e transporte para permitir testes determinísticos.

**Non-Goals:**

- Autenticação, autorização, execução de ordens, movimentação financeira, integração bancária e ativos fora de ações.
- Alteração ou exclusão genérica do histórico de operações.

## Decisions

### Modelo separado para histórico e posição

`Operacao` será o registro imutável de cada compra ou venda e `CarteiraAcao` será a projeção persistida da quantidade atual, única por carteira e ação. A operação será criada e a posição atualizada na mesma transação; a posição usa versionamento otimista. Isso permite consulta eficiente de posições e histórico auditável. Alternativa considerada: calcular a posição sempre a partir do histórico; foi descartada por aumentar custo e complexidade de leitura e concorrência.

### Limites claros entre API, domínio e infraestrutura

Resources recebem e devolvem DTOs, Services aplicam regras e transações, Repositories cuidam da persistência e Mappers fazem conversão. Cotações e metadados de empresa serão acessados por `CotacaoAdapter`; CNPJ e CVM por facades. O cadastro de ação recebe ticker e mercado, importando nome e cotação; o cadastro de corretora recebe somente CNPJ, importando os dados cadastrais e o endereço retornados pela fonte de CNPJ e exigindo registro CVM válido. Clientes HTTP ficam atrás desses componentes e falhas são convertidas para exceções de domínio. Isso evita acoplamento dos casos de uso ao formato de um fornecedor. Alternativa: chamar clientes diretamente nos serviços; foi descartada por reduzir testabilidade e tornar troca de provedor arriscada.

### Persistência controlada por migrations

Flyway será a única fonte de evolução de schema; Hibernate valida os mappings. Colunas financeiras usam decimal de precisão 19,4, IDs usam `Long`, relações são LAZY e constraints/índices exigidos são criados no banco. Alternativa: geração automática de schema; foi descartada por não permitir evolução confiável entre ambientes.

### Política de cotação e falhas externas

Compra com preço fornecido não consulta mercado; compra sem preço e toda venda exigem cotação bem-sucedida. Timeouts e erros HTTP serão classificados, retries ocorrerão apenas para falhas transitórias e nenhum preço artificial será usado. A transação de operação só começa a persistir após as validações e a cotação exigida estarem disponíveis, reduzindo o risco de rollback por chamadas remotas.

### Contratos HTTP previsíveis

Listas usarão paginação e histórico será decrescente por data/hora. Um manipulador global transforma exceções em um único formato de erro. Validação de entrada fica nos DTOs; regras que dependem do estado persistido ficam nos serviços.

## Risks / Trade-offs

- [Disponibilidade e limites de APIs externas] → timeouts curtos, classificação de erros, retry restrito e testes com dublês.
- [Conflito em operações simultâneas] → `@Version`, constraint única carteira-ação e transações atômicas; o cliente recebe falha controlada para nova tentativa.
- [Diferenças entre H2 e banco de produção] → migrations testadas em ambos os perfis e evitar SQL específico quando possível.
- [Spec ampla em uma única entrega] → implementar e testar por capacidade, preservando os contratos do OpenSpec em cada etapa.

## Migration Plan

1. Adicionar dependências e configuração compartilhada, dev e test.
2. Criar migrations versionadas para tabelas e índices; iniciar com `ddl-auto=validate`.
3. Entregar o catálogo e as validações de corretora, seguido dos adapters/facades de integração.
4. Entregar operações transacionais e testes de concorrência, rollback e contratos HTTP.
5. Publicar `.env.example` e README de integrações antes da configuração em ambiente real.

Em caso de falha de implantação, reverter o artefato da aplicação e aplicar uma nova migration corretiva; migrations aplicadas nunca serão modificadas ou removidas.
