## Context

O modelo persistido usa `email` como identidade e já possui `papel`, `ativo` e senha codificada. A camada atual expõe `UsuarioResource` em `/usuarios`, combina operações administrativas e de autoatendimento e tem regras de autorização distribuídas entre resource, service e configuração de segurança.

## Goals / Non-Goals

**Goals:**

- Padronizar os endpoints de negócio sob `/api`, mantendo `/api/auth` como namespace de autenticação.
- Fazer o contrato administrativo corresponder aos endpoints sob `/api/usuarios`, mantendo papel e senha como operações separadas.
- Centralizar no serviço a normalização, unicidade, mapeamento de resposta e uso do `PasswordEncoder`.
- Aplicar autorização de classe e manter a conta desativada no banco.
- Cobrir o contrato com testes de serviço, segurança e integração HTTP.

**Non-Goals:**

- Não renomear as colunas persistidas `email`/`nome` nem criar migração destrutiva.
- Não criar recuperação de senha, exclusão física, paginação nova ou alteração do JWT.
- Não manter os endpoints antigos como aliases sem requisito explícito de compatibilidade.

## Decisions

- **Prefixo de API:** alterar os mapeamentos de todos os resources de negócio para `/api`, sem aliases nos paths antigos; manter `AuthResource` em `/api/auth`.
- **Contrato público administrativo:** ajustar o resource para `/api/usuarios`, com `@PreAuthorize("hasRole('ADMIN')")` na classe, e habilitar method security com `@EnableMethodSecurity`; a configuração continuará garantindo autenticação/403 e os testes confirmarão a combinação das duas camadas.
- **Modelo público versus persistência:** manter `username` no DTO e mapear para a identidade persistida já existente (`email`), evitando alteração de banco. O mapper nunca incluirá `senha` na resposta.
- **Senha:** chamar `passwordEncoder.encode` dentro dos métodos transacionais de criação e troca antes de `save`; o request nunca será passado à entidade como senha persistida.
- **Atualização:** usar um request cadastral somente com username e ativo. A senha será alterada em `/senha` e o papel em `/papel`, cada um com seu contrato específico.
- **Desativação:** manter a linha no banco e preservar `.disabled(!ativo)` no `UserDetails`, validando login rejeitado por teste de integração.
- **Listagem:** preservar o suporte de paginação existente, ajustando apenas autorização, rota e representação pública.

Alternativa considerada: manter autoatendimento e administração no mesmo resource com verificações por ID. Foi rejeitada porque contradiz a proteção integral da API administrativa e aumenta o risco de autorização incompleta.

## Risks / Trade-offs

- [Clientes usam rotas sem `/api` ou `/usuarios` para autoatendimento] → documentar a quebra e atualizar os testes/consumidores no mesmo change; não criar alias silencioso.
- [O termo username pode divergir da coluna `email`] → manter a tradução no mapper/repositório e validar unicidade sobre a identidade efetivamente usada no login.
- [A autorização duplicada no resource e na configuração pode divergir] → testar cada método com admin, usuário comum, anônimo e token inválido.

## Migration Plan

1. Atualizar mapeamentos dos resources, DTOs, mapper, service e resource; ajustar as regras de segurança e remover rotas antigas do contrato.
2. Executar testes unitários e de integração, incluindo prefixos `/api`, hash diferente após troca e login recusado para usuário desativado.
3. Publicar as novas rotas e atualizar consumidores; registros existentes permanecem válidos.

Rollback: restaurar o resource/configuração anteriores e o contrato de clientes; nenhuma migração de dados precisa ser revertida.
