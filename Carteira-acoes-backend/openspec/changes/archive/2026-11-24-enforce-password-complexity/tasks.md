## 1. Política de validação

- [x] 1.1 Definir uma regra reutilizável para exigir 8–72 caracteres, uma letra maiúscula e uma letra minúscula.
- [x] 1.2 Aplicar a regra aos requests de cadastro, criação administrativa e alteração de senha, sem aplicá-la ao request de login.
- [x] 1.3 Confirmar que entradas inválidas retornam o erro 400 já padronizado e não chegam à persistência.

## 2. Testes de contrato

- [x] 2.1 Testar senha válida no limite mínimo e senha válida acima do mínimo.
- [x] 2.2 Testar rejeição de senha com menos de 8 caracteres.
- [x] 2.3 Testar rejeição de senha sem letra maiúscula e sem letra minúscula nos fluxos de cadastro e alteração.
- [x] 2.4 Testar criação e troca administrativa com senha válida e inválida.
- [x] 2.5 Testar que uma senha antiga válida para login continua funcionando mesmo fora da nova política.

## 3. Verificação

- [x] 3.1 Executar a suíte de testes e confirmar que os contratos existentes permanecem compatíveis.
- [x] 3.2 Executar o build com a cobertura configurada e confirmar que os limiares de linha e branch continuam acima de 85%.
