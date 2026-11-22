# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

Investidores que acompanham as próprias carteiras de ações e registram operações de compra e venda.

## Product Purpose

Permitir que investidores consultem posições, resultados, cotações e operações das suas carteiras usando dados financeiros retornados pela API.

## Operating Context

Pessoas autenticadas acompanham carteiras próprias, consultam posições e histórico de operações, analisam dados por moeda e registram compras ou vendas. Administradores usam fluxos administrativos separados.

## Capabilities and Constraints

O frontend Angular consome a API como fonte de verdade. Valores e resultados financeiros não podem ser inventados, recalculados ou convertidos entre BRL e USD no navegador; dados de cada moeda permanecem separados. A evolução total da carteira depende de um endpoint ainda indisponível. A interface deve usar Angular Material, ser responsiva e atender WCAG AA.

## Evidence on Hand

`dados-graficos-financeiros.txt` documenta os endpoints e campos publicados para resumo financeiro, posições, operações e histórico de cotação. Não há endpoint publicado para evolução total da carteira.

## Product Principles

- Mostrar apenas dados financeiros autoritativos e deixar ausências explícitas.
- Priorizar leitura rápida de carteira, posição e operação.
- Preservar contexto, acesso por papel e privacidade da carteira.
- Manter dados e ações acessíveis em desktop e mobile.

## Accessibility & Inclusion

As superfícies devem atender WCAG AA, ser operáveis por teclado, comunicar estados sem depender somente de cor e respeitar redução de movimento.
