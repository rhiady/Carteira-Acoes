import { describe, expect, it } from 'vitest';
import { formatSignedMoney, priceVariationPercent } from './financial.formatter';

describe('formatSignedMoney', () => {
  it('puts the negative sign before the formatted amount', () => {
    expect(formatSignedMoney(-120.1, 'BRL')).toBe('- R$ 120,10');
  });

  it('does not represent unavailable money as zero', () => {
    expect(formatSignedMoney(null, 'BRL')).toBe('—');
  });

  it('derives presentation variation only with a non-zero average', () => {
    expect(priceVariationPercent(120, 100)).toBe(20);
    expect(priceVariationPercent(120, 0)).toBeNull();
    expect(priceVariationPercent(null, 100)).toBeNull();
  });
});
