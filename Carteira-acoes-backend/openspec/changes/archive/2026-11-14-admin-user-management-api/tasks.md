## 1. Padronização das rotas

- [x] 1.1 Alterar os mapeamentos de `AcaoResource`, `CarteiraResource`, `CorretoraResource` e `OperacaoResource` para os paths equivalentes sob `/api`, preservando `/api/auth`.
- [x] 1.2 Atualizar `SecurityConfig`, documentação e testes existentes para usar somente as rotas com `/api`, rejeitando os paths antigos.

## 2. Contratos e mapeamento de usuário

- [x] 2.1 Ajustar `UsuarioCreateRequest`, `UsuarioUpdateRequest`, `AlterarPapelRequest` e `AlterarSenhaRequest` para separar cadastro, papel e senha, com validação de entrada.
- [x] 2.2 Atualizar `UsuarioMapper` e `UsuarioResponse` para mapear username para a identidade persistida e nunca expor senha ou hash.
- [x] 2.3 Ajustar `UsuarioRepository`/normalização para validar unicidade do username na criação e no update, excluindo o próprio ID.

## 3. Serviço e resource administrativos

- [x] 3.1 Refatorar `UsuarioService` para concentrar persistência, transformação, validação e `PasswordEncoder`, implementando create, list, find, update, alterarPapel e alterarSenha com transações adequadas.
- [x] 3.2 Fazer o update alterar somente username e ativo, preservando senha, papel e registro quando o usuário for desativado.
- [x] 3.3 Ajustar `UsuarioResource` para `/api/usuarios`, proteger a classe com `@PreAuthorize("hasRole('ADMIN')")` e implementar os seis endpoints definidos, sem DELETE.
- [x] 3.4 Remover regras de autoatendimento e rotas antigas de usuário que conflitarem com a API administrativa, atualizando documentação OpenAPI quando necessário.

## 4. Segurança e compatibilidade de autenticação

- [x] 4.1 Habilitar method security, adicionar `@PreAuthorize("hasRole('ADMIN')")` na classe `UsuarioResource` e atualizar `SecurityConfig` para reconhecer as novas rotas administrativas e manter respostas 401/403 consistentes.
- [x] 4.2 Confirmar no `UsuarioDetailsService` que `ativo=false` produz uma conta desabilitada e que o login não emite token.
- [x] 4.3 Verificar que o bootstrap administrativo continua codificando a senha e que nenhuma alteração de schema ou registro existente é necessária.

## 5. Verificação

- [x] 5.1 Adicionar testes de serviço para unicidade, hash antes do save, novo hash na troca, preservação de senha/papel no update, alteração separada de papel e desativação sem delete.
- [x] 5.2 Adicionar testes de integração para os seis endpoints com administrador, usuário comum, anônimo e tentativa de DELETE.
- [x] 5.3 Verificar respostas sem senha/hash, login rejeitado para conta desativada e rejeição das rotas sem `/api`.
- [x] 5.4 Executar suíte de testes, build e `openspec validate admin-user-management-api --type change --strict`.
