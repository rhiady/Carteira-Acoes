import { PosicaoAcao, ResumoPorMoeda } from '../models/domain.models';

export interface PortfolioChartItem { label: string; value: number; moeda: PosicaoAcao['moeda']; }
export type ResultDirection = 'negative' | 'neutral' | 'positive';
export interface PortfolioResultChartItem extends PortfolioChartItem { direction: ResultDirection; }
export interface PriceComparisonItem { label: string; average: number; current: number; moeda: PosicaoAcao['moeda']; }

export const resultDirection = (value: number): ResultDirection => value < 0 ? 'negative' : value > 0 ? 'positive' : 'neutral';

export const compositionItems = (positions: readonly PosicaoAcao[], moeda: PosicaoAcao['moeda']) => positions
  .filter((position) => position.moeda === moeda && Number.isFinite(position.valorAtual))
  .map((position) => ({ label: position.ticker, value: position.valorAtual!, moeda }));

export const resultItems = (positions: readonly PosicaoAcao[], moeda: PosicaoAcao['moeda']) => positions
  .filter((position) => position.moeda === moeda && Number.isFinite(position.lucroPrejuizo))
  .map((position): PortfolioResultChartItem => ({
    label: position.ticker,
    value: position.lucroPrejuizo!,
    moeda,
    direction: resultDirection(position.lucroPrejuizo!),
  }));

export const priceComparisonItems = (positions: readonly PosicaoAcao[], moeda: PosicaoAcao['moeda']) => positions
  .filter((position) => position.moeda === moeda && Number.isFinite(position.precoMedio) && Number.isFinite(position.cotacaoAtual))
  .map((position): PriceComparisonItem => ({ label: position.ticker, average: position.precoMedio!, current: position.cotacaoAtual!, moeda }));

export const walletComparisonItems = (summary: ResumoPorMoeda) => summary.valorInvestidoAtual !== null && summary.valorInvestidoAtual !== undefined && summary.valorAtual !== null
  ? [{ label: 'Investido', value: summary.valorInvestidoAtual }, { label: 'Atual', value: summary.valorAtual }]
  : [];
export const walletResultItems = (summary: ResumoPorMoeda) => summary.lucroPrejuizoRealizado !== null && summary.lucroPrejuizoRealizado !== undefined && summary.lucroPrejuizoNaoRealizado !== null && summary.lucroPrejuizoNaoRealizado !== undefined
  ? [{ label: 'Realizado', value: summary.lucroPrejuizoRealizado }, { label: 'Não realizado', value: summary.lucroPrejuizoNaoRealizado }]
  : [];
