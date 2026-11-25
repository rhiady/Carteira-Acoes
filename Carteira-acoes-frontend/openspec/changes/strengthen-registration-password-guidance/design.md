## Context

O cadastro já usa um formulário reativo, controles de senha ocultável, mensagens associadas por `aria-describedby` e a direção visual Swiss/financial editorial. A mudança deve ficar restrita à página de cadastro e aos seus testes, preservando o payload e os serviços existentes.

## Goals / Non-Goals

**Goals:**

- Tornar os três critérios de senha visíveis antes do envio e compreensíveis durante a digitação.
- Reutilizar os tokens, regras, foco visível e padrões de erro já presentes no cadastro.
- Impedir o envio local quando qualquer critério estiver ausente e manter a confirmação de senha funcionando.
- Cobrir os critérios e o bloqueio de submissão com testes de componente.

**Non-Goals:**

- Alterar endpoint, payload, serviço de autenticação ou regra de login.
- Criar componente compartilhado, nova dependência ou política de complexidade além dos três critérios solicitados.
- Implementar validação financeira, persistência de senha ou alteração da senha em `Minha conta`.

## Decisions

- **Validação no `CadastroPage` com validators existentes:** adicionar `Validators.minLength(8)` e um `Validators.pattern` para maiúscula e caractere especial no controle já existente. Isso mantém a regra na borda do formulário e evita abstração nova; a alternativa de um serviço de política de senha seria desproporcional para um único formulário.
- **Aviso persistente e erro contextual:** exibir uma instrução curta próxima ao campo e, após interação/submissão, indicar os critérios não atendidos com texto associado ao input. Isso é mais descobrível e acessível do que depender apenas de tooltip, cor ou mensagem global.
- **Âncora visual Swiss:** manter superfície neutra, tipografia e regras finas atuais, usando a hierarquia já existente do formulário. O diferenciador será uma lista de requisitos que muda de estado por critério durante a digitação, sem introduzir decoração ou texto fictício.
- **Critério de caractere especial explícito:** tratar caractere especial como qualquer caractere que não seja letra ou número (`[^A-Za-z0-9]`), sem inventar uma lista fechada de símbolos. A regra permanece simples para a pessoa e compatível com caracteres especiais comuns.
- **Teste no componente:** acrescentar casos para cada critério ausente, combinação válida e ausência de chamada ao serviço. Não testar implementação interna de regex além do comportamento observável do formulário.

## Risks / Trade-offs

- [O backend pode aplicar uma política diferente] → manter a validação do backend como autoridade e exibir sua mensagem normalizada quando houver rejeição; o frontend apenas antecipa os critérios publicados nesta change.
- [Caracteres Unicode podem não ser considerados letras pela regex ASCII] → a regra documentada usa letras maiúsculas latinas; se o backend precisar aceitar outras categorias Unicode, a política deverá ser alinhada em uma change futura.
- [O feedback durante a digitação pode aumentar o ruído visual] → usar texto curto, agrupado junto ao campo e sem alterar o layout geral; mostrar erros completos apenas quando o campo for tocado ou o formulário submetido.
