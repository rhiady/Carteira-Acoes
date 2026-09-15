## 1. Adaptação da consulta CVM

- [x] 1.1 Conferir o contrato atual da fonte de corretoras CVM e ampliar o DTO/adaptador com os campos necessários para identificar o CNPJ e o estado do registro.
- [x] 1.2 Atualizar a validação para comparar CNPJs normalizados e aceitar somente o registro ativo correspondente.
- [x] 1.3 Classificar resposta ausente/inativa como erro regulatório e resposta indisponível ou não interpretável como erro de integração, sem persistir a corretora.

## 2. Testes e verificação

- [x] 2.1 Atualizar os testes unitários da fachada CVM para registro ativo com CNPJ formatado, registro ausente, registro inativo e resposta incompleta.
- [x] 2.2 Atualizar os testes do serviço e do endpoint de corretoras para confirmar o cadastro de registro CVM válido e a ausência de persistência nos erros 422 e de integração.
- [x] 2.3 Executar a suíte de testes relevante e validar manualmente o cadastro com o CNPJ da corretora Bradesco informado pelo usuário.
