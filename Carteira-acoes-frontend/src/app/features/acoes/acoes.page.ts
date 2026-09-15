import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { Acao, Page } from '../../core/models/domain.models';
import { AcaoService } from '../../core/services/acao.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { EmptyStateComponent, ErrorStateComponent, LoadingStateComponent, PaginationComponent } from '../../shared/components/ui-states.component';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { maintenanceError } from '../../core/services/maintenance-error';

@Component({
  selector: 'app-acoes-page',
  imports: [MatButtonModule, MatMenuModule, RouterLink, CurrencyPipe, DatePipe, ReactiveFormsModule, EmptyStateComponent, ErrorStateComponent, LoadingStateComponent, PaginationComponent],
  template: `
    <section>
      <div class="page-heading"><div><p class="eyebrow">Mercado</p><h1>Ações</h1><p>Consulte ativos existentes e mantenha as cotações atualizadas.</p></div><button mat-stroked-button type="button" [disabled]="refreshing()" (click)="refreshQuotes()">{{ refreshing() ? 'Atualizando cotações…' : 'Atualizar cotações' }}</button></div>
      <form class="search-form action-search" (ngSubmit)="search()"><label for="ticker-search">Buscar por ticker</label><input id="ticker-search" [formControl]="ticker" placeholder="Ex.: PETR4" aria-describedby="ticker-search-help" /><p id="ticker-search-help" class="helper-text">A busca abre o ativo existente; novas ações são fornecidas pela integração de cotações.</p><div class="page-actions"><button mat-stroked-button type="submit" [disabled]="ticker.invalid || searching()">{{ searching() ? 'Buscando…' : 'Buscar ação' }}</button><button mat-button type="button" (click)="clearSearch()">Limpar busca</button></div></form>
      @if (loading()) { <app-loading-state message="Carregando ações…" /> }
      @else if (error()) { <app-error-state [message]="error()" (retry)="load()" /> }
      @else {
        <div class="metric-grid" aria-label="Resumo dos ativos cadastrados">
          <article class="financial-metric"><span>Ativos cadastrados</span><strong>{{ page()?.totalElements ?? '—' }}</strong></article>
          <article class="financial-metric"><span>Brasil nesta página</span><strong>{{ brazilCount() }}</strong></article>
          <article class="financial-metric"><span>EUA nesta página</span><strong>{{ usaCount() }}</strong></article>
        </div>
        @if (!page()?.content?.length) { <app-empty-state message="Nenhuma ação encontrada." /> }
        @else {
          <div class="asset-list" aria-label="Lista de ativos">
            @for (item of page()!.content; track item.id) {
              <article class="asset-row">
                <div><h2>{{ item.ticker }}</h2><p>{{ item.nomeEmpresa }}</p></div>
                <div><span>Mercado</span><strong>{{ item.mercado === 'BRASIL' ? 'Brasil' : 'EUA' }}</strong></div>
                <div><span>Cotação</span><strong>{{ item.cotacaoAtual | currency:item.moeda }}</strong><small>Atualizada em {{ item.dataHoraCotacao | date:'short' }}</small></div>
                <div><a mat-button [routerLink]="['/acoes', item.id]" [attr.aria-label]="'Ver ativo ' + item.ticker">Ver ativo</a><button mat-button type="button" [matMenuTriggerFor]="assetActions" [attr.aria-label]="'Mais ações para ' + item.ticker">Mais ações</button><mat-menu #assetActions="matMenu"><button mat-menu-item type="button" (click)="remove(item)">Excluir ação</button></mat-menu></div>
              </article>
            }
          </div>
          <app-pagination [page]="page()!.number" [totalPages]="page()!.totalPages" [first]="page()!.first" [last]="page()!.last" (previous)="load(page()!.number - 1)" (next)="load(page()!.number + 1)" />
        }
      }
    </section>`,
})
export class AcoesPage {
  private readonly service = inject(AcaoService);
  private readonly dialog = inject(MatDialog);
  private readonly feedback = inject(FeedbackService);
  protected readonly page = signal<Page<Acao> | null>(null);
  protected readonly loading = signal(true);
  protected readonly searching = signal(false);
  protected readonly refreshing = signal(false);
  protected readonly error = signal('');
  protected readonly ticker = new FormControl('', { nonNullable: true, validators: Validators.required });
  protected readonly brazilCount = computed(() => this.page()?.content.filter((item) => item.mercado === 'BRASIL').length ?? 0);
  protected readonly usaCount = computed(() => this.page()?.content.filter((item) => item.mercado === 'EUA').length ?? 0);

  constructor() { this.load(); }

  protected load(index = 0) {
    this.loading.set(true); this.error.set('');
    this.service.list({ page: index, size: 10, sort: 'ticker,asc' }).subscribe({
      next: (page) => { this.page.set(page); this.loading.set(false); },
      error: (httpError: { message?: string }) => { this.error.set(httpError.message ?? 'Não foi possível carregar as ações.'); this.loading.set(false); },
    });
  }

  protected refreshQuotes() {
    if (this.refreshing()) return;
    this.refreshing.set(true);
    this.service.updateAllQuotes().subscribe({
      next: (result) => {
        const message = this.refreshOutcome(result);
        result.atualizadas === 0 || result.falhas > 0 ? this.feedback.error(message) : this.feedback.success(message);
        this.load(this.page()?.number);
      },
      error: (httpError: { message?: string }) => this.feedback.error(httpError.message ?? 'Não foi possível atualizar as cotações.'),
      complete: () => this.refreshing.set(false),
    });
  }

  protected search() {
    if (this.ticker.invalid) return;
    this.searching.set(true); this.error.set('');
    this.service.getByTicker(this.ticker.value.trim().toUpperCase()).subscribe({
      next: (item) => { this.page.set({ content: [item], totalElements: 1, totalPages: 1, size: 1, number: 0, first: true, last: true, numberOfElements: 1, empty: false }); this.searching.set(false); this.loading.set(false); },
      error: (httpError: { message?: string }) => { this.error.set(httpError.message ?? 'Ação não encontrada.'); this.searching.set(false); },
    });
  }

  protected clearSearch() { this.ticker.setValue(''); this.load(); }

  protected remove(item: Acao) { this.dialog.open(ConfirmDialogComponent, { data: { title: 'Excluir ação?', message: `Você está prestes a excluir ${item.ticker} — ${item.nomeEmpresa}. A ação só poderá ser removida se não possuir registros vinculados.`, confirmLabel: 'Excluir ação' }, autoFocus: 'first-tabbable', width: 'min(100% - 2rem,32rem)' }).afterClosed().subscribe((confirmed) => { if (!confirmed) return; this.service.delete(item.id).subscribe({ next: () => { this.feedback.success('Ação excluída com sucesso.'); this.page.update((current) => current ? { ...current, content: current.content.filter((entry) => entry.id !== item.id), totalElements: Math.max(current.totalElements - 1, 0) } : current); }, error: (error: unknown) => this.feedback.error(maintenanceError(error, 'Não foi possível excluir a ação.', 'Esta ação não está mais disponível.')) }); }); }

  private refreshOutcome(result: { total: number; atualizadas: number; falhas: number; dataHora: string }) {
    const completedAt = new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(result.dataHora));
    if (result.atualizadas === 0) return `Nenhuma cotação foi atualizada; ${result.falhas} falharam. Concluído em ${completedAt}.`;
    if (result.falhas > 0) return `${result.atualizadas} de ${result.total} cotações atualizadas; ${result.falhas} falharam. Concluído em ${completedAt}.`;
    return `${result.atualizadas} de ${result.total} cotações atualizadas. Concluído em ${completedAt}.`;
  }
}
