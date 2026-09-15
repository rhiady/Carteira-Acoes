## 1. Contrato e validação

- [x] 1.1 Revisar `UsuarioResource`, `UsuarioService`, entidade, mapper, DTOs e repositório para preservar as regras atuais.
- [x] 1.2 Criar DTO de atualização com nome e e-mail obrigatórios e senha opcional, mantendo validações de formato/tamanho.
- [x] 1.3 Definir o endpoint `PUT /usuarios/{id}` e respostas de sucesso, validação, conflito e não encontrado.

## 2. Implementação

- [x] 2.1 Implementar a operação de atualização no serviço dentro de transação.
- [x] 2.2 Validar unicidade do e-mail excluindo o próprio usuário e preservar a senha quando omitida.
- [x] 2.3 Aplicar hashing à nova senha quando enviada e mapear a resposta sem expor credenciais.

## 3. Testes

- [x] 3.1 Testar atualização de nome e e-mail com HTTP 200.
- [x] 3.2 Testar atualização sem senha preservando a senha anterior.
- [x] 3.3 Testar alteração de senha sem persistir texto puro nem retorná-la.
- [x] 3.4 Testar e-mail duplicado, usuário inexistente e payload inválido.

## 4. Verificação

- [x] 4.1 Executar os testes relevantes e a suíte do backend.
- [x] 4.2 Validar o change com `openspec validate "add-user-update" --strict`.
