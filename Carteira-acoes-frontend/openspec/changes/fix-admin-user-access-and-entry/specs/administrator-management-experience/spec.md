## Purpose

Definir uma entrada administrativa distinta e uma gestão de usuários confiável, usando somente recursos e permissões administrativos já publicados.

## ADDED Requirements

### Requirement: Administração e Usuários têm destinos distintos

O sistema SHALL fornecer uma rota de entrada administrativa distinta da rota de gestão de usuários. A entrada administrativa SHALL apresentar contexto de administração e links para recursos administrativos publicados, enquanto `Usuários` SHALL apresentar a coleção e ações de gestão de usuários. A entrada SHALL NOT duplicar a tabela de usuários nem redirecionar silenciosamente para ela.

#### Scenario: Administrador abre Administração
- **WHEN** uma pessoa com `ROLE_ADMIN` abre a rota de Administração
- **THEN** visualiza o contexto administrativo e navegação para recursos de gestão, sem ser redirecionada para a listagem de usuários

#### Scenario: Administrador abre Usuários
- **WHEN** uma pessoa com `ROLE_ADMIN` abre a rota de Usuários
- **THEN** visualiza a listagem de usuários e as ações administrativas permitidas para essa coleção

### Requirement: Auto-cadastro público permanece inalterado

Esta change SHALL NOT alterar a rota pública `/cadastro` nem seu fluxo de auto-cadastro. A URL, os redirecionamentos relacionados, o payload de registro e o contrato de autenticação SHALL permanecer como estavam antes da implementação da mudança administrativa.

#### Scenario: Pessoa acessa o auto-cadastro público
- **WHEN** uma pessoa não autenticada acessa `/cadastro`
- **THEN** encontra o fluxo público de auto-cadastro existente, sem comportamento novo introduzido pela mudança administrativa

### Requirement: Listagem administrativa de usuários é confiável e acessível

O sistema SHALL carregar e apresentar a página de usuários retornada pelo recurso administrativo publicado. Enquanto a resposta estiver pendente, vazia ou indisponível, SHALL apresentar um estado textual acessível e uma continuação segura; ao receber usuários, SHALL exibir somente `login`, `papel`, `ativo` e a paginação correspondente. A tela SHALL NOT exibir ou editar nome, e-mail ou datas quando não forem retornados. Uma resposta `403` SHALL preservar a sessão e informar que o acesso não é permitido.

#### Scenario: Listagem retorna usuários
- **WHEN** o recurso administrativo retorna uma página com usuários
- **THEN** a tela apresenta login, papel e status de cada registro retornado, sem campos de perfil não publicados

#### Scenario: Listagem está vazia
- **WHEN** o recurso administrativo retorna uma página sem usuários
- **THEN** a tela explica que não há usuários e oferece somente a ação administrativa de criação já publicada

#### Scenario: Listagem falha
- **WHEN** a requisição de usuários falha
- **THEN** a tela apresenta uma mensagem segura, mantém o contexto de administração e permite nova tentativa quando aplicável
