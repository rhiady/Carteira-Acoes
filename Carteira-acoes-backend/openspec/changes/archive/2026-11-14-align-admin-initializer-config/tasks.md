## 1. Alinhamento do initializer

- [x] 1.1 Renomear as variáveis e propriedades do `AdminInitializer` para `email` e `senha`, usando `@Value` do Spring.
- [x] 1.2 Trocar a busca por username por `findByEmail`, normalizar o email e criar `Usuario` com setters atuais, papel `ADMIN` e senha BCrypt.
- [x] 1.3 Atualizar as propriedades do perfil `dev` para `APP_ADMIN_EMAIL` e `APP_ADMIN_SENHA`, mantendo senha obrigatória.

## 2. Verificação

- [x] 2.1 Executar build/testes e confirmar que o diff não introduz credenciais reais ou construtores incompatíveis.
