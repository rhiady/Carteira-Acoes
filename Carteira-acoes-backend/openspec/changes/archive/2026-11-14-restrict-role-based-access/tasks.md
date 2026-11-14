## 1. Inventário e regras de autorização

- [x] 1.1 Mapear todos os mappings de ações, corretoras, carteiras, posições e operações e registrar a matriz `ADMIN`/`USER`/anônimo.
- [x] 1.2 Confirmar que as authorities emitidas pelo JWT continuam no formato `ROLE_ADMIN` e `ROLE_USER` usado pelo Spring Security.

## 2. Proteção do catálogo

- [x] 2.1 Restringir `POST`, `PUT`/`PATCH` e `DELETE` de ações a `ROLE_ADMIN` na configuração HTTP e/ou no resource, sem alterar os `GET` públicos existentes.
- [x] 2.2 Restringir `POST`, `PUT`/`PATCH` e `DELETE` de corretoras a `ROLE_ADMIN`, preservando as validações de CNPJ, CVM e integridade referencial.
- [x] 2.3 Garantir respostas padronizadas `401` e `403` e impedir que requisições negadas alcancem mutações nos services/repositórios.

## 3. Operações do usuário

- [x] 3.1 Revisar criação e manutenção de carteiras para permitir somente o usuário autenticado e impedir associação a `usuarioId` de terceiro.
- [x] 3.2 Revisar vínculos, compras, vendas, posições e resumos para manter o acesso de usuários comuns apenas às próprias carteiras.
- [x] 3.3 Confirmar que o papel não pode ser informado, alterado ou elevado pelo cliente em cadastro ou atualização de usuário.

## 4. Verificação

- [x] 4.1 Adicionar testes de integração em que `ADMIN` cria, altera e exclui ações e corretoras com sucesso.
- [x] 4.2 Adicionar testes de integração em que `USER` recebe `403` para cada mutação administrativa e nenhum dado é alterado.
- [x] 4.3 Adicionar testes de integração em que usuário anônimo recebe `401` nas rotas protegidas.
- [x] 4.4 Adicionar ou ajustar testes de compra, venda e vínculo próprios e de tentativa de acesso a carteira de terceiro.
- [x] 4.5 Executar testes, validação OpenSpec e build, corrigindo qualquer endpoint que divergir da matriz de autorização.
