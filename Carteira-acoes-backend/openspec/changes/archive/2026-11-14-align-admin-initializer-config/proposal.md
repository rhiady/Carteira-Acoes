## Why

`AdminInitializer` ainda usa nomes e APIs que não existem no modelo atual de `Usuario`, como `username`, `password`, `findByUsername` e um construtor incompatível. Isso deixa o bootstrap do administrador desalinhado com o restante do projeto e pode impedir a compilação ou a inicialização do perfil `dev`.

## What Changes

- Alinhar as variáveis do initializer para `email` e `senha`.
- Usar `UsuarioRepository.findByEmail` e normalizar o email como os demais fluxos.
- Usar a anotação `@Value` do Spring para injetar propriedades.
- Criar o administrador com o construtor/setters disponíveis no domínio atual, mantendo BCrypt e `Papel.ADMIN`.
- Renomear as propriedades de bootstrap do perfil `dev` para refletir `email` e `senha`.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

Nenhuma. Esta é uma correção de configuração e alinhamento interno, sem mudança de requisito funcional da API.

## Impact

Afeta `AdminInitializer` e `application-dev.properties`. O ambiente `dev` deverá fornecer `APP_ADMIN_EMAIL` e `APP_ADMIN_SENHA`; não afeta o contrato dos endpoints de autenticação.
