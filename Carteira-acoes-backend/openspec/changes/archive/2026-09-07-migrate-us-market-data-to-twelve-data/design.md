## Context

O adaptador do mercado EUA usa Alpha Vantage em duas chamadas sequenciais (`GLOBAL_QUOTE` e `OVERVIEW`) e combina as respostas. A seleção por mercado, o contrato `CotacaoAdapter`, a Brapi e os endpoints públicos já existem. Consulte `proposal.md` para a motivação e `specs/market-data/spec.md` para o comportamento contratado.

## Goals / Non-Goals

**Goals:**

- Substituir integralmente a integração Alpha Vantage do mercado EUA por uma consulta ao Twelve Data.
- Obter preço, nome e instante da cotação de uma resposta validável e normalizá-los ao contrato interno existente.
- Tornar URL e chave do provedor configuráveis sem expor a credencial em logs.

**Non-Goals:**

- Migrar os dados do mercado Brasil, alterar a Brapi ou introduzir fallback entre provedores.
- Alterar endpoints, persistência, regras de operações ou adicionar dados históricos/streaming.
- Fazer chamadas de retry automáticas após limite de uso.

## Decisions

### Usar `GET /quote` como fonte única para ações dos EUA

O endpoint `/quote` do Twelve Data retorna `name`, `close` e `timestamp` para um símbolo, cobrindo os dados hoje montados por duas chamadas. O adaptador aceitará somente uma resposta com esses três valores válidos; `timestamp` será convertido de epoch em UTC e `close` em decimal.

Alternativas consideradas:

- Combinar `/price` e um endpoint de perfil: descartada, pois volta a exigir múltiplas chamadas e não melhora o contrato necessário.
- Usar `/eod`: descartada, pois fornece fechamento sem o nome da empresa na resposta contratada.

### Configuração isolada e autenticação por header

O novo cliente usará `TWELVE_DATA_URL` (padrão `https://api.twelvedata.com`) e `TWELVE_DATA_API_KEY`. A chave será enviada em `Authorization: apikey <chave>`, método recomendado pelo provedor, para não integrar a credencial à URL registrada pela telemetria. As propriedades Alpha Vantage, cliente, DTOs, adaptador, testes e menções na documentação serão removidos ou substituídos juntos.

Alternativa considerada: passar `apikey` como query string. É suportado pelo Twelve Data, mas aumenta o risco de registro acidental da credencial em URLs.

### Reutilizar o contrato e o mapeamento de falhas existentes

`DefaultCotacaoAdapter` continuará escolhendo Brapi para `BRASIL` e o novo adaptador para `EUA`. Erros de HTTP e de rede seguirão o tratamento central existente; respostas HTTP bem-sucedidas com campos ausentes, nulos, vazios ou não numéricos serão convertidas em `EXTERNAL_API_INVALID_RESPONSE`. Nenhum dado persistido será alterado quando a consulta falhar.

## Risks / Trade-offs

- [Plano/chave sem acesso ao símbolo ou limite de créditos] → mapear respostas de autenticação, símbolo ausente e rate limit às categorias de erro existentes e cobrir com testes.
- [Preço `close` pode representar o último fechamento disponível quando o mercado estiver fechado] → persistir a semântica retornada pelo provedor e seu timestamp, sem estimativas.
- [Mudança no contrato JSON do provedor] → DTO mínimo, validação explícita e testes de desserialização para resposta válida, de erro e incompleta.
- [Configurações antigas deixadas em ambientes] → atualizar README e arquivos de exemplo, documentando a remoção das variáveis Alpha Vantage.

## Migration Plan

1. Configurar `TWELVE_DATA_URL` e `TWELVE_DATA_API_KEY` nos ambientes antes da implantação.
2. Implantar a versão que usa Twelve Data e observar os logs estruturados de chamadas externas e as categorias de falha.
3. Remover as variáveis Alpha Vantage dos ambientes após a implantação confirmada.
4. Em caso de falha operacional, restaurar a versão anterior e suas variáveis Alpha Vantage; não há migração de banco de dados.
