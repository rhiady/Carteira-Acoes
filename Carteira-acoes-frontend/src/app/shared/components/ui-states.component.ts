import { Component, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({ selector: 'app-loading-state', imports: [MatProgressSpinnerModule], template: `<div class="ui-state" role="status" aria-live="polite"><mat-spinner diameter="32" aria-hidden="true" /><p>{{ message() }}</p></div>` })
export class LoadingStateComponent { readonly message = input('Carregando…'); }

@Component({ selector: 'app-empty-state', template: `<div class="ui-state empty-state"><h2>Nenhum resultado</h2><p>{{ message() }}</p></div>` })
export class EmptyStateComponent { readonly message = input.required<string>(); }

@Component({ selector: 'app-error-state', imports: [MatButtonModule], template: `<div class="ui-state error-state" role="alert"><p class="error">{{ message() }}</p><button mat-stroked-button type="button" (click)="retry.emit()">Tentar novamente</button></div>` })
export class ErrorStateComponent { readonly message = input.required<string>(); readonly retry = output<void>(); }

@Component({ selector: 'app-pagination', imports: [MatButtonModule], template: `<nav class="pagination" aria-label="Paginação"><button mat-stroked-button type="button" [disabled]="first()" (click)="previous.emit()">Anterior</button><span aria-live="polite">Página {{ page() + 1 }} de {{ totalPages() }}</span><button mat-stroked-button type="button" [disabled]="last()" (click)="next.emit()">Próxima</button></nav>` })
export class PaginationComponent { readonly page = input.required<number>(); readonly totalPages = input.required<number>(); readonly first = input.required<boolean>(); readonly last = input.required<boolean>(); readonly previous = output<void>(); readonly next = output<void>(); }
