import { describe, expect, it } from 'vitest';
import { apiConfig } from './api.config';

describe('apiConfig', () => {
  it('composes an API endpoint from the configured base path', () => {
    expect(apiConfig.endpoint('/usuarios')).toBe('/api/usuarios');
  });

  it('accepts paths without a leading slash', () => {
    expect(apiConfig.endpoint('acoes')).toBe('/api/acoes');
  });
});
