## Context

O projeto já possui `PasswordEncoder`, login por email/senha, `CurrentUserService` baseado em sessão HTTP e dependências do Spring Security. `Usuario` passou a ter o enum persistido `Papel`, mas o schema Flyway ainda não possui a coluna correspondente e o `SecurityConfig` ainda não protege a cadeia HTTP.

## Goals / Non-Goals

**Goals:**

- Usar autenticação de sessão integrada ao contexto do Spring Security.
- Reutilizar `CurrentUserService` como ponto único para obter o usuário autenticado e validar ownership.
- Aplicar autorização por papel somente onde houver regra administrativa, sem duplicar checagens nos services de carteira.
- Tornar a migração de `papel` segura para bases existentes e impedir alteração pública do papel.
- Manter respostas de login e usuário baseadas em DTOs sem senha ou hash.

**Non-Goals:**

- Token stateless, JWT, refresh token ou integração OAuth.
- Tela de login, recuperação de senha, MFA ou confirmação de email.
- Criação de endpoint público para promover usuários a `ADMIN`.

## Decisions

- **Sessão gerenciada pelo Spring Security:** o login autenticará o usuário e salvará a autenticação no `HttpSession`; a alternativa de manter apenas um atributo manual de sessão é rejeitada porque deixa a proteção HTTP e o contexto de autorização desacoplados.
- **Identidade no contexto, não em request:** `CurrentUserService` lerá a identidade autenticada do contexto/sessão e continuará sendo usado pelos services para ownership. IDs enviados pelo cliente servirão apenas para localizar o recurso e nunca para definir o usuário da operação.
- **Papel como autoridade simples:** `Papel.ADMIN` e `Papel.USER` serão convertidos em autoridades do Spring Security. A configuração liberará cadastro/login e exigirá autenticação nas demais rotas; regras administrativas usarão a autoridade `ADMIN` e as regras de carteira continuarão verificando o proprietário.
- **Cadastro sempre como USER:** o mapper/service definirá `USER` no servidor. DTOs de cadastro e atualização não receberão papel. Atribuição de `ADMIN` ficará restrita a operação controlada de banco/infraestrutura, fora da API pública.
- **Migração Flyway incremental:** adicionar uma nova migration que crie `papel` inicialmente com valor padrão `USER`, preencha registros existentes e depois torne a coluna obrigatória. Não editar migrations já aplicadas.
- **Proteção CSRF compatível com sessão:** manter proteção contra requisições forjadas usando a estratégia compatível com o cliente web existente; o contrato e os testes devem deixar explícito o comportamento para requisições mutáveis. Não desabilitar CSRF globalmente como atalho sem justificar a integração do frontend.
- **Erro uniforme:** requests sem autenticação retornam `401`; requests autenticadas sem autoridade retornam `403`; ambos usam o formato de erro já adotado, sem stack trace ou detalhes internos.

## Risks / Trade-offs

- [Sessão exige envio e armazenamento correto de cookie pelo frontend] → documentar o contrato de sessão/CORS e cobrir login seguido de requisição protegida com o mesmo `MockMvc` session.
- [Bases existentes não têm papel] → migration com default e backfill antes de `NOT NULL`, validada em banco de teste.
- [Endpoints administrativos não estão explicitamente classificados no código atual] → inventariar resources durante a implementação e aplicar a regra somente às rotas que realmente administram catálogo/configuração; manter operações do usuário sob ownership.
- [CSRF pode quebrar chamadas mutáveis atuais] → testar o fluxo real e configurar o mecanismo mínimo compatível, sem remover a proteção por conveniência.

## Migration Plan

1. Aplicar a migration de `papel` com backfill `USER`.
2. Implementar o contexto de sessão, configuração de rotas e autorização.
3. Executar testes de login, acesso anônimo, `401`, `403`, ownership e migração.
4. Liberar a versão mantendo cadastro/login públicos e demais rotas protegidas.
5. Rollback de código consiste em reverter a configuração/fluxo de sessão; rollback de banco deve usar uma migration reversa somente se a operação for necessária e os dados de papel tiverem sido preservados externamente.
