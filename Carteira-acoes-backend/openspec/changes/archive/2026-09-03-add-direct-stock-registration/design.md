## Context

Veja a motivação em `proposal.md`. O serviço de ações já concentra a normalização de ticker, consulta ao provedor, validação de dados, persistência e criação do histórico inicial. Atualmente, somente a compra alcança esse fluxo; o recurso HTTP de ações expõe apenas consultas e atualização de cotação.

## Goals / Non-Goals

**Goals:**

- Oferecer um contrato simples de cadastro por `ticker` e `mercado`.
- Reutilizar a mesma importação validada usada pela compra, incluindo histórico inicial em UTC.
- Diferenciar criação direta de reuso por compra: duplicidade no endpoint direto é conflito.

**Non-Goals:**

- Permitir valores de cotação ou nome informados manualmente pelo cliente.
- Criar automaticamente uma carteira, posição ou operação.
- Alterar o comportamento de uma compra que reutiliza uma ação existente.

## Decisions

### DTO mínimo e resposta REST de criação

O endpoint receberá um DTO validado contendo somente `ticker` não vazio e `mercado`. Ele retornará o DTO de ação existente com HTTP 201 e `Location` para o recurso criado, seguindo o padrão dos demais cadastros da API.

Alternativa considerada: aceitar dados cadastrais e cotação no corpo. Foi descartada para manter o catálogo confiável e consistente com a fonte de mercado.

### Importação compartilhada com semânticas distintas para duplicidade

O serviço extrairá ou reutilizará a rotina de importação para que compra e cadastro direto tenham a mesma normalização, validação, cotação e histórico. A compra continuará retornando a ação existente pelo ticker; o cadastro direto verificará a duplicidade antes da importação e retornará conflito.

Alternativa considerada: fazer o endpoint direto chamar o resolvedor da compra. Foi descartada porque responderia sucesso para um ticker existente, escondendo a tentativa de cadastro duplicado.

### Uma única transação para ação e histórico

O cadastro direto será transacional: persistência da ação e do histórico inicial ocorrerão juntas. Falhas do provedor, validação, unicidade ou persistência não poderão deixar ação ou histórico parcial.

## Risks / Trade-offs

- [Provedor indisponível impede o cadastro] → propagar erro externo padronizado e não criar dados locais estimados.
- [Concorrência em dois cadastros do mesmo ticker] → preservar unicidade no banco e converter violação em conflito padronizado quando aplicável.
- [Ação importada sem compra pode não ter posição] → é comportamento intencional; o catálogo e a carteira permanecem independentes.

## Migration Plan

1. Adicionar o DTO, endpoint e serviço compartilhado, mantendo as migrations atuais.
2. Executar testes de serviço e integração para sucesso, duplicidade e falha externa.
3. Publicar sem migração de dados; clientes podem adotar `POST /acoes` gradualmente.
