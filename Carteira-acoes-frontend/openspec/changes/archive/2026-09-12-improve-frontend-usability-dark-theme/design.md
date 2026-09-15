## Direction

Swiss / financial editorial / calm finance. O claro mantém superfícies neutras, hairlines e acento azul econômico; o escuro usa carvão e grafite em camadas contínuas, com texto primário quase branco e secundário cinza claro. O diferenciador é substituir a sequência de cards por agrupamentos editoriais com régua de métricas, separadores e whitespace intencional.

## Decisions

1. Auditar as telas reais antes de mexer na paleta; registrar redundâncias, ações ocultas, desalinhamentos e problemas de contraste.
2. Consolidar tokens de superfície, texto, borda, foco, radius, spacing e estados semânticos; eliminar hardcodes relevantes onde interferem nos temas.
3. Manter o shell e os componentes atuais, refinando composição e estilos em vez de criar uma arquitetura paralela.
4. Reduzir cards apenas quando não houver separação semântica; usar grupos, hairlines e whitespace para leitura financeira.
5. Preservar ações de compra/venda como CTAs claros e enviar manutenção/administrativo para menus contextuais quando adequado.
6. Validar visualmente rotas reais nos dois temas e em larguras desktop/mobile; build/testes não substituem essa revisão.

## Validation

Usar o anchor Swiss com tokens dark grafite, verificar contraste/foco via axe e inspeção, testar overlays, tabelas, dialogs, loading/empty/error, e comparar layout claro/escuro para garantir que a troca não altere responsividade.
