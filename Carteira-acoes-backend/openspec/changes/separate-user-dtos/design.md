## Context

O código atual possui DTOs de usuário acoplados ao contrato legado (`nome`, `email`, `senha`) e serviços que transformam dados persistidos em respostas públicas. A mudança precisa separar cadastro, manutenção cadastral, alteração de senha e resposta segura, preservando o hash apenas na entidade e no fluxo de autenticação.

## Goals / Non-Goals

**Goals:**

- Introduzir `UsuarioCreateRequest`, `UsuarioUpdateRequest`, `AlterarSenhaRequest`, um request de papel e `UsuarioResponse` com campos explícitos.
- Manter `POST /usuarios` público criando `USER`, `PUT /usuarios/{id}` para dados cadastrais, `PUT /usuarios/{id}/senha` para credencial e `PUT /usuarios/{id}/papel` para administradores.
- Impedir que senha/hash sejam recebidos ou devolvidos por contratos cadastrais e de identidade.
- Atualizar mappers, serviços, resources e testes sem devolver `Usuario` diretamente.

**Non-Goals:**

- Não alterar o algoritmo de hash ou o formato do JWT.
- Não criar fluxo de recuperação de senha, MFA ou troca de papel administrativo.
- Não alterar as regras de ownership de carteiras.

## Decisions

- **DTOs por operação:** cada endpoint recebe apenas os campos necessários à operação; isso evita que uma atualização cadastral reutilize um DTO de senha.
- **Resposta única e segura:** `UsuarioResponse` contém somente `id`, `login`, `papel` e `ativo`; entidades JPA não aparecem na camada web.
- **Operações sensíveis separadas:** `PUT /usuarios/{id}/senha` recebe apenas `AlterarSenhaRequest`; `PUT /usuarios/{id}/papel` exige `ROLE_ADMIN`; `PUT /usuarios/{id}` não recebe credencial nem papel.
- **Login explícito:** o contrato público usa `login`; a coluna persistida `email` permanece interna para evitar migração desnecessária.
- **Compatibilidade interna:** o mapper traduz os nomes públicos para os campos persistidos existentes quando necessário, evitando migração de banco sem requisito explícito.

Alternativa considerada: manter um DTO único com senha opcional. Foi rejeitada porque permite que uma alteração cadastral altere credenciais acidentalmente e aumenta o risco de exposição do hash.

## Risks / Trade-offs

- [Clientes atuais usam `nome`, `email`, `username` ou `senha`] → atualizar testes e documentação do contrato; não manter aliases sem requisito de compatibilidade.
- [O modelo persistido pode não possuir `ativo`] → verificar o domínio/migration durante a implementação e adicionar somente a alteração de persistência necessária.
- [Senha enviada no endpoint cadastral pode ser ignorada silenciosamente] → validar o contrato recebido e cobrir o cenário com teste de não alteração.

## Migration Plan

1. Criar os quatro DTOs e ajustar mappers/services.
2. Separar os endpoints cadastral e de senha.
3. Atualizar respostas de autenticação e testes para garantir ausência de senha/hash.
4. Executar suíte, validação OpenSpec e build.

Rollback: restaurar os contratos e mappings anteriores; nenhuma migração de dados é necessária se os campos persistidos atuais forem reutilizados.
