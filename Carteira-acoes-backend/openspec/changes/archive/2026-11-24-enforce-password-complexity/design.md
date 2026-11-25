## Context

Os DTOs de criação e alteração já limitam a senha a 8–72 caracteres, mas não compartilham uma regra para letras maiúsculas e minúsculas. A validação ocorre na borda HTTP antes do serviço codificar ou persistir a senha.

## Goals / Non-Goals

**Goals:**

- Centralizar a expressão da política para reduzir divergência entre os DTOs.
- Aplicar a mesma validação aos fluxos de cadastro, criação administrativa e troca de senha.
- Preservar o limite superior compatível com BCrypt e as respostas de validação existentes.
- Cobrir os limites e cada requisito de composição com testes de contrato.

**Non-Goals:**

- Não exigir números, símbolos, expiração ou troca periódica de senha, pois não foram solicitados.
- Não revalidar senhas já armazenadas durante o login.
- Não migrar ou re-hashear senhas existentes.

## Decisions

- Usar uma única regra de validação reutilizável nos requests que recebem senha. Isso evita que cadastro e troca de senha evoluam com critérios diferentes.
- Manter a validação declarativa na camada de request, retornando o erro HTTP 400 já usado para dados inválidos. Validar no serviço em duplicidade foi considerado, mas aumentaria código sem proteger um novo limite de confiança neste fluxo.
- Manter o intervalo de 8 a 72 caracteres e acrescentar apenas os requisitos de uma letra minúscula e uma maiúscula. O limite de 72 permanece compatível com o BCrypt.
- Testar que a senha inválida não chega à persistência e que o login de uma senha antiga válida continua compatível.

## Risks / Trade-offs

- [Usuários podem ter senhas antigas fora da nova política] → permitir login normal e aplicar a regra somente ao criar ou alterar a senha.
- [Mensagens genéricas podem dificultar saber qual requisito falhou] → manter o contrato de erro existente e cobrir os motivos nos testes, sem expor detalhes sensíveis.
- [Regex divergente entre requests] → usar uma constante ou anotação compartilhada e testar todos os endpoints que recebem senha.
