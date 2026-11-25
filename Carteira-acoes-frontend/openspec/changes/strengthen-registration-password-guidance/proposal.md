## Why

O cadastro atualmente informa apenas o tamanho mínimo da senha, deixando de orientar a pessoa sobre os demais critérios exigidos para uma senha válida. Isso causa tentativas rejeitadas e feedback tardio no fluxo de criação de conta.

## What Changes

- Exibir, junto ao campo de senha do cadastro, um aviso claro com os critérios: mínimo de 8 caracteres, pelo menos uma letra maiúscula e pelo menos um caractere especial.
- Validar esses critérios no frontend antes de enviar `POST /api/auth/cadastro`.
- Apresentar mensagens acessíveis e específicas para os critérios ainda não atendidos, preservando a confirmação de senha e os campos não sensíveis em caso de erro.
- Manter o payload de cadastro e o contrato do backend sem incluir a confirmação de senha.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `user-access-experience`: fortalecer os critérios e o feedback de validação da senha no cadastro de conta.

## Impact

- `src/app/features/cadastro/cadastro.page.ts` e seus testes de componente.
- Validators e mensagens visíveis do formulário de cadastro; nenhuma nova dependência ou endpoint.
- O contrato de `POST /api/auth/cadastro` permanece `{ nome, email, senha }`; a regra também deve continuar compatível com a validação autoritativa do backend.
