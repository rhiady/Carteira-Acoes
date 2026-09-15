## Context

O README atual contém somente configuração e inicialização resumidas. As informações necessárias estão distribuídas entre os recursos REST, classes de integração, propriedades de ambiente e testes. A melhoria será documental; consulte `proposal.md` para a motivação.

## Goals / Non-Goals

**Goals:**

- Oferecer uma referência única para executar, testar e consumir a API localmente.
- Documentar contratos de entrada de ações e corretoras, endpoints e erros padronizados sem expor segredos.
- Registrar finalidade, configuração e limitações operacionais de cada integração externa.

**Non-Goals:**

- Alterar a implementação, endpoints, esquema de banco ou credenciais.
- Duplicar a documentação OpenAPI completa no README.

## Decisions

### README organizado por jornada de uso

O documento seguirá a ordem: visão geral e arquitetura, pré-requisitos/configuração, execução/testes, uso da API e integrações. Essa sequência permite que uma pessoa configure o projeto antes de consultar os contratos. Uma lista sem seções foi descartada por dificultar localização e manutenção.

### Exemplos seguros e aderentes aos contratos atuais

Exemplos de variáveis usarão placeholders e jamais valores reais. Exemplos HTTP refletirão os DTOs atuais, incluindo ação por ticker e mercado e corretora somente por CNPJ. Copiar respostas completas foi descartado para evitar documentação extensa e desatualizada; serão descritos campos e fluxos essenciais.

### Integrações descritas como dependências operacionais

Cada integração informará URL base, autenticação, variável de ambiente, finalidade, limites conhecidos e falhas esperadas. A documentação apontará para as fontes oficiais quando detalhes do fornecedor forem necessários, evitando reproduzir material volátil no repositório.

## Risks / Trade-offs

- [README divergir da API] → conferir endpoints, DTOs e propriedades antes da edição e manter exemplos pequenos.
- [Exposição acidental de segredos] → usar somente placeholders e reiterar que `.env` não é versionado.
- [Informação de fornecedor ficar desatualizada] → indicar documentação oficial e descrever apenas o comportamento relevante ao projeto.

## Migration Plan

1. Substituir o README resumido pelo conteúdo estruturado.
2. Conferir comandos e exemplos contra o projeto local.
3. Revisar o diff para garantir a ausência de credenciais antes da entrega.

O rollback consiste em restaurar a versão anterior do README.
