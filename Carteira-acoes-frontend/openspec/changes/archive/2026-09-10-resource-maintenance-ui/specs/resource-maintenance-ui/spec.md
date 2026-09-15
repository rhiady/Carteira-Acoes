## Purpose

Oferecer manutenção segura de carteiras, corretoras e ações com confirmação, feedback acessível e sincronização local após respostas autoritativas do backend.

## ADDED Requirements

### Requirement: Carteira pode ser renomeada
O sistema SHALL permitir editar somente `nome` com Reactive Forms, enviar apenas `{ nome }` em `PUT /carteiras/{id}`, bloquear submissão duplicada e atualizar a interface após sucesso.

#### Scenario: Atualização do nome
- **WHEN** o usuário informa um nome válido e salva a carteira
- **THEN** o backend recebe somente o campo `nome` e o novo nome aparece após sucesso

### Requirement: Recursos exigem confirmação para exclusão
O sistema SHALL confirmar explicitamente `DELETE /carteiras/{id}`, `DELETE /corretoras/{id}` e `DELETE /acoes/{id}`, identificando o recurso e mostrando consequência, cancelamento e estado `Excluindo…`.

#### Scenario: Exclusão confirmada
- **WHEN** o usuário confirma uma exclusão
- **THEN** a operação é enviada uma única vez e o recurso só é removido após sucesso

### Requirement: Falhas de manutenção preservam contexto
O sistema SHALL manter o recurso após erro, tratar 409 como conflito de negócio priorizando mensagem segura do backend, nunca exibir detalhes técnicos, e tratar 404 como recurso indisponível sem deixar detalhe inválido aberto.

#### Scenario: Recurso vinculado
- **WHEN** o backend rejeita uma exclusão com 409
- **THEN** o recurso permanece e uma mensagem de vínculo compreensível é exibida

### Requirement: Sucesso sincroniza o estado
O sistema SHALL fechar dialogs, mostrar feedback, remover somente após sucesso real (incluindo 204), limpar seleção/detalhe relacionado e navegar para rota válida quando necessário.

#### Scenario: Exclusão bem-sucedida
- **WHEN** o backend responde sucesso, inclusive 204
- **THEN** a interface remove somente o recurso afetado e limpa referências relacionadas

### Requirement: Acessibilidade e direção visual
Dialogs e ações SHALL ter nomes explícitos, foco inicial, suporte a teclado, contraste/foco visível, responsividade e manter a linguagem Swiss/financial editorial existente sem interferir em Compra/Venda.

#### Scenario: Operação por teclado
- **WHEN** o usuário navega pelo fluxo usando teclado em viewport estreito
- **THEN** os controles permanecem alcançáveis, identificados e sem overflow obrigatório
