## Why

As telas de manutenção não oferecem edição segura do nome da carteira nem exclusão explícita de carteiras, corretoras e ações. Esta change adiciona esses fluxos somente no frontend, preservando o backend como autoridade para vínculos e regras de segurança.

## Scope

- Editar somente `nome` da carteira via `PUT /carteiras/{id}`.
- Excluir carteira, corretora e ação via os DELETE correspondentes, sempre com confirmação.
- Reutilizar dialogs, feedback, interceptor e arquitetura Angular existentes.
- Tratar 404, 409, 204, loading, erros amigáveis, acessibilidade e atualização de estado local.

## Non-goals

Não alterar backend, banco, migrations, Docker, Nginx, deploy, compras, vendas, cálculos financeiros ou contratos existentes fora dos endpoints desta manutenção.
