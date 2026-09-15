## Direction

Manter o anchor Swiss/financial editorial já adotado: superfícies neutras, regras finas, tipografia sans alinhada à esquerda e ações destrutivas explicitamente rotuladas. O diferenciador desta change é o contexto textual do recurso dentro de cada dialog, com estados de processamento visíveis e foco inicial no controle apropriado.

## Decisions

1. Criar dialogs pequenos e reutilizáveis para editar carteira e confirmar exclusões, usando Angular Material e Reactive Forms.
2. Normalizar mensagens técnicas de conflito para fallback amigável; mensagens de negócio confiáveis vindas de `ApiError.message` têm prioridade.
3. Remover itens somente no callback de sucesso HTTP; 404 pode limpar detalhe/listagem conforme a tela, enquanto 409 preserva o recurso.
4. Atualizar sinais locais e navegar somente após sucesso; ações financeiras existentes permanecem inalteradas.
