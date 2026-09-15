import { HistoricoCotacao, Moeda } from '../models/domain.models';

export interface QuoteHistoryPoint { x: number; y: number; }

export const quoteHistoryPoints = (history: readonly HistoricoCotacao[]): QuoteHistoryPoint[] => history
  .map((point) => ({ x: Date.parse(point.dataHora), y: point.cotacao }))
  .filter((point) => !Number.isNaN(point.x));

export const quoteHistorySeries = (history: readonly HistoricoCotacao[], moeda: Moeda) => ({
  name: moeda === 'BRL' ? 'Cotação (R$)' : 'Quote (US$)',
  data: quoteHistoryPoints(history),
});
