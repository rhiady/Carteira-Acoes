## ADDED Requirements

### Requirement: Integridade transacional e configuração segura
Operações que gravam operação e posição SHALL possuir boundary transacional; entidades e migrations SHALL manter constraints compatíveis; credenciais e segredos SHALL vir de configuração externa e nunca ser expostos em logs ou erros HTTP.

#### Scenario: Falha após gravação parcial
- **WHEN** uma compra ou venda falha durante a persistência relacionada
- **THEN** nenhuma entidade parcial permanece persistida

