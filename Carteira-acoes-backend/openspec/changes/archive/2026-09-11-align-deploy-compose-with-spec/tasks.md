## 1. Compose e imagens

- [x] 1.1 Ajustar `deploy/docker-compose.yml` para os serviços `frontend`, `backend` e `postgres`.
- [x] 1.2 Alinhar contextos de build aos Dockerfiles multiestágio dos repositórios backend e frontend.
- [x] 1.3 Configurar healthcheck do PostgreSQL e dependência de inicialização da API.
- [x] 1.4 Configurar volume `carteira-acoes-deploy-data` e publicar o PostgreSQL na porta 5433.

## 2. Configuração

- [x] 2.1 Configurar perfil `dev`, URL JDBC interna, credenciais e nome do banco via variáveis.
- [x] 2.2 Ajustar `.env.example` sem incluir segredos reais.
- [x] 2.3 Configurar frontend na porta 80, API na 8080, proxy `/api/` e CORS para `http://localhost`.
- [x] 2.4 Adicionar `.dockerignore` adequado ao backend e conferir o do frontend.

## 3. Documentação e verificação

- [x] 3.1 Documentar execução, logs, encerramento, volume e remoção intencional de dados.
- [x] 3.2 Executar build em ambiente limpo e verificar healthcheck, migrations e portas.
- [x] 3.3 Verificar chamada browser–API e persistência após reiniciar os serviços.
- [x] 3.4 Validar as specs e marcar as tarefas somente após as evidências correspondentes.
