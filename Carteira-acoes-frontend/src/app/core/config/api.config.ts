import { environment } from '../../../environments/environment';

export const apiConfig = {
  baseUrl: environment.apiBaseUrl,
  endpoint: (path: string) => `${environment.apiBaseUrl.replace(/\/$/, '')}/${path.replace(/^\//, '')}`,
} as const;
