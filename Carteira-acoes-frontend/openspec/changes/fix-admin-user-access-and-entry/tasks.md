## 1. Diagnóstico e contrato da listagem

- [x] 1.1 Cobrir `UsuarioService` com testes do recurso paginado administrativo, parâmetros, página retornada e normalização de falhas `403` e fallback seguro.
- [x] 1.2 Atualizar `UsuariosPage` para manter contexto, dados e paginação exclusivamente da resposta autoritativa e apresentar loading, vazio, falha, acesso negado e retry acessíveis.
- [x] 1.3 Atualizar o modelo e a tabela da listagem para os campos publicados `login`, `papel` e `ativo`, removendo a edição incompatível e sua cobertura obsoleta.

## 2. Entrada administrativa distinta

- [x] 2.1 Alterar `/admin` para carregar a entrada administrativa existente com os guardas de administrador, preservando lazy loading e links diretos para `/usuarios`.
- [x] 2.2 Refinar navegação e títulos administrativos para que Administração e Usuários tenham propósito, rota ativa e ação principal distintos, sem duplicar a tabela de usuários.
- [x] 2.3 Adicionar testes de rota e página para a entrada administrativa, gestão de usuários e bloqueio de usuário comum.

## 3. Verificação

- [x] 3.1 Executar testes de serviço, rota e páginas; confirmar que nenhuma URL, payload, modelo ou autorização foi inventado ou alterado fora do contrato publicado, incluindo a preservação de `/cadastro`, seus redirecionamentos e o contrato de auto-cadastro público.
- [x] 3.2 Executar AXE e inspeção autenticada de Administração e Usuários em claro/escuro, desktop/mobile, cobrindo lista com dados, vazio, erro, `403`, foco, retry e navegação ativa.
- [x] 3.3 Executar build, validação estrita OpenSpec e checagens de regressão relevantes.
