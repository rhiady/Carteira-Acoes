## Context

O resource `/api/usuarios` representa operações administrativas e a API já
possui papéis `ROLE_ADMIN` e `ROLE_USER`. A mudança deve reforçar o contrato de
autorização nos endpoints de usuário e cobrir leitura, criação e alteração com
testes de integração.

## Goals / Non-Goals

**Goals:**

- manter todos os endpoints de `/api/usuarios/**` exclusivos de administradores;
- garantir `403 Forbidden` para usuários comuns sem executar a operação;
- impedir exposição de existência ou dados de usuários por consultas não autorizadas;
- manter `/api/auth/me` como o endpoint permitido para o usuário consultar o
  próprio contexto autenticado.

**Non-Goals:**

- criar endpoint de cadastro público;
- alterar o modelo de papéis;
- alterar a resposta de login ou o formato do JWT;
- implementar um endpoint separado de perfil do usuário.

## Decisions

- **Aplicar a regra na autorização da classe de usuários:** a proteção deve
  continuar abrangendo todos os métodos de `/api/usuarios`, evitando liberar
  acidentalmente um método novo por configuração individual.
- **Retornar 403 para usuário autenticado sem papel:** o cliente está
  autenticado, mas não autorizado; portanto a resposta deve ser diferente de
  `401`, que permanece reservado para ausência ou invalidade do token.
- **Usar `/api/auth/me` para o contexto próprio:** o usuário comum recebe seu
  próprio `id`, login e authorities nesse endpoint, sem ganhar acesso à
  listagem administrativa.
- **Adicionar testes de integração por método:** testar listagem, consulta,
  criação e alteração com `ROLE_USER`, além de preservar o acesso de
  `ROLE_ADMIN`.

## Risks / Trade-offs

- [Risco] O frontend pode ocultar a tela administrativa, mas ainda tentar
  chamar a API diretamente → [Mitigação] a autorização permanece no backend e
  os testes validam `403` para usuários comuns.
- [Risco] Um novo endpoint de usuário ser criado fora da regra da classe →
  [Mitigação] manter a proteção no resource e revisar qualquer endpoint novo
  como parte da mesma capacidade.
