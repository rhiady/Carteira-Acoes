## 1. Imagens e orquestração

- [ ] 1.1 Criar o Dockerfile multiestágio para compilar a aplicação com Java 21 e executar o JAR Spring Boot.
- [ ] 1.2 Criar Dockerfile multiestágio e `.dockerignore` do frontend Angular para gerar os arquivos estáticos e servi-los por Nginx.
- [ ] 1.3 Criar o `docker-compose.yml` com os serviços `frontend`, `app` e `postgres`, contexts de build dos dois repositórios, healthcheck do banco, dependência de inicialização e publicação das portas `4200` e `8080`.
- [ ] 1.4 Criar o volume nomeado `postgres_data` para `/var/lib/postgresql/data`, sem publicar a porta do banco no host.
- [ ] 1.5 Adicionar `.dockerignore` ao backend para excluir `.env`, logs, `target` e demais arquivos que não devem compor a imagem.

## 2. Configuração de desenvolvimento

- [ ] 2.1 Configurar o serviço da API para usar o perfil `dev` e conectar em `jdbc:postgresql://postgres:5432/carteira_acoes` no Compose.
- [ ] 2.2 Atualizar `.env.example` com as variáveis não secretas necessárias para o Compose e valores locais consistentes de banco/usuário.
- [ ] 2.3 Configurar a URL pública da API no build do frontend e `CORS_ALLOWED_ORIGINS` para a origem do frontend em container.
- [ ] 2.4 Preservar a possibilidade de executar o perfil `dev` fora do Docker com `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` explícitos.

## 3. Documentação e verificação

- [ ] 3.1 Documentar nos READMEs o ponto de entrada do Compose, build/inicialização, URL do frontend/API, logs, encerramento preservando dados e remoção intencional do volume.
- [ ] 3.2 Subir o Compose em ambiente limpo e verificar healthcheck, migrations Flyway, frontend na porta 4200, API na porta 8080, chamada browser–API e persistência após reiniciar os serviços.
