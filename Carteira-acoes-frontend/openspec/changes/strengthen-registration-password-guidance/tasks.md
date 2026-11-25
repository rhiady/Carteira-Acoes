## 1. Validação e feedback do cadastro

- [x] 1.1 Atualizar o controle de senha de `CadastroPage` para exigir no mínimo 8 caracteres, uma letra maiúscula e um caractere especial, mantendo a confirmação de senha e o payload `{ nome, email, senha }`.
- [x] 1.2 Exibir junto ao campo de senha um aviso acessível e persistente com os três critérios, indicando durante a digitação quais critérios ainda não foram atendidos sem depender apenas de cor.
- [x] 1.3 Preservar os estados existentes de visibilidade, carregamento, erro de API, foco e redirecionamento, garantindo que nenhuma senha ou confirmação seja persistida ou exibida fora dos campos.

## 2. Testes e validação

- [x] 2.1 Atualizar `cadastro.page.spec.ts` para cobrir cada critério ausente, uma senha válida, bloqueio sem chamada ao serviço e preservação do fluxo de confirmação de senha.
- [x] 2.2 Executar os testes focados do cadastro e a validação/build do frontend, confirmando que o endpoint, payload e demais fluxos de login não foram alterados.
- [x] 2.3 Verificar o cadastro por DOM/testes de acessibilidade estrutural e pelos estilos responsivos existentes, incluindo mensagens associadas ao campo, foco e viewport estreito.
