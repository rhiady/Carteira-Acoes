# backend-quality-validation Specification

## Purpose

Define evidências obrigatórias de qualidade para todas as classes do backend, incluindo arquitetura, segurança, configuração, testes, build, startup, smoke tests e relatório final.

## Requirements

### Requirement: Inventário integral de produção
Todas as classes de produção MUST ser inventariadas e classificadas por responsabilidade, incluindo controllers, services, repositories, entities, DTOs, mappers, exceptions, configs, clients, validators, utilities e segurança. A revisão MUST registrar problemas concretos de responsabilidade, duplicação, complexidade, código morto e nullability sem realizar rewrite por preferência.

#### Scenario: Classe descoberta
- **WHEN** uma classe de produção é encontrada em qualquer package da aplicação
- **THEN** ela aparece no inventário com sua responsabilidade e resultado da revisão

### Requirement: Segurança, erros e logging
A revisão MUST verificar autenticação, ownership, IDOR, mass assignment conceitual, validação, SQL injection, senha/hash/segredo em responses e logs, stack traces e exception mapping. Segredos MUST NOT ser hardcoded ou registrados, e regras conhecidas MUST NOT produzir erro 500.

#### Scenario: Dado sensível
- **WHEN** responses, schemas OpenAPI, logs e handlers são inspecionados
- **THEN** nenhuma senha, hash, token, segredo, SQL ou stack trace é exposto ao cliente ou logado indevidamente

### Requirement: Configuração operacional compatível
Datasource, JPA, Flyway, logging, Swagger, H2, profiles, Open Session in View e variáveis de ambiente MUST ser revisados. Compatibilidade com `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` MUST ser preservada, e qualquer alteração de Open Session in View somente poderá ocorrer após comprovar carregamento adequado em services.

#### Scenario: Inicialização configurada
- **WHEN** o backend inicia com configuração válida e PostgreSQL disponível
- **THEN** datasource conecta, Flyway valida/aplica migrations e a aplicação sobe sem erro grave

### Requirement: Evidência executável de qualidade
Antes da conclusão, `./mvnw test` e `./mvnw clean package` MUST passar. Regras financeiras críticas, contratos, repositories, exceptions, ownership e integrações MUST possuir testes proporcionais ao risco, e as APIs importantes MUST receber smoke tests de sucesso e de erros relevantes.

#### Scenario: Pipeline local completo
- **WHEN** a implementação é considerada pronta
- **THEN** testes e package passam, o backend inicia e os smoke tests cobrem sucesso, validação, not found, conflito e unauthorized/ownership quando aplicáveis

### Requirement: Relatório de auditoria rastreável
O resultado MUST incluir achados classificados sem inflação como CRITICAL, HIGH, MEDIUM, LOW ou INFO. Cada achado MUST indicar arquivo/classe, problema, impacto, correção realizada ou recomendação e teste/evidência associado; bloqueios reais de smoke ou ambiente MUST ser documentados.

#### Scenario: Entrega final da auditoria
- **WHEN** todas as áreas forem revisadas
- **THEN** o relatório permite rastrear cada achado à evidência e demonstra que frontend e deploy não foram alterados
