import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { RouterLink } from '@angular/router';
import { Carteira, Page } from '../../core/models/domain.models';
import { CarteiraService } from '../../core/services/carteira.service';
import { AuthService } from '../../core/services/auth.service';
import { EmptyStateComponent, ErrorStateComponent, LoadingStateComponent } from '../../shared/components/ui-states.component';

@Component({
  selector: 'app-operacoes-page',
  imports: [MatButtonModule, MatSelectModule, RouterLink, EmptyStateComponent, ErrorStateComponent, LoadingStateComponent],
  template: `
    <section>
      <div class="page-heading"><div><p class="eyebrow">Histórico financeiro</p><h1>Operações</h1><p>Escolha uma carteira para consultar compras, vendas e custos registrados.</p></div></div>
      @if (loading()) {
        <app-loading-state message="Carregando carteiras…" />
      } @else if (error()) {
        <app-error-state [message]="error()" (retry)="load()" />
      } @else if (!page()?.content?.length) {
        <app-empty-state message="Crie uma carteira para consultar as operações." />
      } @else {
        <div class="section-heading"><div><h2>Carteira selecionada</h2><p>O histórico permanece separado por carteira e por moeda.</p></div></div>
        <div class="portfolio-operation-list" aria-label="Selecione uma carteira para consultar operações">
          @for (carteira of page()!.content; track carteira.id) {
            <a mat-stroked-button [routerLink]="['/carteiras', carteira.id, 'operacoes']"><span>{{ carteira.nome }}</span><small>Consultar histórico →</small></a>
          }
        </div>
      }
    </section>
  `,
})
export class OperacoesPage {
  private readonly carteiras = inject(CarteiraService);
  private readonly auth = inject(AuthService);
  protected readonly page = signal<Page<Carteira> | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal('');

  constructor() { this.load(); }

  protected load() {
    this.loading.set(true);
    this.error.set('');
    const userId = this.auth.currentUser()?.id;
    if (!userId) { this.page.set({ content: [], totalElements: 0, totalPages: 0, size: 20, number: 0, first: true, last: true, numberOfElements: 0, empty: true }); this.loading.set(false); return; }
    this.carteiras.listForUser(userId).subscribe({
      next: (page) => { this.page.set(page); this.loading.set(false); },
      error: (error: { message?: string }) => { this.error.set(error.message ?? 'Não foi possível carregar as carteiras.'); this.loading.set(false); },
    });
  }
}
