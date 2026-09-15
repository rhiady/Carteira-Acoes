import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { formatMoney, formatSignedMoney } from '../../core/formatters/financial.formatter';
import { Carteira, Moeda, Page, ResumoCarteira } from '../../core/models/domain.models';
import { CarteiraService } from '../../core/services/carteira.service';
import { AuthService } from '../../core/services/auth.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { CarteiraDialogComponent } from '../../shared/components/registration-dialogs.component';
import { EmptyStateComponent, ErrorStateComponent, LoadingStateComponent, PaginationComponent } from '../../shared/components/ui-states.component';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { CarteiraEditDialogComponent } from '../../shared/components/carteira-edit-dialog.component';
import { maintenanceError } from '../../core/services/maintenance-error';

interface TickerPreview {
  readonly tickers: readonly string[];
  readonly remaining: number;
}

@Component({
  selector: 'app-carteiras-page',
  imports: [MatButtonModule, MatMenuModule, RouterLink, EmptyStateComponent, ErrorStateComponent, LoadingStateComponent, PaginationComponent],
  template: `
    <section>
      <div class="page-heading"><div><p class="eyebrow">Investimentos</p><h1>Minhas carteiras</h1><p>Resultado e composição dos seus investimentos.</p></div><button mat-flat-button type="button" (click)="openDialog()">Nova carteira</button></div>
      @if (loading()) { <app-loading-state message="Carregando carteiras…" /> }
      @else if (error()) { <app-error-state [message]="error()" (retry)="load()" /> }
      @else if (!page()?.content?.length) { <app-empty-state message="Você ainda não possui carteiras." /><a class="empty-state-action" routerLink="/carteiras/nova">Criar primeira carteira</a> }
      @else {
        <div class="portfolio-card-grid">
          @for (item of page()!.content; track item.id) {
            <article class="portfolio-summary-card">
              <div class="portfolio-summary-card__header"><div><h2>{{ item.nome }}</h2><p>Corretora #{{ item.corretoraId }}</p></div><span class="portfolio-summary-card__id">Carteira #{{ item.id }}</span></div>
              <div class="portfolio-summary-card__content">
                @if (summaries().get(item.id); as summary) {
                  @for (subtotal of summary.resumosPorMoeda; track subtotal.moeda) { <div class="portfolio-summary-card__metric"><span>Valor atual · {{ subtotal.moeda }}</span><strong>{{ money(subtotal.valorAtual, subtotal.moeda) }}</strong><small [class]="resultClass(subtotal.lucroPrejuizo)">Lucro / prejuízo: {{ signedMoney(subtotal.lucroPrejuizo, subtotal.moeda) }}</small></div> }
                  <p class="portfolio-summary-card__assets">{{ summary.quantidadeAtivos }} ativo{{ summary.quantidadeAtivos === 1 ? '' : 's' }}</p>
                } @else { <p>Resumo financeiro indisponível.</p> }
                @if (tickerPreviews().get(item.id); as preview) {
                  @if (preview.tickers.length) {
                    <div class="ticker-preview" [attr.aria-label]="'Ativos da carteira ' + item.nome"><span>Ativos</span><p>@for (ticker of preview.tickers; track ticker) { <b>{{ ticker }}</b> } @if (preview.remaining) { <em>+{{ preview.remaining }} outro{{ preview.remaining === 1 ? '' : 's' }}</em> }</p></div>
                  }
                }
              </div>
              <div class="portfolio-summary-card__actions"><a class="portfolio-summary-card__link" [routerLink]="['/carteiras', item.id]">Ver carteira <span aria-hidden="true">→</span></a><button mat-button type="button" [matMenuTriggerFor]="walletActions" [attr.aria-label]="'Mais ações para ' + item.nome">Mais ações</button><mat-menu #walletActions="matMenu"><button mat-menu-item type="button" (click)="edit(item)">Editar nome</button><button mat-menu-item type="button" (click)="remove(item)">Excluir carteira</button></mat-menu></div>
            </article>
          }
        </div>
        <app-pagination [page]="page()!.number" [totalPages]="page()!.totalPages" [first]="page()!.first" [last]="page()!.last" (previous)="load(page()!.number - 1)" (next)="load(page()!.number + 1)" />
      }
    </section>`,
})
export class CarteirasPage {
  private readonly service = inject(CarteiraService);
  private readonly dialog = inject(MatDialog);
  private readonly auth = inject(AuthService);
  private readonly feedback = inject(FeedbackService);
  protected readonly page = signal<Page<Carteira> | null>(null);
  protected readonly summaries = signal(new Map<number, ResumoCarteira>());
  protected readonly tickerPreviews = signal(new Map<number, TickerPreview>());
  protected readonly loading = signal(true);
  protected readonly error = signal('');

  constructor() { this.load(); }

  protected load(index = 0) {
    this.loading.set(true); this.error.set('');
    const userId = this.auth.currentUser()?.id;
    if (!userId) { this.page.set({ content: [], totalElements: 0, totalPages: 0, size: 10, number: 0, first: true, last: true, numberOfElements: 0, empty: true }); this.loading.set(false); return; }
    this.service.listForUser(userId, { page: index, size: 10, sort: 'nome,asc' }).subscribe({
      next: (page) => {
        this.page.set(page); this.summaries.set(new Map()); this.tickerPreviews.set(new Map());
        page.content.forEach((portfolio) => {
          this.service.summary(portfolio.id).subscribe({ next: (summary) => this.summaries.update((current) => new Map(current).set(portfolio.id, summary)) });
          this.service.positions(portfolio.id, { page: 0, size: 3, sort: 'ticker,asc' }).subscribe({
            next: (positions) => this.tickerPreviews.update((current) => new Map(current).set(portfolio.id, {
              tickers: positions.content.map((position) => position.ticker),
              remaining: Math.max(positions.totalElements - positions.content.length, 0),
            })),
          });
        });
        this.loading.set(false);
      },
      error: (httpError: { message?: string }) => { this.error.set(httpError.message ?? 'Não foi possível carregar as carteiras.'); this.loading.set(false); },
    });
  }

  protected openDialog() { this.dialog.open(CarteiraDialogComponent, { autoFocus: 'first-tabbable', width: 'min(100% - 2rem,32rem)' }).afterClosed().subscribe((created) => { if (created) this.load(this.page()?.number); }); }
  protected edit(item: Carteira) { this.dialog.open(CarteiraEditDialogComponent, { data: item, autoFocus: 'first-tabbable', width: 'min(100% - 2rem,32rem)' }).afterClosed().subscribe((updated: Carteira | undefined) => { if (!updated || !this.page()) return; this.page.update((current) => current ? { ...current, content: current.content.map((entry) => entry.id === updated.id ? { ...entry, nome: updated.nome } : entry) } : current); }); }
  protected remove(item: Carteira) { this.dialog.open(ConfirmDialogComponent, { data: { title: 'Excluir carteira?', message: `Você está prestes a excluir "${item.nome}". A carteira só poderá ser excluída se não possuir registros vinculados.`, confirmLabel: 'Excluir carteira' }, autoFocus: 'first-tabbable', width: 'min(100% - 2rem,32rem)' }).afterClosed().subscribe((confirmed) => { if (!confirmed) return; this.service.delete(item.id).subscribe({ next: () => { this.feedback.success('Carteira excluída com sucesso.'); this.page.update((current) => current ? { ...current, content: current.content.filter((entry) => entry.id !== item.id), totalElements: Math.max(current.totalElements - 1, 0) } : current); }, error: (error: unknown) => this.feedback.error(maintenanceError(error, 'Não foi possível excluir a carteira.', 'Esta carteira não está mais disponível.')) }); }); }
  protected money(value: number | null | undefined, currency: Moeda) { return formatMoney(value, currency); }
  protected signedMoney(value: number | null | undefined, currency: Moeda) { return formatSignedMoney(value, currency); }
  protected resultClass(value: number | null | undefined) { return value === null || value === undefined ? '' : value < 0 ? 'negative-value' : value > 0 ? 'positive-value' : 'neutral-value'; }
}
