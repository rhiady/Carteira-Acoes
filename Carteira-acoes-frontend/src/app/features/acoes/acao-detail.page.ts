import { Component, computed, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApexAnnotations, ApexAxisChartSeries, ApexChart, ApexXAxis, ApexYAxis } from 'ng-apexcharts';
import { Acao, HistoricoCotacao, Page } from '../../core/models/domain.models';
import { quoteHistorySeries } from '../../core/mappers/quote-history.mapper';
import { AcaoService } from '../../core/services/acao.service';
import { CarteiraService } from '../../core/services/carteira.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { ChartPanelComponent } from '../../shared/components/chart-panel.component';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { maintenanceError } from '../../core/services/maintenance-error';

type HistoryPeriod = '7D' | '30D' | '90D' | 'TUDO';

@Component({
  selector: 'app-acao-detail-page',
  imports: [RouterLink, CurrencyPipe, DatePipe, MatButtonModule, MatMenuModule, ChartPanelComponent],
  template: `
    <section>@if (loading()) { <p role="status">Carregando ação…</p> } @else if (item()) {
      <div class="page-heading"><div><h1>{{ item()!.ticker }}</h1><p>{{ item()!.nomeEmpresa }} · {{ item()!.mercado }} · {{ item()!.moeda }}</p></div><nav class="page-actions" aria-label="Ações do ativo"><a mat-flat-button routerLink="/carteiras">Comprar {{ item()!.ticker }}</a><button mat-stroked-button type="button" [disabled]="updating()" (click)="updateQuote()">{{ updating() ? 'Atualizando…' : '↻ Atualizar cotação' }}</button><button mat-button type="button" [matMenuTriggerFor]="assetActions" aria-label="Mais ações do ativo">Mais ações</button><mat-menu #assetActions="matMenu"><button mat-menu-item type="button" (click)="remove()">Excluir ação</button></mat-menu></nav></div>
      <section class="quote-highlight"><span>Cotação atual</span><strong>{{ item()!.cotacaoAtual | currency:item()!.moeda }}</strong><small>Atualizada em {{ item()!.dataHoraCotacao | date:'short' }}</small>@if (averagePrice() !== undefined) { <small>Preço médio: {{ averagePrice() === null ? '—' : (averagePrice() | currency:item()!.moeda) }}</small> }</section>
      @if (error()) { <p class="error" role="alert">{{ error() }}</p> }
      <nav class="period-controls" aria-label="Período do histórico">@for (option of periods; track option) { <button mat-stroked-button type="button" [class.active-period]="period() === option" (click)="selectPeriod(option)">{{ option === 'TUDO' ? 'Tudo' : option }}</button> }</nav>
      <app-chart-panel title="Evolução da cotação" [chart]="chart" [series]="series()" [xaxis]="xaxis" [yaxis]="yaxis" [annotations]="annotations()" [loading]="historyLoading()" [error]="historyError()" [retryable]="!!historyError()" (retry)="retryHistory()" [hasData]="history().length > 0" emptyMessage="Ainda não há histórico suficiente. Atualize a cotação para começar a registrar o gráfico." [summary]="historySummary()" />
    } @else { <p class="error" role="alert">{{ error() }}</p> }<a routerLink="/acoes">Voltar</a></section>`,
})
export class AcaoDetailPage {
  private readonly service = inject(AcaoService);
  private readonly portfolios = inject(CarteiraService);
  private readonly feedback = inject(FeedbackService);
  private readonly route = inject(ActivatedRoute);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);
  private readonly id = Number(this.route.snapshot.paramMap.get('id'));
  protected readonly item = signal<Acao | null>(null);
  protected readonly loading = signal(true);
  protected readonly updating = signal(false);
  protected readonly error = signal('');
  protected readonly history = signal<HistoricoCotacao[]>([]);
  protected readonly historyLoading = signal(true);
  protected readonly historyError = signal('');
  protected readonly averagePrice = signal<number | null | undefined>(undefined);
  protected readonly period = signal<HistoryPeriod>('30D');
  protected readonly periods: readonly HistoryPeriod[] = ['7D', '30D', '90D', 'TUDO'];
  protected readonly series = computed<ApexAxisChartSeries>(() => [quoteHistorySeries(this.history(), this.item()?.moeda ?? 'BRL')]);
  protected readonly historySummary = computed(() => this.history().map((point) => `${point.dataHora}: ${point.cotacao}`));
  protected readonly chart: ApexChart = { type: 'line', height: 280, toolbar: { show: false } };
  protected readonly xaxis: ApexXAxis = { type: 'datetime' };
  protected readonly yaxis: ApexYAxis = { decimalsInFloat: 2 };
  protected readonly annotations = computed<ApexAnnotations>(() => this.averagePrice() === null || this.averagePrice() === undefined ? {} : { yaxis: [{ y: this.averagePrice()!, borderColor: 'var(--color-blue)', label: { text: 'Preço médio', style: { color: 'var(--color-canvas)', background: 'var(--color-blue)' } } }] });

  constructor() { this.load(); }

  protected selectPeriod(period: HistoryPeriod) { this.period.set(period); this.loadHistory(); }

  protected updateQuote() {
    this.updating.set(true); this.error.set('');
    this.service.updateQuote(this.id).subscribe({
      next: () => this.reloadAfterQuoteUpdate(),
      error: (error: { message?: string }) => { const message = error.message ?? 'Não foi possível atualizar a cotação.'; this.error.set(message); this.feedback.error(message); this.updating.set(false); },
    });
  }

  protected retryHistory() { this.loadHistory(); }

  protected remove() {
    const item = this.item();
    if (!item) return;
    this.dialog.open(ConfirmDialogComponent, { data: { title: 'Excluir ação?', message: `Você está prestes a excluir ${item.ticker} — ${item.nomeEmpresa}. A ação só poderá ser removida se não possuir registros vinculados.`, confirmLabel: 'Excluir ação' }, autoFocus: 'first-tabbable', width: 'min(100% - 2rem,32rem)' }).afterClosed().subscribe((confirmed) => { if (!confirmed) return; this.updating.set(true); this.service.delete(item.id).subscribe({ next: () => { this.feedback.success('Ação excluída com sucesso.'); this.router.navigateByUrl('/acoes'); }, error: (error: unknown) => { this.feedback.error(maintenanceError(error, 'Não foi possível excluir a ação.', 'Esta ação não está mais disponível.')); this.updating.set(false); } }); });
  }

  private load() {
    this.service.get(this.id).subscribe({ next: (item) => { this.item.set(item); this.loading.set(false); this.loadHistory(); this.loadAveragePrice(); }, error: (error: { message?: string }) => { this.error.set(error.message ?? 'Não foi possível carregar a ação.'); this.loading.set(false); this.historyLoading.set(false); } });
  }

  private loadAveragePrice() {
    const carteiraId = Number(this.route.snapshot.queryParamMap?.get('carteiraId'));
    if (!Number.isInteger(carteiraId) || carteiraId < 1 || !this.portfolios.positions) return;
    this.portfolios.positions(carteiraId, { page: 0, size: 100 }).subscribe({
      next: (page) => this.averagePrice.set(page.content.find((position) => position.acaoId === this.id)?.precoMedio ?? null),
    });
  }

  private reloadAfterQuoteUpdate() {
    this.loading.set(true);
    this.service.get(this.id).subscribe({
      next: (item) => { this.item.set(item); this.loading.set(false); this.updating.set(false); this.feedback.success('Cotação atualizada.'); this.loadHistory(); },
      error: (error: { message?: string }) => { const message = error.message ?? 'A cotação foi atualizada, mas não foi possível recarregar o ativo.'; this.error.set(message); this.feedback.error(message); this.loading.set(false); this.updating.set(false); },
    });
  }

  private loadHistory() {
    this.historyLoading.set(true); this.historyError.set(''); this.history.set([]);
    this.service.history(this.id, this.historyParams()).subscribe({
      next: (page: Page<HistoricoCotacao>) => { this.history.set(page.content); this.historyLoading.set(false); },
      error: (error: { message?: string }) => { this.historyError.set(error.message ?? 'Não foi possível carregar o histórico.'); this.historyLoading.set(false); },
    });
  }

  private historyParams() {
    const days = this.period() === 'TUDO' ? null : Number.parseInt(this.period(), 10);
    return days === null ? {} : { inicio: new Date(Date.now() - days * 86_400_000).toISOString() };
  }
}
