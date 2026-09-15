## Why

O projeto já possui Dockerfiles para backend e frontend e um Compose em `deploy`, mas a configuração atual diverge do contrato documentado de desenvolvimento. Alinhar esses arquivos evita conexões incorretas, exposição desnecessária do PostgreSQL e diferenças de portas entre o ambiente executado e o ambiente esperado.

## What Changes

- Alinhar o Compose em `deploy` aos serviços `frontend`, `app` e `postgres`.
- Publicar frontend em 4200 e API em 8080, sem publicar a porta do PostgreSQL no host.
- Usar o volume nomeado `postgres_data` e healthcheck/dependência para inicialização ordenada.
- Configurar `DB_URL`, credenciais, perfil `dev`, CORS e URL pública da API por ambiente.
- Adicionar ou ajustar `.dockerignore`, `.env.example` e documentação operacional.
- Validar build, migrations, comunicação browser–API e persistência após reinício.

## Capabilities

### New Capabilities

<!-- Nenhuma capacidade nova. -->

### Modified Capabilities

- `platform-foundation`: tornar o ambiente Docker Compose uma forma documentada e reproduzível de executar o perfil `dev` com PostgreSQL persistente e credenciais externas.

## Impact

- `deploy/docker-compose.yml`, Dockerfiles e arquivos de ambiente/documentação dos repositórios irmãos.
- Inicialização local via Docker Compose e portas utilizadas pelo frontend/API.
- Nenhum endpoint ou regra de domínio será alterado.
