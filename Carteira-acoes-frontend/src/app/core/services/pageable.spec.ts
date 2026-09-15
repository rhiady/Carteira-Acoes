import { describe, expect, it } from 'vitest';
import { pageableParams } from './pageable';

describe('pageableParams', () => {
  it('uses the documented defaults', () => {
    const params = pageableParams();
    expect(params.get('page')).toBe('0');
    expect(params.get('size')).toBe('20');
  });

  it('preserves every sort value', () => {
    const params = pageableParams({ page: 2, size: 10, sort: ['ticker,asc', 'id,desc'] });
    expect(params.get('page')).toBe('2');
    expect(params.get('size')).toBe('10');
    expect(params.getAll('sort')).toEqual(['ticker,asc', 'id,desc']);
  });
});
