import { HttpParams } from '@angular/common/http';
import { DEFAULT_PAGE_PARAMS, Pageable } from '../models/domain.models';

export const pageableParams = (pageable: Pageable = {}) => {
  const { page, size, sort } = { ...DEFAULT_PAGE_PARAMS, ...pageable };
  return (Array.isArray(sort) ? sort : [sort]).reduce(
    (params, value) => params.append('sort', value),
    new HttpParams().set('page', page).set('size', size),
  );
};
