import { describe, expect, it } from 'vitest';
import { Operacao } from '../models/domain.models';
import { cashFlowItems, tradedVolumeItems } from './operation-analytics.mapper';

const operation = (overrides: Partial<Operacao>): Operacao => ({ id: 1, carteiraId: 1, acaoId: 1, ticker: 'PETR4', nomeEmpresa: 'Petrobras', mercado: 'BRASIL', moeda: 'BRL', tipo: 'COMPRA', quantidade: 1, precoUnitario: 10, valorBruto: 10, corretagem: 0, impostos: 0, valorAdicional: 0, valorLiquido: 10, dataHora: '2026-01-10T12:00:00Z', createdAt: '2026-01-10T12:00:00Z', ...overrides });

describe('operation analytics mappers', () => {
  it('keeps cash flow and volume separated by currency', () => {
    const items = [operation({}), operation({ id: 2, tipo: 'VENDA', valorLiquido: 4 }), operation({ id: 3, moeda: 'USD', ticker: 'AAPL', valorLiquido: 20 })];
    expect(cashFlowItems(items, 'BRL')).toEqual([{ label: '2026-01', value: -6, moeda: 'BRL' }]);
    expect(tradedVolumeItems(items, 'USD')).toEqual([{ label: 'AAPL', value: 20, moeda: 'USD' }]);
  });
});
