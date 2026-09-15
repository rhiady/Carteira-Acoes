## Context

Os Dockerfiles existem nos repositórios irmãos e o Compose atual está em `deploy/docker-compose.yml`, mas usa nomes, portas e volume diferentes do design documentado. A mudança deve alinhar a topologia sem alterar a aplicação ou o perfil de testes.

## Goals / Non-Goals

**Goals:**

- Tornar `deploy/docker-compose.yml` o ponto de entrada reproduzível para frontend, API e PostgreSQL.
- Usar a rede interna para a API acessar `postgres`, mantendo apenas frontend/API expostos ao host.
- Validar saúde, migrations, comunicação e persistência.

**Non-Goals:**

- Migrar dados existentes do PostgreSQL do host.
- Criar infraestrutura de produção, TLS, publicação de imagens ou CI/CD.
- Alterar endpoints, modelo de dados ou regras de negócio.

## Decisions

- Manter os Dockerfiles multiestágio existentes e ajustar somente contextos, variáveis e contratos do Compose quando necessário.
- Manter os serviços como `frontend`, `backend` e `postgres`; usar `depends_on` com healthcheck para evitar corrida entre API e banco.
- Usar o volume nomeado `carteira-acoes-deploy-data` e publicar o PostgreSQL em `5433` para acesso local.
- Expor o frontend em `80`, encaminhar `/api/` pelo Nginx para `backend:8080` e configurar CORS para `http://localhost`.

## Risks / Trade-offs

- [Portas ocupadas] → documentar substituição das portas e atualização de CORS/URL.
- [Volume removido acidentalmente] → documentar `down -v` como operação destrutiva de dados.
- [Contexto de execução incorreto] → documentar execução a partir de `deploy` e validar os caminhos relativos.

## Migration Plan

Executar `docker compose up --build` a partir de `deploy`, validar os serviços e manter a execução local fora do Docker disponível pelas variáveis existentes. Para rollback, restaurar o Compose anterior; o volume Docker permanece independente do banco local.
