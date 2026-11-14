## Context

O domínio `Usuario` possui `nome`, `email`, `senha` e `papel`, com construtor sem argumentos e setters Lombok. O repositório expõe `findByEmail`, e as senhas do projeto são armazenadas com `PasswordEncoder`.

## Goals / Non-Goals

**Goals:**

- Fazer o initializer compilar e funcionar no perfil `dev`.
- Reutilizar os nomes e convenções já usados no domínio e nos services.
- Preservar criação idempotente do administrador e armazenamento BCrypt.

**Non-Goals:**

- Alterar o modelo de usuário, migrations ou endpoints de autenticação.
- Criar um novo mecanismo de configuração tipada para somente duas propriedades.

## Decisions

- Usar `email` como identidade do administrador porque é o campo persistido e usado pelo `UsuarioRepository`.
- Usar `senha` apenas como nome local/configurável; persistir sempre o hash produzido pelo `PasswordEncoder`.
- Instanciar `Usuario` com `new Usuario()` e setters, evitando depender de um construtor que a entidade não possui.
- Manter o initializer restrito ao perfil `dev` e a existência prévia verificada por email.

## Risks / Trade-offs

- [Ambientes dev existentes usam os nomes antigos] → atualizar as variáveis para `APP_ADMIN_EMAIL` e `APP_ADMIN_SENHA` antes de iniciar.
- [Senha ausente] → manter falha rápida com mensagem clara, sem valor padrão inseguro.
