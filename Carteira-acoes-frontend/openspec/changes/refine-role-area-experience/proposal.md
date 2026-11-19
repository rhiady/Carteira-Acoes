## Why

As áreas de usuário comum e administrador já possuem separação de acesso, mas a experiência ainda precisa de uma revisão unificada de hierarquia, clareza, estados e responsividade. A crítica do Impeccable identificou uma base visual coerente, porém também apontou linguagem ambígua, recuperação insuficiente, feedback de erro pouco orientado e excesso de cerimônia visual; esses problemas devem ser tratados nas duas áreas antes de ampliar o produto.

## What Changes

- Refinar as superfícies de entrada e navegação de `ROLE_USER` e `ROLE_ADMIN` com hierarquia explícita, uma ação primária dominante e contexto de autoridade visível.
- Melhorar clareza de labels, estados loading/empty/error/success/access-denied e continuidade após ações sem fabricar métricas ou conteúdo.
- Corrigir o tratamento acessível de erros de formulário, incluindo foco, relações `aria-describedby` e anúncios não duplicados quando aplicável.
- Reduzir espaço ocupado por branding decorativo e priorizar o conteúdo real de cada área, preservando a navegação e os fluxos financeiros existentes.
- Garantir leitura consistente nos temas claro/escuro e em desktop, tablet e mobile, sem overflow horizontal acidental.
- Executar auditoria técnica Impeccable e validação de acessibilidade/responsividade antes de considerar a change concluída.
- Não criar endpoint de recuperação de senha, métrica administrativa, regra financeira, dependência ou autorização nova.

## Capabilities

### New Capabilities

- `role-area-visual-experience`: experiência visual e estados compartilhados das áreas autenticadas comum e administrativa, mantendo a autoridade derivada da sessão.

### Modified Capabilities

- `frontend-usability-and-visual-consistency`: reforçar contexto, hierarquia, densidade, estados, responsividade e acessibilidade nas áreas por papel.
- `investment-frontend-experience`: manter as superfícies financeiras e administrativas escaneáveis, reais e consistentes com o sistema visual existente.

## Impact

- Affected UI: `src/app/features/acesso`, `src/app/features/inicio`, `src/app/features/dashboard`, componentes compartilhados de shell/header/navegação e estilos globais relacionados.
- Affected verification: testes das páginas de entrada comum/admin, componentes compartilhados, rotas/guards, AXE e validações responsive quando suportadas pelo ambiente.
- API/backend: nenhum endpoint, payload, DTO, regra de autoridade ou cálculo financeiro será alterado. Recuperação de senha permanece dependência explícita caso não exista contrato publicado.
- Dependencies: nenhuma dependência nova; reutilizar Angular Material, Router, sinais, tokens, formatters e componentes existentes.
