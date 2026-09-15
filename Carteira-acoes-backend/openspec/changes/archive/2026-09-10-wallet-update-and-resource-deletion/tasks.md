## 1. Contratos e validação

- [x] 1.1 Revisar as restrições de `CarteiraRequest` e criar DTO dedicado de atualização contendo somente `nome`.
- [x] 1.2 Adicionar validação e normalização do nome, mantendo o contrato de erro padrão para payload inválido.

## 2. Regras de domínio e persistência

- [x] 2.1 Adicionar consultas eficientes para detectar operações e posições vinculadas à carteira.
- [x] 2.2 Adicionar consulta para detectar carteiras vinculadas à corretora e revisar todas as relações reais nas entities/migrations.
- [x] 2.3 Implementar no `CarteiraService` atualização somente do nome, com existência e ownership.
- [x] 2.4 Implementar no `CarteiraService` exclusão transacional, bloqueando vínculos com conflito de domínio antes do delete.
- [x] 2.5 Implementar no `CorretoraService` exclusão transacional, bloqueando carteiras e demais dependências sem cascade destrutivo.
- [x] 2.6 Traduzir eventual violação referencial concorrente para erro padronizado 409 sem expor exceção SQL.
- [x] 2.7 Adicionar consultas `exists` para operações, posições e histórico de cotações vinculados à ação.
- [x] 2.8 Implementar no `AcaoService` exclusão transacional, bloqueando qualquer vínculo antes do delete e preservando o cascade existente.

## 3. Endpoints e OpenAPI

- [x] 3.1 Expor `PUT /carteiras/{id}` com resposta 200 e documentação de 400/404.
- [x] 3.2 Expor `DELETE /carteiras/{id}` com resposta 204 e documentação de 404/409.
- [x] 3.3 Expor `DELETE /corretoras/{id}` com resposta 204 e documentação de 404/409.
- [x] 3.4 Expor `DELETE /acoes/{id}` com resposta 204 e documentação de 404/409.

## 4. Testes

- [x] 4.1 Testar atualização válida, nome inválido, carteira inexistente e payload com campos protegidos.
- [x] 4.2 Testar ownership de PUT e DELETE, garantindo 404 para carteira de outro usuário.
- [x] 4.3 Testar exclusão de carteira vazia (204), inexistente (404) e vinculada (409), preservando operações/posições.
- [x] 4.4 Testar exclusão de corretora sem vínculos (204), inexistente (404) e vinculada (409), preservando registros.
- [x] 4.5 Testar que nenhum erro SQL ou nome de constraint chega à API e que falhas são atômicas.
- [x] 4.6 Testar exclusão de ação sem vínculos (204), inexistente (404), com posição (409), com operações (409) e com histórico/cotação impeditivo (409), preservando todos os registros.

## 5. Verificação final

- [x] 5.1 Executar a suíte de testes e corrigir regressões nas operações Compra/Venda e consultas existentes.
- [x] 5.2 Executar build do backend e validar a documentação OpenAPI gerada.
- [x] 5.3 Confirmar que frontend, deploy, migrations financeiras e cascades não foram alterados.
- [x] 5.4 Confirmar que o cascade de histórico de cotação não é usado para apagar dados durante exclusão de ação.
