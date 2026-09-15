export const formatMoney = (value: number | null | undefined, currency: string, locale = 'pt-BR') => {
  if (value === null || value === undefined) return '—';
  return new Intl.NumberFormat(locale, { style: 'currency', currency, minimumFractionDigits: 2 }).format(value);
};

export const formatSignedMoney = (value: number | null | undefined, currency: string, locale = 'pt-BR') => {
  if (value === null || value === undefined) return '—';
  const formatted = formatMoney(Math.abs(value), currency, locale);
  return value < 0 ? `- ${formatted}` : value > 0 ? `+ ${formatted}` : formatted;
};

export const priceVariationPercent = (current: number | null | undefined, average: number | null | undefined) => {
  if (current === null || current === undefined || average === null || average === undefined || average === 0) return null;
  const variation = ((current - average) / average) * 100;
  return Number.isFinite(variation) ? variation : null;
};
