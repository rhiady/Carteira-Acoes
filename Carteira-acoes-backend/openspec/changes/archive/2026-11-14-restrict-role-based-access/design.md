## Context

O backend já emite JWT com authorities `ROLE_ADMIN` e `ROLE_USER`, aplica algumas regras em `SecurityConfig` e mantém verificações de ownership nos serviços de carteira e operações. As rotas de catálogo precisam ter uma política explícita e consistente para impedir que usuários comuns criem, alterem ou excluam ações e corretoras.

## Goals / Non-Goals

**Goals:**

- Centralizar a autorização de catálogo na cadeia do Spring Security e, quando necessário, reforçá-la com anotações declarativas nos resources.
- Manter consultas públicas existentes sem transformá-las em operações de escrita.
- Preservar ownership para carteiras, posições, compras, vendas e vínculos.
- Retornar `401` para ausência de autenticação e `403` para authority insuficiente.
- Testar ausência de mutação em toda tentativa negada.

**Non-Goals:**

- Alterar o formato, a assinatura ou as claims do JWT.
- Criar fluxo de promoção de usuário para administrador.
- Implementar refresh token, revogação, MFA ou gestão de identidade.
- Reescrever as regras financeiras já existentes.

## Decisions

- **Autorização por método HTTP e rota:** operações de escrita de ações e corretoras serão classificadas como administrativas; consultas manterão o acesso atual. A regra será expressa na configuração HTTP e/ou em `@PreAuthorize("hasRole('ADMIN')")` quando isso tornar o contrato do resource explícito.
- **Regra no servidor:** o papel será obtido exclusivamente das authorities do JWT validado. Nenhum campo de papel enviado pelo cliente será considerado.
- **Ownership preservado:** carteira, vínculo, posição e operação continuarão usando o usuário autenticado para validar propriedade. Um `ROLE_ADMIN` não ganhará acesso implícito aos dados financeiros de terceiros nesta change.
- **Resposta de autorização:** os handlers existentes serão mantidos para garantir o JSON padronizado de `401` e `403`.
- **Testes de integração:** cada grupo administrativo terá cenários de `ADMIN` permitido, `USER` proibido e anônimo não autenticado; cenários financeiros verificarão usuário próprio e usuário de terceiro.

Alternativa considerada: colocar todas as regras somente em services. Ela foi rejeitada para autorização de catálogo porque permitiria que uma rota esquecesse a checagem antes de chegar ao serviço; a cadeia HTTP e as anotações tornam o limite visível no endpoint.

## Risks / Trade-offs

- [Uma rota nova de catálogo pode não entrar na regra administrativa] → inventariar todos os mappings atuais e adicionar teste para cada mutação.
- [Anotações e regras HTTP podem divergir] → manter uma única política por endpoint e validar o resultado com MockMvc.
- [Usuários comuns podem precisar consultar catálogo para operar] → conservar os `GET` públicos atuais e restringir somente mutações.
- [Regras de ownership podem ser enfraquecidas durante a alteração] → executar a suíte existente de isolamento e exigir ausência de alterações em caso negado.

## Migration Plan

1. Inventariar os mappings de ações, corretoras, carteiras e operações.
2. Aplicar as restrições de `ROLE_ADMIN` às mutações de catálogo.
3. Confirmar que as rotas de carteira e operações continuam limitadas ao proprietário autenticado.
4. Adicionar ou ajustar testes de integração para `401`, `403`, `ADMIN`, `USER` e ownership.
5. Executar testes, validação OpenSpec e build.

Rollback: remover as novas regras de autorização e os testes associados; não há migração de banco nem alteração do formato do token.
