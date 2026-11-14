## 1. Contratos públicos de usuário

- [x] 1.1 Ajustar `UsuarioCreateRequest` para aceitar somente `login` e `password`, criando sempre `USER`.
- [x] 1.2 Ajustar `UsuarioUpdateRequest` para aceitar somente `login` e `ativo`, sem senha ou papel.
- [x] 1.3 Criar `AlterarSenhaRequest` com `password` obrigatório e validado.
- [x] 1.4 Ajustar `UsuarioResponse` para expor somente `id`, `login`, `papel` e `ativo`.
- [x] 1.5 Criar request específico para alteração administrativa de papel.

## 2. Serviços, mapeamento e endpoints

- [x] 2.1 Atualizar mapper e service para converter os DTOs sem retornar ou receber diretamente `Usuario` na camada pública.
- [x] 2.2 Separar `PUT /usuarios/{id}` para alteração cadastral, `PUT /usuarios/{id}/senha` para senha e `PUT /usuarios/{id}/papel` para admins.
- [x] 2.3 Preservar ownership, unicidade, papel persistido e autenticação existentes durante a migração dos contratos.
- [x] 2.4 Revisar login e `/api/auth/me` para usar `login` e garantir ausência de senha, hash e entidade de persistência.
- [x] 2.5 Restringir a alteração de papel a administradores e impedir autoelevação.

## 3. Verificação

- [x] 3.1 Atualizar testes unitários e de integração para cadastro, alteração cadastral, alteração de senha, promoção e respostas seguras.
- [x] 3.2 Cobrir tentativa de enviar senha/papel no update cadastral e garantir que não sejam aplicados.
- [x] 3.3 Executar testes, validação OpenSpec e build, corrigindo incompatibilidades restantes.
