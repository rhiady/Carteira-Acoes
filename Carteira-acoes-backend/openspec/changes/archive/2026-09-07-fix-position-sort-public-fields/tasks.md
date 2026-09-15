## 1. Diagnóstico

- [x] 1.1 Reproduzir ou confirmar o erro de `GET /carteiras/{id}/acoes?sort=ticker,asc` causado por ordenação sobre campo que não existe diretamente em `CarteiraAcao`.
- [x] 1.2 Confirmar os campos reais disponíveis em `CarteiraAcao`, a associação com `Acao` e o campo público exposto em `PosicaoAcaoResponse`.
- [x] 1.3 Revisar testes existentes de paginação/ordenação de posições para definir a cobertura mínima sem duplicar cenários.

## 2. Implementação

- [x] 2.1 Criar uma tradução/validação de sort para posições ativas com whitelist de campos públicos: `ticker`, `quantidade` e `id`.
- [x] 2.2 Mapear `ticker` para a propriedade real associada de ação antes da chamada ao repository.
- [x] 2.3 Preservar o comportamento sem sort explícito usando a ordenação padrão do endpoint.
- [x] 2.4 Rejeitar campos de sort não permitidos com erro HTTP 400 padronizado, sem vazar exceção de persistência como 500.

## 3. Testes

- [x] 3.1 Adicionar teste para `sort=ticker,asc`.
- [x] 3.2 Adicionar teste para `sort=ticker,desc`.
- [x] 3.3 Adicionar teste para paginação combinada com `sort=ticker,asc`.
- [x] 3.4 Adicionar teste para campo de sort inválido retornando HTTP 400.
- [x] 3.5 Adicionar teste para endpoint sem sort explícito retornando HTTP 200 com ordenação padrão.

## 4. Validação

- [x] 4.1 Rodar os testes relevantes do backend.
- [x] 4.2 Rodar `openspec validate "fix-position-sort-public-fields" --strict`.
- [x] 4.3 Documentar no resumo final a causa raiz, arquivos alterados, comportamento final e exemplos de requisição funcionando.
