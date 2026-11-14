## Context

Consulte `proposal.md` para a motivação. Atualmente a cadeia de segurança escreve respostas inline em `SecurityConfig`, enquanto os handlers em `security` escrevem o formato legado `StandardError`. O contrato público usado pelo restante da API é `ErroResponse`, e o `GlobalExceptionHandler` deve continuar tratando exceções do processamento MVC.

## Goals / Non-Goals

**Goals:**

- Manter autenticação e autorização em classes separadas, cada uma com uma responsabilidade.
- Fazer os dois handlers de segurança responderem com `ErroResponse`.
- Configurar os handlers separados na cadeia Spring Security e remover a duplicação inline.
- Garantir testes para status, content type, código, path e ausência de detalhes internos.

**Non-Goals:**

- Alterar regras de negócio, permissões ou emissão/validação dos JWTs.
- Criar um mecanismo adicional de tratamento de exceções.
- Alterar o contrato de erros de validação, domínio ou persistência além do necessário para evitar sobreposição com segurança.

## Decisions

- **Handlers separados por fluxo:** usar um `AuthenticationEntryPoint` para 401 e um `AccessDeniedHandler` para 403. Isso preserva o princípio de responsabilidade única e segue os pontos de extensão próprios do Spring Security.
- **Contrato único de resposta:** construir `ErroResponse` nos dois handlers, alinhando campos e códigos ao restante da API. A alternativa de manter `StandardError` foi rejeitada porque criaria dois contratos públicos para falhas equivalentes.
- **Configuração por injeção:** referenciar os handlers na configuração de `exceptionHandling`, em vez de lambdas com serialização duplicada. A alternativa inline foi rejeitada por repetir mapeamento e dificultar testes unitários.
- **Separação do MVC:** manter `GlobalExceptionHandler` para exceções lançadas após o despacho da requisição. O handler global não substitui os pontos de entrada do filtro de segurança.

## Risks / Trade-offs

- [Compatibilidade de clientes] → clientes que dependem dos campos de `StandardError` receberão `ErroResponse`; cobrir o contrato nos testes e documentar a mudança.
- [Resposta parcialmente escrita] → os handlers devem definir status e content type antes de serializar e não escrever após a resposta estar comprometida; testar o caminho de erro diretamente.
- [Código legado sem uso] → procurar referências a `StandardError` após a migração e remover somente se não houver consumidores.

## Migration Plan

1. Migrar os handlers para `ErroResponse` e conectá-los à cadeia de segurança.
2. Remover a implementação inline equivalente de `SecurityConfig`.
3. Executar testes unitários e de integração dos fluxos 401/403.
4. Reverter a configuração e os handlers caso clientes ainda dependam explicitamente do formato legado, após identificar a compatibilidade necessária.
