import { Mercado, Moeda, Operacao } from '../models/domain.models';

export interface OperationAnalyticsItem { label: string; value: number; moeda: Moeda; }
export const cashFlowItems = (operations: readonly Operacao[], moeda: Moeda, mercado?: Mercado): OperationAnalyticsItem[] => {
  const totals = new Map<string, number>();
  operations.filter((item) => item.moeda === moeda && (!mercado || item.mercado === mercado)).forEach((item) => {
    const month = item.dataHora.slice(0, 7);
    totals.set(month, (totals.get(month) ?? 0) + (item.tipo === 'COMPRA' ? -item.valorLiquido : item.valorLiquido));
  });
  return [...totals].map(([label, value]) => ({ label, value, moeda }));
};
export const tradedVolumeItems = (operations: readonly Operacao[], moeda: Moeda, mercado?: Mercado): OperationAnalyticsItem[] => {
  const totals = new Map<string, number>();
  operations.filter((item) => item.moeda === moeda && (!mercado || item.mercado === mercado)).forEach((item) => totals.set(item.ticker, (totals.get(item.ticker) ?? 0) + item.valorLiquido));
  return [...totals].map(([label, value]) => ({ label, value, moeda }));
};
