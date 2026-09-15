## Why

O frontend, o backend e o banco de desenvolvimento hoje exigem inicialização e configuração separadas, e o backend depende de um PostgreSQL instalado na máquina local. Um ambiente Docker reproduzível para toda a aplicação reduz dependências locais e torna a inicialização previsível.

## What Changes

- Adicionar uma definição Docker Compose para executar o frontend Angular, a API Spring Boot e um PostgreSQL juntos.
- Criar build de produção do frontend e servi-lo em um container HTTP na porta de desenvolvimento documentada.
- Criar um volume nomeado novo para os dados do PostgreSQL, isolado do banco local existente.
- Configurar o ambiente de desenvolvimento em container para conectar ao serviço PostgreSQL pelo nome da rede Docker.
- Configurar a origem permitida pelo CORS e a URL pública da API consumida pelo frontend containerizado.
- Documentar como iniciar, parar, consultar logs e remover o ambiente e seu volume de desenvolvimento completo.

## Capabilities

### New Capabilities

_Nenhuma._

### Modified Capabilities

_Nenhuma._ Esta é uma alteração de infraestrutura de desenvolvimento, sem mudança de requisitos funcionais ou contratos da API.

## Impact

- Novos arquivos de build/orquestração Docker para backend e frontend, além de `.dockerignore`.
- Configuração de ambiente, CORS, URL pública da API e documentação nos dois repositórios irmãos.
- Desenvolvimento local: os três componentes iniciam por Docker Compose e o PostgreSQL deixa de depender da instância da máquina.
