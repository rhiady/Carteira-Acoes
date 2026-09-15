## Context

Os formulários usam Angular Material e Reactive Forms, enquanto os estilos globais já fornecem tokens de cor, espaçamento, borda e foco. Os contratos de criação existentes devem ser preservados; veja proposal.md e `creation-form-experience` para o comportamento esperado.

## Goals / Non-Goals

**Goals:**

- Formar um padrão reutilizável de cabeçalho, grupos de campos, ajuda, erros, resumo e ações para cadastros, operações e consulta de ação.
- Tornar dependências de cadastro e contexto de operação visíveis antes do envio.
- Garantir que formulários se reorganizem entre 375 px, tablet e desktop sem alterar a ordem semântica.
- Usar os tokens e componentes Angular Material já presentes, com estados de foco e feedback consistentes.

**Non-Goals:**

- Alterar contratos HTTP, DTOs, cálculo autoritativo de valores, regras do backend ou disponibilizar criação direta de ação.
- Adicionar Tailwind, shadcn, uma biblioteca de formulários externa ou um tema escuro nesta mudança.
- Reformular telas de listagem, detalhe, dashboard ou gráficos.

## Decisions

### Um padrão de composição para todos os formulários

Cada tela usará cabeçalho de página, formulário em superfície única, `fieldset`/`legend` para blocos relacionados, texto de ajuda persistente e rodapé de ações. Isso reduz variações de interface e oferece referências semânticas a leitores de tela.

Alternativa considerada: criar um componente de formulário genérico com projeção de conteúdo. Foi rejeitada inicialmente porque os campos, dependências e validações são distintos; estilos compartilhados e pequenos componentes de feedback oferecem reutilização sem abstração prematura.

### Validação reativa com mensagens locais e resumo no envio

Os controles manterão validação reativa. Após uma tentativa inválida, cada campo afetado exibirá instrução curta conectada por `aria-describedby`; se houver mais de um erro, um resumo focável no início do formulário listará links para os campos inválidos. A validação não bloqueará a digitação nem exibirá erro antes da primeira interação relevante.

Alternativa considerada: usar somente snackbars. Foi rejeitada porque mensagens transitórias não explicam a correção no local do erro nem preservam contexto para tecnologias assistivas.

### Contexto e custos progressivos em operações

Compra e venda apresentarão ativo e ordem primeiro. Custos serão revelados por controle com `aria-expanded`, e o resumo estimado usará região de status sem tomar foco. A venda continuará usando confirmação explícita antes do envio.

Alternativa considerada: mostrar todos os custos inicialmente. Foi rejeitada para reduzir densidade e porque esses campos são opcionais.

### Layout por CSS Grid e tokens existentes

Grades de campos terão colunas somente onde a largura permitir e retornarão a uma coluna em telas estreitas. Os valores monetários usarão algarismos tabulares; o estado de venda combinará texto, ícone e cor semântica. Animações limitar-se-ão a opacidade e transformação e respeitarão `prefers-reduced-motion`.

Alternativa considerada: fixar duas colunas para uniformidade. Foi rejeitada porque comprime rótulos e campos em dispositivos móveis.

## Risks / Trade-offs

- [O estado visual duplicar regras dos componentes Material] → Manter tokens globais apenas para composição, superfícies e espaçamento; preservar controles Material para interação.
- [Erros de formulário podem gerar ruído excessivo] → Exibir validação após interação ou envio e manter textos curtos, específicos e próximos ao campo.
- [Dependências de carteira podem não existir] → Exibir estado orientativo e ação para criar o registro dependente, sem inventar identificadores.
- [Campos monetários podem variar por moeda] → Formatar resumos pela moeda associada ao ativo e manter a confirmação da API como valor oficial.

## Migration Plan

1. Aplicar os estilos e componentes compartilhados sem alterar endpoints ou modelos.
2. Atualizar cada formulário mantendo seus caminhos e parâmetros de rota.
3. Validar fluxos em 375 px, 768 px e desktop, incluindo teclado, foco e envio com falha.
4. Publicar junto à aplicação; rollback consiste em reverter os componentes e estilos deste change, pois não há migração de dados nem contrato externo novo.
