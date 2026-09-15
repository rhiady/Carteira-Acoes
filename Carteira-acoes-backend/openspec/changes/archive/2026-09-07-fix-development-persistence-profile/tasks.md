## 1. Perfis e fonte de dados

- [x] 1.1 Remover a ativação padrão do perfil `test` e configurar `dev` como perfil padrão da execução local.
- [x] 1.2 Manter a fonte PostgreSQL do perfil `dev` parametrizada por ambiente e alinhar seu alvo padrão ao banco local da aplicação.
- [x] 1.3 Confirmar que o perfil `test` conserva URL, driver e isolamento H2 explícitos.

## 2. Documentação e verificação

- [x] 2.1 Atualizar a documentação e o modelo de ambiente com o perfil e banco de desenvolvimento consistentes.
- [x] 2.2 Adicionar ou ajustar teste de configuração que valide a seleção H2 no perfil `test`.
- [x] 2.3 Executar a suíte afetada e validar a persistência de um cadastro no PostgreSQL de desenvolvimento após reiniciar a aplicação.
