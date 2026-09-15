import { describe, expect, it } from 'vitest';
import { PosicaoAcao } from '../models/domain.models';
import { compositionItems, resultItems } from './portfolio-analytics.mapper';

const position: PosicaoAcao = { carteiraId: 1, acaoId: 10, ticker: 'PETR4', nomeEmpresa: 'Petrobras', mercado: 'BRASIL', moeda: 'BRL', quantidade: 15, cotacaoAtual: 45, dataHoraCotacao: '2026-09-03T12:00:00Z', valorAtual: 675, precoMedio: 35, precoMedioVenda: null, lucroPrejuizoRealizado: 50, lucroPrejuizoNaoRealizado: 150, lucroPrejuizo: 200 };

describe('portfolio analytics mappers', () => {
  it('keeps authoritative values separated by currency', () => {
    const usd = { ...position, acaoId: 11, ticker: 'AAPL', moeda: 'USD' as const, valorAtual: 100, lucroPrejuizo: -20 };
    expect(compositionItems([position, usd], 'BRL')).toEqual([{ label: 'PETR4', value: 675, moeda: 'BRL' }]);
    expect(resultItems([position, usd], 'USD')).toEqual([{ label: 'AAPL', value: -20, moeda: 'USD', direction: 'negative' }]);
  });

  it('omits unavailable analytics instead of treating them as zero', () => {
    const unavailable = { ...position, valorAtual: null, lucroPrejuizo: null };
    expect(compositionItems([unavailable], 'BRL')).toEqual([]);
    expect(resultItems([unavailable], 'BRL')).toEqual([]);
  });

  it('preserves negative, positive and neutral authoritative result signs', () => {
    const negative = { ...position, ticker: 'LOSS3', lucroPrejuizo: -5 };
    const neutral = { ...position, ticker: 'FLAT3', lucroPrejuizo: 0 };

    expect(resultItems([negative, position, neutral], 'BRL')).toEqual([
      { label: 'LOSS3', value: -5, moeda: 'BRL', direction: 'negative' },
      { label: 'PETR4', value: 200, moeda: 'BRL', direction: 'positive' },
      { label: 'FLAT3', value: 0, moeda: 'BRL', direction: 'neutral' },
    ]);
  });
});
