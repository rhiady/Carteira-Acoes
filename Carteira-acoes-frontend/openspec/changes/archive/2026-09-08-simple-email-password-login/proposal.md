## Why

A aplicação atualmente não oferece uma entrada autenticada para que uma pessoa acesse suas carteiras e dados de investimento. Esta mudança cria o fluxo mínimo de acesso por email e senha, alinhado ao contrato de autenticação do backend e à linguagem visual financeira já adotada.

## What Changes

- Criar a rota pública `/login` e uma tela com apenas email, senha e o CTA `Entrar`.
- Integrar o envio a `POST /auth/login` usando a URL base já configurada no frontend.
- Exibir validação local, carregamento, erro de credenciais e falha inesperada sem submeter duplicadamente.
- Persistir somente `id`, `nome` e `email` do usuário autenticado, com logout simples e limpeza do estado local.
- Redirecionar login bem-sucedido para a rota principal existente, `/inicio`, e avaliar proteção de navegação das rotas privadas.
- Garantir responsividade, acessibilidade por teclado, foco visível e controle acessível de mostrar/ocultar senha.
- Não alterar backend, deploy, JWT, refresh token, cadastro, recuperação de senha, login social, MFA ou permissões.

## Capabilities

### New Capabilities

- `email-password-authentication`: fluxo frontend de login, sessão local mínima, logout e guard de navegação.

### Modified Capabilities

- `investment-api-integration`: adicionar o consumo tipado do endpoint de autenticação publicado, preservando a política de URL centralizada e mensagens seguras de erro.
- `investment-frontend-experience`: incluir o estado de acesso autenticado e a tela pública de login na experiência frontend consistente e responsiva.

## Impact

- Afeta rotas Angular, uma tela de feature de autenticação, models/DTOs, um `AuthService` simples e possivelmente um guard.
- Consome exclusivamente `POST /auth/login` com `{ email, senha }`; a resposta precisa fornecer os dados simples do usuário (`id`, `nome`, `email`).
- Reutiliza `apiConfig`, `environment.apiBaseUrl`, Angular Reactive Forms, Angular Material e os tokens visuais existentes.
- Não modifica qualquer código de backend ou configuração de deploy.
