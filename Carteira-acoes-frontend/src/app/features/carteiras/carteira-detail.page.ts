import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApexAxisChartSeries, ApexChart, ApexNonAxisChartSeries, ApexPlotOptions, ApexXAxis } from 'ng-apexcharts';
import { formatMoney, formatSignedMoney, priceVariationPercent } from '../../core/formatters/financial.formatter';
import { compositionItems, priceComparisonItems, resultItems } from '../../core/mappers/portfolio-analytics.mapper';
import { Moeda, Page, PosicaoAcao, ResumoCarteira } from '../../core/models/domain.models';
import { AcaoService } from '../../core/services/acao.service';
import { CarteiraService } from '../../core/services/carteira.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { ChartPanelComponent } from '../../shared/components/chart-panel.component';
import { EmptyStateComponent, ErrorStateComponent, LoadingStateComponent, PaginationComponent } from '../../shared/components/ui-states.component';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { CarteiraEditDialogComponent } from '../../shared/components/carteira-edit-dialog.component';
import { maintenanceError } from '../../core/services/maintenance-error';

@Component({
  selector: 'app-carteira-detail-page',
  imports: [MatButtonModule, MatMenuModule, MatTooltipModule, RouterLink, ChartPanelComponent, EmptyStateComponent, ErrorStateComponent, LoadingStateComponent, PaginationComponent],
  template: `
    <section>
      @if (loading()) { <app-loading-state message="Carregando carteira…" /> }
      @else if (error()) { <app-error-state [message]="error()" (retry)="load()" /><a class="back-link" routerLink="/carteiras">← Voltar para minhas carteiras</a> }
      @else if (summary()) {
        <a class="back-link" routerLink="/carteiras">← Carteiras</a>
        <div class="page-heading">
          <div><h1>{{ summary()!.nome }}</h1><p>Resumo e posições da sua carteira.</p></div>
          <nav class="page-actions" aria-label="Ações da carteira">
            <a mat-flat-button [routerLink]="['/carteiras', id, 'comprar']">Comprar ação</a>
            <a mat-stroked-button [routerLink]="['/carteiras', id, 'vender']">Vender</a>
            <button mat-stroked-button type="button" [disabled]="refreshing()" (click)="refreshQuotes()">{{ refreshing() ? 'Atualizando…' : '↻ Atualizar cotações' }}</button><button mat-button type="button" [matMenuTriggerFor]="walletActions" aria-label="Mais ações da carteira">Mais ações</button><mat-menu #walletActions="matMenu"><button mat-menu-item type="button" (click)="editWallet()">Editar nome</button><button mat-menu-item type="button" (click)="removeWallet()">Excluir carteira</button></mat-menu>
          </nav>
        </div>

        <div class="financial-metric-grid" aria-label="Resumo financeiro por moeda">
          <article class="financial-metric"><span>Ativos</span><strong>{{ summary()!.quantidadeAtivos }}</strong></article>
          @for (subtotal of summary()!.resumosPorMoeda; track subtotal.moeda) {
            <article class="financial-metric"><span>Valor atual · {{ subtotal.moeda }}</span><strong>{{ money(subtotal.valorAtual, subtotal.moeda) }}</strong></article>
            <article class="financial-metric financial-metric--result"><span>Lucro / Prejuízo · {{ subtotal.moeda }}</span><strong [class]="resultClass(subtotal.lucroPrejuizo)" matTooltip="Resultado calculado com base nas operações desta carteira.">{{ signedMoney(subtotal.lucroPrejuizo, subtotal.moeda) }}</strong><small>{{ resultDescription(subtotal.lucroPrejuizo) }}</small></article>
          }
        </div>

        @for (currency of chartCurrencies(); track currency) {
          <section class="analytics-currency-group" [attr.aria-label]="'Gráficos em ' + currency">
            <div class="currency-section-heading"><h2>Analytics · {{ currency }}</h2><p>Valores em {{ currency }}. Moedas diferentes não são combinadas.</p></div>
            <div class="chart-grid">
              <app-chart-panel title="Composição por ação" [chart]="donutChart" [series]="compositionSeries(currency)" [labels]="compositionLabels(currency)" [loading]="analyticsLoading()" [error]="analyticsError()" [retryable]="!!analyticsError()" (retry)="loadAnalyticsPositions()" [hasData]="composition(currency).length > 0" [summary]="compositionSummary(currency)" emptyMessage="Ainda não há posições suficientes para exibir a composição." />
              <app-chart-panel title="Lucro / prejuízo por ação" [chart]="resultChart" [series]="resultSeries(currency)" [xaxis]="resultAxis(currency)" [plotOptions]="resultPlotOptions" [loading]="analyticsLoading()" [error]="analyticsError()" [retryable]="!!analyticsError()" (retry)="loadAnalyticsPositions()" [hasData]="results(currency).length > 0" [summary]="resultSummary(currency)" emptyMessage="Ainda não há resultados por ação para exibir." />
              <app-chart-panel title="Preço médio x cotação" [chart]="comparisonChart" [series]="comparisonSeries(currency)" [xaxis]="comparisonAxis(currency)" [loading]="analyticsLoading()" [error]="analyticsError()" [retryable]="!!analyticsError()" (retry)="loadAnalyticsPositions()" [hasData]="comparisons(currency).length > 0" [summary]="comparisonSummary(currency)" emptyMessage="Ainda não há preço médio e cotação comparáveis." />
            </div>
          </section>
        }

        <div class="page-heading"><div><h2>Posições ativas</h2><p>Valores oficiais na moeda de cada posição.</p></div><a mat-button [routerLink]="['/carteiras', id, 'operacoes']">Histórico de operações</a></div>
        @if (!positions()?.content?.length) { <app-empty-state message="Esta carteira ainda não possui posições ativas." /> }
        @else {
          <div class="position-list">
            @for (item of positions()!.content; track item.acaoId) {
              <article class="position-card">
                <div class="position-card__heading"><div><h3>{{ item.ticker }}</h3><p>{{ item.nomeEmpresa }}</p><span class="market-chip">{{ item.mercado }} · {{ item.moeda }}</span></div><strong>{{ item.quantidade }} ações</strong></div>
                <dl class="position-metrics">
                  <div><dt>Preço médio</dt><dd>{{ money(item.precoMedio, item.moeda) }}</dd></div>
                  <div><dt>Investido atualmente</dt><dd>{{ money(item.valorInvestidoAtual, item.moeda) }}</dd></div>
                  <div><dt>Cotação atual</dt><dd>{{ money(item.cotacaoAtual, item.moeda) }} <small class="metric-note">{{ variation(item.cotacaoAtual, item.precoMedio) }}</small></dd></div>
                  <div><dt>Valor da posição</dt><dd>{{ money(item.valorAtual, item.moeda) }}</dd></div>
                  <div><dt>Total comprado</dt><dd>{{ money(item.valorTotalComprado, item.moeda) }}</dd></div>
                  <div><dt>Total vendido</dt><dd>{{ money(item.valorTotalVendido, item.moeda) }}</dd></div>
                  <div><dt>Preço médio de venda</dt><dd matTooltip="{{ item.precoMedioVenda === null ? 'Nenhuma venda registrada para este ativo.' : 'Preço médio das vendas registradas para este ativo.' }}">{{ item.precoMedioVenda === null ? '—' : money(item.precoMedioVenda, item.moeda) }}</dd></div>
                  <div><dt>Resultado realizado</dt><dd [class]="resultClass(item.lucroPrejuizoRealizado)" matTooltip="Lucro ou prejuízo já concretizado nas vendas.">{{ signedMoney(item.lucroPrejuizoRealizado, item.moeda) }}</dd></div>
                  <div><dt>Resultado não realizado</dt><dd [class]="resultClass(item.lucroPrejuizoNaoRealizado)" matTooltip="Lucro ou prejuízo da posição ainda aberta.">{{ signedMoney(item.lucroPrejuizoNaoRealizado, item.moeda) }}</dd></div>
                  <div><dt>Resultado total</dt><dd [class]="resultClass(item.lucroPrejuizo)" matTooltip="Resultado total autoritativo desta posição.">{{ signedMoney(item.lucroPrejuizo, item.moeda) }}</dd></div>
                </dl>
                <div class="position-card__actions"><a mat-button [routerLink]="['/acoes', item.acaoId]" [queryParams]="{ carteiraId: id }">Ver ação</a><a mat-stroked-button [routerLink]="['/carteiras', id, 'comprar']" [queryParams]="{ acaoId: item.acaoId }">Comprar ação</a><a mat-flat-button [routerLink]="['/carteiras', id, 'vender']" [queryParams]="{ acaoId: item.acaoId }">Vender ação</a></div>
              </article>
            }
          </div>
          <app-pagination [page]="positions()!.number" [totalPages]="positions()!.totalPages" [first]="positions()!.first" [last]="positions()!.last" (previous)="loadPositions(positions()!.number - 1)" (next)="loadPositions(positions()!.number + 1)" />
        }
      }
    </section>`,
})
export class CarteiraDetailPage {
  private readonly service = inject(CarteiraService);
  private readonly acaoService = inject(AcaoService);
  private readonly feedback = inject(FeedbackService);
  private readonly route = inject(ActivatedRoute);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);
  protected readonly id = Number(this.route.snapshot.paramMap.get('id'));
  protected readonly summary = signal<ResumoCarteira | null>(null);
  protected readonly wallet = signal<{ id: number; nome: string } | null>(null);
  protected readonly positions = signal<Page<PosicaoAcao> | null>(null);
  protected readonly analyticsPositions = signal<readonly PosicaoAcao[]>([]);
  protected readonly analyticsLoading = signal(false);
  protected readonly analyticsError = signal('');
  protected readonly loading = signal(true);
  protected readonly error = signal('');
  protected readonly refreshing = signal(false);
  protected readonly chartCurrencies = computed<Moeda[]>(() => [...new Set([
    ...(this.summary()?.resumosPorMoeda.map((subtotal) => subtotal.moeda) ?? []),
    ...this.analyticsPositions().map((position) => position.moeda),
  ])]);
  protected readonly donutChart: ApexChart = { type: 'donut', height: 300 };
  protected readonly resultChart: ApexChart = { type: 'bar', height: 300 };
  protected readonly comparisonChart: ApexChart = { type: 'bar', height: 300, toolbar: { show: false } };
  protected readonly resultPlotOptions: ApexPlotOptions = { bar: { horizontal: true, borderRadius: 2, colors: { ranges: [{ from: -Infinity, to: -0.00001, color: 'var(--color-danger)' }, { from: 0, to: 0, color: 'var(--color-neutral)' }, { from: 0.00001, to: Infinity, color: 'var(--color-success)' }] } } };

  constructor() { this.load(); }

  protected load() {
    this.loading.set(true); this.error.set('');
    this.service.get(this.id).subscribe({
      next: (wallet) => { this.wallet.set(wallet); this.loadSummary(); this.loadPositions(); this.loadAnalyticsPositions(); },
      error: (httpError: { status?: number; message?: string }) => { this.error.set(httpError.status === 403 || httpError.status === 404 ? 'Carteira não encontrada.' : (httpError.message ?? 'Não foi possível carregar a carteira.')); this.loading.set(false); },
    });
  }

  protected loadPositions(index = 0) {
    this.service.positions(this.id, { page: index, size: 10 }).subscribe({
      next: (page) => { this.positions.set(page); this.loading.set(false); },
      error: (httpError: { message?: string }) => { this.error.set(httpError.message ?? 'Não foi possível carregar as posições.'); this.loading.set(false); },
    });
  }

  protected loadAnalyticsPositions() {
    this.analyticsLoading.set(true); this.analyticsError.set(''); this.analyticsPositions.set([]);
    this.service.allPositions(this.id).subscribe({
      next: (positions) => { this.analyticsPositions.set(positions); this.analyticsLoading.set(false); },
      error: (httpError: { message?: string }) => { this.analyticsError.set(httpError.message ?? 'Não foi possível carregar os gráficos da carteira.'); this.analyticsLoading.set(false); },
    });
  }

  protected composition(currency: Moeda) { return compositionItems(this.analyticsPositions(), currency); }
  protected results(currency: Moeda) { return resultItems(this.analyticsPositions(), currency); }
  protected comparisons(currency: Moeda) { return priceComparisonItems(this.analyticsPositions(), currency); }
  protected compositionLabels(currency: Moeda) { return this.composition(currency).map((item) => item.label); }
  protected compositionSeries(currency: Moeda): ApexNonAxisChartSeries { return this.composition(currency).map((item) => item.value); }
  protected resultSeries(currency: Moeda): ApexAxisChartSeries { return [{ name: 'Lucro / Prejuízo', data: this.results(currency).map((item) => item.value) }]; }
  protected resultAxis(currency: Moeda): ApexXAxis { return { categories: this.results(currency).map((item) => item.label) }; }
  protected compositionSummary(currency: Moeda) { return this.composition(currency).map((item) => `${item.label}: ${this.money(item.value, item.moeda)}`); }
  protected resultSummary(currency: Moeda) { return this.results(currency).map((item) => `${item.label}: ${this.resultDescription(item.value)}, ${this.signedMoney(item.value, item.moeda)}`); }
  protected comparisonSeries(currency: Moeda): ApexAxisChartSeries { const items = this.comparisons(currency); return [{ name: 'Preço médio', data: items.map((item) => item.average) }, { name: 'Cotação atual', data: items.map((item) => item.current) }]; }
  protected comparisonAxis(currency: Moeda): ApexXAxis { return { categories: this.comparisons(currency).map((item) => item.label) }; }
  protected comparisonSummary(currency: Moeda) { return this.comparisons(currency).map((item) => `${item.label}: preço médio ${this.money(item.average, item.moeda)}; cotação atual ${this.money(item.current, item.moeda)}`); }

  private loadSummary() {
    this.service.summary(this.id).subscribe({
      next: (summary) => this.summary.set(summary),
      error: (httpError: { message?: string }) => { this.error.set(httpError.message ?? 'Não foi possível carregar o resumo da carteira.'); this.loading.set(false); },
    });
  }

  protected refreshQuotes() {
    if (this.refreshing()) return;
    this.refreshing.set(true);
    this.acaoService.updateAllQuotes().subscribe({
      next: (result) => { this.feedback[result.atualizadas === 0 || result.falhas > 0 ? 'error' : 'success'](this.refreshOutcome(result)); this.load(); },
      error: () => this.feedback.error('Não foi possível atualizar as cotações.'),
      complete: () => this.refreshing.set(false),
    });
  }

  protected editWallet() { const wallet = this.wallet(); if (!wallet) return; this.dialog.open(CarteiraEditDialogComponent, { data: wallet, autoFocus: 'first-tabbable', width: 'min(100% - 2rem,32rem)' }).afterClosed().subscribe((updated) => { if (updated) this.wallet.update((current) => current ? { ...current, nome: updated.nome } : current); if (updated) this.loadSummary(); }); }
  protected removeWallet() { const wallet = this.wallet(); if (!wallet) return; this.dialog.open(ConfirmDialogComponent, { data: { title: 'Excluir carteira?', message: `Você está prestes a excluir "${wallet.nome}". A carteira só poderá ser excluída se não possuir registros vinculados.`, confirmLabel: 'Excluir carteira' }, autoFocus: 'first-tabbable', width: 'min(100% - 2rem,32rem)' }).afterClosed().subscribe((confirmed) => { if (!confirmed) return; this.service.delete(this.id).subscribe({ next: () => { this.feedback.success('Carteira excluída com sucesso.'); this.router.navigateByUrl('/carteiras'); }, error: (error: unknown) => this.feedback.error(maintenanceError(error, 'Não foi possível excluir a carteira.', 'Esta carteira não está mais disponível.')) }); }); }

  protected money(value: number | null | undefined, currency: string) {
    return formatMoney(value, currency);
  }

  protected signedMoney(value: number | null | undefined, currency: string) { return formatSignedMoney(value, currency); }

  protected variation(current: number | null | undefined, average: number | null | undefined) {
    const value = priceVariationPercent(current, average);
    return value === null ? 'variação indisponível' : `${value >= 0 ? '+' : ''}${value.toFixed(2)}% sobre o preço médio`;
  }

  protected resultClass(value: number | null | undefined) { return value === null || value === undefined ? '' : value < 0 ? 'negative-value' : value > 0 ? 'positive-value' : 'neutral-value'; }
  protected resultDescription(value: number | null | undefined) { return value === null || value === undefined ? 'resultado indisponível' : value < 0 ? 'resultado negativo' : value > 0 ? 'resultado positivo' : 'resultado neutro'; }

  private refreshOutcome(result: { total: number; atualizadas: number; falhas: number; dataHora: string }) {
    const completedAt = new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(result.dataHora));
    if (result.atualizadas === 0) return `Nenhuma cotação foi atualizada; ${result.falhas} falharam. Concluído em ${completedAt}.`;
    if (result.falhas > 0) return `${result.atualizadas} de ${result.total} cotações atualizadas; ${result.falhas} falharam. Concluído em ${completedAt}.`;
    return `${result.atualizadas} de ${result.total} cotações atualizadas. Concluído em ${completedAt}.`;
  }
}
