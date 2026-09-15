# Deploy Agent

## Scope

Este diretório é responsável exclusivamente pela infraestrutura de execução e deploy da aplicação Carteira Ações.

## Services

A stack atual contém:

- frontend Angular servido por Nginx
- backend Spring Boot
- PostgreSQL 17

Todos os serviços devem compartilhar a rede:

`carteira-acoes-network`

## Database

O banco de deploy utiliza o volume:

`carteira-acoes-deploy-data`

Esse volume é independente do volume local antigo:

`postgres-data`

MUST NOT reutilizar, renomear ou remover `postgres-data`.

MUST NOT executar:

`docker compose down -v`

sem solicitação explícita do usuário.

## Backend

O backend deve acessar o PostgreSQL internamente usando:

`jdbc:postgresql://postgres:5432/${POSTGRES_DB}`

Containers MUST NOT utilizar `localhost` para comunicação entre serviços.

## Frontend

O frontend deve ser servido por Nginx.

Chamadas de produção ao backend devem usar:

`/api`

O Nginx deve encaminhar `/api/` para:

`http://backend:8080/`

## Ports

Durante desenvolvimento/deploy local:

- frontend: host `80`
- backend: host `8080`
- PostgreSQL: host `5433`

Portas externas podem ser reduzidas em um deploy de produção real.

## Environment

Segredos devem permanecer em `.env`.

`.env` MUST NOT ser commitado.

Um `.env.example` MAY ser versionado sem valores secretos.

## Validation

Após mudanças no deploy, validar:

1. `docker compose config`
2. build dos serviços alterados
3. `docker compose up -d`
4. `docker compose ps`
5. health do PostgreSQL
6. logs do backend
7. acesso ao frontend
8. acesso a `/api/usuarios`

## Updating services

Frontend:

`docker compose build frontend`
`docker compose up -d frontend`

Backend:

`docker compose build backend`
`docker compose up -d backend`

Stack completa:

`docker compose build`
`docker compose up -d`

## Safety

Nunca apagar volumes persistentes como parte de uma atualização normal.

Nunca alterar contratos do backend ou código Angular neste diretório.

Mudanças específicas de aplicação devem ser feitas nos respectivos projetos.
