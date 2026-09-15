## Context

O frontend Angular está em um repositório irmão e consome a API Spring Boot deste repositório. O perfil `dev` usa JDBC PostgreSQL configurado por `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`, hoje com valor padrão para `localhost`. Não há Dockerfile ou Compose para a aplicação completa. Consulte `proposal.md` para a motivação.

## Goals / Non-Goals

**Goals:**

- Fornecer um único comando Docker Compose que inicie frontend, API, banco e migrations Flyway em um ambiente novo e reproduzível.
- Persistir apenas os dados PostgreSQL em volume nomeado, sem tocar na instância PostgreSQL da máquina anfitriã.
- Manter o acesso local ao frontend e à API por portas documentadas e permitir configuração por variáveis sem versionar segredos.

**Non-Goals:**

- Criar imagens publicadas, configuração de produção com TLS/reverse proxy ou adicionar CI/CD.
- Migrar dados do PostgreSQL local existente para o volume Docker.
- Alterar o perfil `test`, endpoints ou regras de domínio.

## Decisions

### Orquestrar frontend, API e PostgreSQL com Docker Compose

O Compose definirá serviços `frontend`, `app` e `postgres`. O serviço `app` será construído por Dockerfile multiestágio com Java 21 e executará o artefato Spring Boot no perfil `dev`; `postgres` usará uma imagem oficial PostgreSQL compatível e healthcheck. `frontend` será compilado com a versão Node compatível com Angular 22 e servido por Nginx. A API aguardará a saúde do banco antes de iniciar.

Alternativa considerada: containerizar apenas o PostgreSQL e executar frontend/API na máquina. Foi descartada porque não dockeriza a aplicação inteira nem garante os mesmos runtimes para todos os desenvolvedores.

### Usar os dois repositórios irmãos como contextos de build explícitos

O Compose ficará em um ponto de entrada documentado que consiga referenciar os diretórios irmãos `Carteira-acoes-backend` e `Carteira-acoes-frontend`. Cada serviço terá seu contexto de build próprio; os Dockerfiles e `.dockerignore` correspondentes permanecerão junto do repositório que constroem. Assim, o build não depende de copiar código entre repositórios.

Alternativa considerada: transformar os repositórios em monorepo. Foi descartada por ampliar o escopo da dockerização e alterar a organização de código existente.

### Conectar por rede interna e isolar persistência em volume nomeado

O Compose injetará `DB_URL=jdbc:postgresql://postgres:5432/carteira_acoes` no serviço `app`; `postgres` é o hostname interno do serviço. As credenciais e o nome de banco serão parametrizados pelo ambiente do Compose, com valores locais de desenvolvimento documentados. O volume nomeado `postgres_data` será montado em `/var/lib/postgresql/data` e não apontará para diretórios ou volumes preexistentes.

O banco não terá porta publicada por padrão; a API será publicada em `8080` e o frontend em `4200`. Administração do banco ocorrerá por `docker compose exec` ou por uma substituição local explícita quando necessária, evitando conflito com PostgreSQL instalado no host.

### Configurar comunicação browser–API pela URL pública

O build do frontend receberá a URL pública da API, com padrão `http://localhost:8080`, pois as requisições são feitas pelo navegador do host e não pela rede interna do Compose. O serviço `app` configurará `CORS_ALLOWED_ORIGINS=http://localhost:4200` (substituível por variável) para aceitar essa origem. O frontend não receberá credenciais de banco ou chaves de provedores no bundle.

Alternativa considerada: proxy reverso único para frontend e API. Foi descartada para manter a execução de desenvolvimento simples e as portas atuais visíveis; pode ser avaliada em uma futura configuração de produção.

### Separar segredos, contexto de build e documentação operacional

`.env` continuará ignorado pelo Git e `.env.example` listará as variáveis necessárias sem valores secretos. `.dockerignore` excluirá artefatos, logs e arquivos de ambiente do contexto de build. O README documentará `docker compose up --build`, logs, encerramento preservando dados e remoção intencional do volume.

## Risks / Trade-offs

- [As portas 4200 ou 8080 já estão ocupadas no host] → documentar a substituição das portas publicadas no Compose e da origem CORS correspondente.
- [Usuário executa `down -v` e perde os dados do ambiente] → documentar que esse comando remove intencionalmente o volume e exigir ação explícita para limpar dados.
- [Variáveis ausentes ou credenciais divergentes entre serviços] → manter nomes únicos no `.env.example` e usar os mesmos valores para `POSTGRES_*` e `DB_*` no Compose.
- [A imagem da aplicação fica maior ou build demora mais] → multiestágio para que ferramentas de compilação não sejam incluídas no runtime final.
- [URL da API embutida no build do frontend está incorreta] → expor variável de build documentada e verificar uma chamada do navegador após o Compose iniciar.

## Migration Plan

1. Criar o ambiente com `docker compose up --build`; o banco começa vazio no novo volume, o Flyway aplica as migrations existentes e o frontend fica disponível na porta documentada.
2. Desenvolvedores que precisem manter dados locais continuam usando a configuração local fora do Compose; não há cópia automática.
3. Para recomeçar o ambiente Docker, parar os serviços e remover explicitamente o volume nomeado; o próximo início recria o banco e reaplica migrations.
