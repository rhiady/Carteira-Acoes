## 1. Persistência da carteira

- [x] 1.1 Criar migração Flyway que adicione `corretora_id` obrigatório e sua chave estrangeira à tabela `carteira`, com estratégia de backfill para bases já populadas.
- [x] 1.2 Mapear a associação obrigatória de `Carteira` para `Corretora`.

## 2. Contrato e aplicação

- [x] 2.1 Exigir `corretoraId` no request de criação de carteira.
- [x] 2.2 Incluir `corretoraId` na resposta e no mapeamento de carteira.
- [x] 2.3 Atualizar o fluxo de criação para buscar a corretora por identificador e vinculá-la à carteira, rejeitando referência inexistente.

## 3. Verificação

- [x] 3.1 Atualizar testes de recurso e serviço para criação e consulta de carteira com corretora validada.
- [x] 3.2 Adicionar testes para rejeição de corretora ausente ou inexistente.
- [x] 3.3 Executar a suíte de testes relevante e corrigir regressões.
