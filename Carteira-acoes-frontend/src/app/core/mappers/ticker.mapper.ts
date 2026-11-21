import { Page } from '../models/domain.models';

export function normalizeTicker<T extends { ticker: string }>(item: T): T {
  return { ...item, ticker: item.ticker.toUpperCase() };
}

export function normalizeTickerPage<T extends { ticker: string }>(page: Page<T>): Page<T> {
  return { ...page, content: page.content.map(normalizeTicker) };
}
