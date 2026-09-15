## Context

O frontend Angular usa componentes standalone, rotas lazy, `Service`, `inject()`, `apiConfig.endpoint(...)`, Angular Material e Reactive Forms. A rota principal do produto é `/inicio`; `/dashboard` é uma superfície analítica existente. O backend já é acessado por uma base configurada em `environment`, portanto a autenticação não deve criar uma URL própria.

## Goals / Non-Goals

**Goals:**

- Introduzir uma fronteira clara entre a tela pública `/login` e as rotas da aplicação.
- Manter um estado local pequeno e tipado, com persistência somente da identidade não sensível.
- Integrar loading, erro, logout, redirecionamento e acessibilidade sem novo mecanismo complexo de estado.
- Aplicar a direção Swiss / financial editorial da skill `frontend-design`: branco/neutro, sans única, linhas finas, alinhamento à esquerda e acento vermelho usado com parcimônia.

**Non-Goals:**

- Alterar backend, deploy, modelo de segurança do servidor ou contrato além do consumo de `/auth/login`.
- Adicionar token, refresh, cadastro, recuperação de senha, login social, MFA ou autorização por perfil.
- Refatorar o shell inteiro ou criar uma arquitetura paralela de autenticação.

## Decisions

### Serviço simples de autenticação

Criar um serviço singleton responsável por `login`, usuário atual em signal, leitura/escrita de `authenticatedUser` e `logout`. O storage conterá somente `{ id, nome, email }`; a senha existirá apenas no corpo transitório da requisição. A alternativa rejeitada é um store global, pois o fluxo não exige coordenação complexa.

### Roteamento e guard

Adicionar `/login` como rota lazy e proteger o conjunto atual de rotas de produto com um guard funcional baseado no usuário local, preservando redirecionamento para `/login` e retorno pós-login para `/inicio`. A alternativa de proteger somente componentes deixaria navegação e deep links inconsistentes.

### Contrato HTTP

Adicionar DTOs de login e método no serviço de autenticação usando `apiConfig.endpoint('/auth/login')`. O tratamento distinguirá 401 de outros erros e manterá a mensagem amigável definida na especificação. Nenhum endpoint de cadastro ou endpoint presumido será criado.

### Formulário e acessibilidade

Usar Reactive Forms com dois controles, mensagens associadas aos campos, `aria-live` para estados de erro e loading, submit por Enter, foco visível e botão de senha com nome alternável. O componente manterá email após falha e bloqueará enquanto a requisição estiver pendente.

### Direção visual

Escolha: anchor Swiss, porque a tela precisa parecer uma porta de entrada do produto financeiro existente, não um template autônomo. O diferenciador será uma regra editorial vertical/horizontal que ancora o bloco de marca e o formulário em uma grade assimétrica, mantendo o formulário compacto e centralizado na área útil. Tokens: superfície `#FFFFFF`/`#F7F7F8`, texto neutro, acento Swiss Red `#E4002B`, sans única compatível com o shell e regras de 1px; sem gradiente, textura ou decoração extra.

### Testes e validação

Cobrir serviço e guard com `HttpTestingController`, componente com validação, toggle, loading, erros e redirecionamento, além de build e revisão manual em desktop/mobile. Validar que nenhum teste ou implementação persiste senha, hash ou segredo.

## Risks / Trade-offs

- **[Resposta real do backend diverge do shape esperado]** → Confirmar o contrato OpenAPI disponível antes da implementação e manter o mapper restrito aos campos publicados; registrar qualquer dependência em tasks.
- **[Guard bloqueia telas administrativas usadas sem sessão durante desenvolvimento]** → Aplicar o guard às rotas de aplicação somente após confirmar o fluxo existente e cobrir redirecionamento em testes.
- **[Storage local é alterado manualmente]** → Validar o shape ao restaurar e tratar dados inválidos como sessão ausente; a proteção real permanece no backend.
- **[Login visualmente destoar do shell]** → Reutilizar tokens/componentes Material existentes e revisar a tela nos quatro breakpoints antes de concluir.

## Migration Plan

1. Implementar DTOs, serviço, rota, guard e tela sem alterar backend.
2. Executar testes unitários, build e validação manual responsiva.
3. Em rollback, remover a rota/guard/feature e o serviço; nenhum dado de backend ou migração persistente é afetado.
