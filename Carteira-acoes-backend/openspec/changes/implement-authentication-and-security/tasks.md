## 1. Modelo e persistência

- [x] 1.1 Completar o mapeamento de `Papel` em `Usuario`, definindo `USER` como padrão no fluxo de cadastro e mantendo o papel fora dos DTOs públicos de cadastro/atualização.
- [x] 1.2 Criar a migration Flyway incremental para adicionar a coluna `papel`, preencher usuários existentes com `USER`, aplicar a restrição `NOT NULL` e validar os valores permitidos.
- [x] 1.3 Ajustar fixtures e testes que instanciam `Usuario` diretamente para sempre definirem um papel válido.

## 2. Autenticação por sessão

- [x] 2.1 Integrar o login existente ao contexto do Spring Security e persistir a autenticação na sessão HTTP, preservando a resposta segura atual e o erro uniforme de credenciais inválidas.
- [x] 2.2 Atualizar `CurrentUserService` para obter o usuário autenticado do contexto/sessão gerenciado pelo Spring Security e manter as verificações atuais de ownership.
- [x] 2.3 Configurar `SecurityConfig` com as rotas públicas mínimas, autenticação obrigatória para as demais rotas e respostas padronizadas para `401` e `403`.
- [x] 2.4 Configurar o comportamento de CSRF/CORS compatível com a sessão e validar que requisições mutáveis não ficam desprotegidas por configuração ampla.

## 3. Autorização e contratos

- [x] 3.1 Converter o `Papel` persistido em autoridade de segurança e aplicar `ADMIN` somente às operações administrativas identificadas no inventário real de resources.
- [x] 3.2 Garantir que cadastro e atualização não aceitem papel do cliente nem permitam autoelevação, mantendo respostas sem senha, hash ou autoridade sensível desnecessária.
- [x] 3.3 Revisar as rotas de carteira, posição, resumo e operação para confirmar que continuam usando o usuário autenticado e rejeitam acesso a dados de terceiros.
- [x] 3.4 Atualizar anotações OpenAPI e tratamento de erros para refletir `401` e `403` nas rotas protegidas.

## 4. Verificação

- [x] 4.1 Adicionar testes de integração para login válido criar sessão, acesso anônimo retornar `401` e usuário autenticado acessar o próprio recurso.
- [x] 4.2 Adicionar testes de integração para `USER` receber `403` em operação administrativa e `ADMIN` ser aceito, sem mutação indevida em caso negado.
- [x] 4.3 Adicionar testes para migração/default de papel, tentativa de informar papel no cadastro/atualização, isolamento entre usuários e ausência de senha/hash nas respostas.
- [x] 4.4 Executar a suíte de testes, validação OpenSpec e build; corrigir qualquer endpoint ou fixture que ainda dependa do comportamento sem autenticação.
