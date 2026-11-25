# Carteira de Ações — Frontend

Interface web Angular para investidores acompanharem carteiras, posições, cotações e operações. O frontend consome exclusivamente a API do backend para dados financeiros e usa Angular Material para a interface.

## Stack

- Angular 22 com componentes standalone e roteamento lazy;
- TypeScript 6 e RxJS;
- Angular Material/CDK;
- ApexCharts para gráficos;
- Vitest para testes unitários;
- Nginx para servir o build em Docker.

## Organização do código

```text
src/app/
├── core/
│   ├── config/         configuração da API
│   ├── guards/         proteção por autenticação e papel
│   ├── interceptors/   JWT e tratamento de erros
│   ├── mappers/        normalização de respostas
│   ├── models/         contratos de domínio
│   ├── services/       acesso à API e estado de sessão
│   └── formatters/     formatação monetária e variação
├── features/           páginas por domínio
└── shared/components/  componentes reutilizáveis
```

Principais áreas: login e cadastro, início/dashboard, ações, carteiras, operações, corretoras e administração de usuários. Rotas administrativas exigem `ROLE_ADMIN`; as demais superfícies financeiras exigem autenticação.

## Pré-requisitos e instalação

Node.js 22 e npm são recomendados.

```bash
npm ci
```

## Desenvolvimento

```bash
npm start
```

Acesse `http://localhost:4200`. A configuração local usa `apiBaseUrl: '/api'`, portanto as requisições devem ser encaminhadas para a API em `http://localhost:8080` pelo proxy do ambiente ou por um servidor reverso. O backend precisa estar em execução e aceitar a origem do frontend em `CORS_ALLOWED_ORIGINS` quando o navegador acessar a API diretamente.

## Build e testes

```bash
npm run build
npm run watch
npm test
```

O build de produção gera os arquivos em `dist/Carteira-acoes-frontend/browser/`.

## Configuração da API

- Desenvolvimento: [`src/environments/environment.ts`](src/environments/environment.ts) usa `/api`.
- Produção: [`src/environments/environment.production.ts`](src/environments/environment.production.ts) também usa `api` relativo, atendido pelo proxy `/api/` do Nginx no Docker Compose.

O token JWT é mantido em `sessionStorage`. O interceptor adiciona `Authorization: Bearer ...` às chamadas autenticadas; ao expirar ou falhar a sessão, o usuário volta para o login.

## Docker

O `Dockerfile` executa `npm ci`, cria o build Angular e publica os arquivos com Nginx. Para subir a aplicação completa:

```bash
cd ../deploy
docker compose up --build -d
```

Acesse `http://localhost`. O Nginx serve a SPA e encaminha `/api/` para o backend.

## Regras de domínio importantes

- O backend é a fonte de verdade de posições, valores, cotações e conversões.
- O frontend não recalcula resultados financeiros nem mistura BRL e USD.
- Dados ausentes de conversão ou cotação devem continuar explícitos na interface.
- Componentes e páginas devem preservar navegação por teclado, contraste e estados acessíveis.

Para o fluxo completo, incluindo Postman e configuração do banco, consulte o [README geral](../README.md) e o [README do backend](../Carteira-acoes-backend/README.md).
