import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { formatMoney, formatSignedMoney } from '../../core/formatters/financial.formatter';
import { compositionItems, resultItems } from '../../core/mappers/portfolio-analytics.mapper';
import { AuthenticatedUser, Carteira, Moeda, Page, PosicaoAcao, ResumoCarteira } from '../../core/models/domain.models';
import { AcaoService } from '../../core/services/acao.service';
import { CarteiraService } from '../../core/services/carteira.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { ApexAxisChartSeries, ApexChart, ApexNonAxisChartSeries, ApexPlotOptions, ApexXAxis } from 'ng-apexcharts';
import { ChartPanelComponent } from '../../shared/components/chart-panel.component';
import { EmptyStateComponent, ErrorStateComponent, LoadingStateComponent } from '../../shared/components/ui-states.component';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard-page',
  imports: [MatButtonModule, MatIconModule, RouterLink, EmptyStateComponent, ErrorStateComponent, LoadingStateComponent, ChartPanelComponent],
  template: `
    <section class="dashboard-page">
      <div class="page-heading dashboard-heading"><div><p class="eyebrow">Carteira</p><h1>Visão geral da carteira</h1><p>Acompanhe valor, resultado e composição com dados oficiais.</p></div><button mat-flat-button type="button" [disabled]="refreshing()" (click)="refreshQuotes()"><mat-icon aria-hidden="true">refresh</mat-icon>{{ refreshing() ? 'Atualizando cotações…' : 'Atualizar cotações' }}</button></div>
      @if (loading()) { <app-loading-state message="Carregando resumo…" /> }
      @else if (error()) { <app-error-state [message]="error()" (retry)="load()" /> }
      @else if (!user()) { <app-empty-state message="Não foi possível identificar sua sessão." /> }
      @else {
        <p class="context-note">Carteiras de {{ user()!.nome }}.</p>
        @if (!portfolios()?.content?.length) { <app-empty-state message="Você ainda não possui carteiras. Crie a primeira para registrar operações." /><a mat-flat-button routerLink="/carteiras/nova">Criar carteira</a> }
        @else {
          <div class="dashboard-context"><label for="analytics-portfolio">Carteira em foco</label><select id="analytics-portfolio" [value]="analyticsPortfolioId() ?? portfolios()!.content[0].id" (change)="selectPortfolio($any($event.target).value)">@for (portfolio of portfolios()!.content; track portfolio.id) {<option [value]="portfolio.id">{{ portfolio.nome }}</option>}</select><span class="dashboard-context__actions"><a mat-flat-button [routerLink]="['/carteiras', analyticsPortfolioId() ?? portfolios()!.content[0].id, 'comprar']"><mat-icon aria-hidden="true">add</mat-icon> Comprar ação</a><a [routerLink]="['/carteiras', analyticsPortfolioId() ?? portfolios()!.content[0].id]">Abrir carteira <mat-icon aria-hidden="true">arrow_forward</mat-icon></a></span></div>
          <div class="financial-metric-grid" aria-label="Resumo financeiro da carteira">
            <article class="financial-metric financial-metric--assets"><span>Ativos</span><strong>{{ summary()?.quantidadeAtivos ?? '—' }}</strong><small>posições acompanhadas</small></article>
            @for (subtotal of summary()?.resumosPorMoeda ?? []; track subtotal.moeda) { <article class="financial-metric financial-metric--value"><span>Valor atual · {{ subtotal.moeda }}</span><strong>{{ money(subtotal.valorAtual, subtotal.moeda) }}</strong><small>subtotal em {{ subtotal.moeda }}</small></article><article class="financial-metric financial-metric--result"><span>Lucro / prejuízo · {{ subtotal.moeda }}</span><strong [class]="resultClass(subtotal.lucroPrejuizo)">{{ signedMoney(subtotal.lucroPrejuizo, subtotal.moeda) }}</strong><small>{{ resultDescription(subtotal.lucroPrejuizo) }}</small></article> }
          </div>
          @if (chartCurrencies().length) {
            @for (currency of chartCurrencies(); track currency) {
              <section class="analytics-currency-group" [attr.aria-label]="'Gráficos em ' + currency"><div class="currency-section-heading"><h2>Analytics · {{ currency }}</h2><p>Valores em {{ currency }}; moedas diferentes não são combinadas.</p></div><div class="chart-grid">
                <app-chart-panel title="Composição por ação" [chart]="compositionChart" [series]="compositionSeries(currency)" [labels]="compositionLabels(currency)" [loading]="analyticsLoading()" [error]="analyticsError()" [retryable]="!!analyticsError()" (retry)="retryAnalytics()" [hasData]="composition(currency).length > 0" [summary]="compositionSummary(currency)" emptyMessage="Ainda não há posições com valor atual disponível nesta moeda." />
                <app-chart-panel title="Resultado por ação" [chart]="resultChart" [series]="resultSeries(currency)" [xaxis]="resultAxis(currency)" [plotOptions]="resultPlotOptions" [loading]="analyticsLoading()" [error]="analyticsError()" [retryable]="!!analyticsError()" (retry)="retryAnalytics()" [hasData]="results(currency).length > 0" [summary]="resultSummary(currency)" emptyMessage="Ainda não há resultados disponíveis nesta moeda." />
              </div></section>
            }
          } @else { <app-empty-state message="Não há moedas ou posições disponíveis para os gráficos." /> }
        }
      }
    </section>`,
})
export class DashboardPage {
  private readonly auth = inject(AuthService);
  private readonly carteiras = inject(CarteiraService);
  private readonly stocks = inject(AcaoService);
  private readonly feedback = inject(FeedbackService);
  protected readonly user = signal<AuthenticatedUser | null>(null);
  protected readonly portfolios = signal<Page<Carteira> | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal('');
  protected readonly refreshing = signal(false);
  protected readonly summary = signal<ResumoCarteira | null>(null);
  protected readonly analyticsPositions = signal<readonly PosicaoAcao[]>([]);
  protected readonly analyticsLoading = signal(false);
  protected readonly analyticsError = signal('');
  protected readonly analyticsPortfolioId = signal<number | null>(null);
  protected readonly chartCurrencies = computed<Moeda[]>(() => {
    const currencies = [...new Set([
      ...(this.summary()?.resumosPorMoeda.map((subtotal) => subtotal.moeda) ?? []),
      ...this.analyticsPositions().map((position) => position.moeda),
    ])];
    return currencies;
  });
  protected readonly compositionChart: ApexChart = { type: 'donut', height: 300, toolbar: { show: false } };
  protected readonly resultChart: ApexChart = { type: 'bar', height: 300, toolbar: { show: false } };
  protected readonly resultPlotOptions: ApexPlotOptions = { bar: { horizontal: true, borderRadius: 2, colors: { ranges: [{ from: -Infinity, to: -0.00001, color: 'var(--color-danger)' }, { from: 0, to: 0, color: 'var(--color-neutral)' }, { from: 0.00001, to: Infinity, color: 'var(--color-success)' }] } } };

  constructor() { this.load(); }

  protected load() {
    this.loading.set(true); this.error.set('');
    const currentUser = this.auth.currentUser();
    this.user.set(currentUser);
    if (!currentUser) { this.portfolios.set(null); this.loading.set(false); return; }
        this.carteiras.listForUser(currentUser.id).subscribe({
          next: (portfolios) => {
            this.portfolios.set(portfolios); this.loading.set(false);
            const selected = portfolios.content.at(0);
            if (selected) this.loadAnalytics(selected.id);
          },
          error: (httpError: { message?: string }) => { this.error.set(httpError.message ?? 'Não foi possível carregar as carteiras.'); this.loading.set(false); },
        });
  }

  protected retryAnalytics() { const id = this.analyticsPortfolioId(); if (id !== null) this.loadAnalytics(id); }
  protected selectPortfolio(value: string) { const id = Number(value); if (Number.isInteger(id) && id > 0 && id !== this.analyticsPortfolioId()) this.loadAnalytics(id); }

  protected refreshQuotes() {
    if (this.refreshing()) return;
    this.refreshing.set(true);
    this.stocks.updateAllQuotes().subscribe({
      next: (result) => {
        const message = this.refreshOutcome(result);
        if (result.atualizadas === 0 || result.falhas > 0) this.feedback.error(message); else this.feedback.success(message);
        this.refreshing.set(false); this.load();
      },
      error: (httpError: { message?: string }) => { this.feedback.error(httpError.message ?? 'Não foi possível atualizar as cotações.'); this.refreshing.set(false); },
    });
  }

  protected composition(currency: Moeda) { return compositionItems(this.analyticsPositions(), currency); }
  protected results(currency: Moeda) { return resultItems(this.analyticsPositions(), currency); }
  protected compositionLabels(currency: Moeda) { return this.composition(currency).map((item) => item.label); }
  protected compositionSeries(currency: Moeda): ApexNonAxisChartSeries { return this.composition(currency).map((item) => item.value); }
  protected resultSeries(currency: Moeda): ApexAxisChartSeries { return [{ name: 'Lucro / Prejuízo', data: this.results(currency).map((item) => item.value) }]; }
  protected resultAxis(currency: Moeda): ApexXAxis { return { categories: this.results(currency).map((item) => item.label) }; }
  protected compositionSummary(currency: Moeda) { return this.composition(currency).map((item) => `${item.label}: ${this.money(item.value, item.moeda)}`); }
  protected resultSummary(currency: Moeda) { return this.results(currency).map((item) => `${item.label}: ${this.resultDescription(item.value)}, ${this.signedMoney(item.value, item.moeda)}`); }
  protected money(value: number | null | undefined, currency: Moeda) { return formatMoney(value, currency); }
  protected signedMoney(value: number | null | undefined, currency: Moeda) { return formatSignedMoney(value, currency); }
  protected resultClass(value: number | null | undefined) { return value === null || value === undefined ? '' : value < 0 ? 'negative-value' : value > 0 ? 'positive-value' : 'neutral-value'; }
  protected resultDescription(value: number | null | undefined) { return value === null || value === undefined ? 'indisponível' : value < 0 ? 'resultado negativo' : value > 0 ? 'resultado positivo' : 'resultado neutro'; }

  private loadAnalytics(id: number) {
    this.analyticsPortfolioId.set(id); this.analyticsLoading.set(true); this.analyticsError.set(''); this.analyticsPositions.set([]); this.summary.set(null);
    forkJoin({ summary: this.carteiras.summary(id), positions: this.carteiras.allPositions(id) }).subscribe({
      next: ({ summary, positions }) => { this.summary.set(summary); this.analyticsPositions.set(positions); this.analyticsLoading.set(false); },
      error: (httpError: { message?: string }) => { this.analyticsError.set(httpError.message ?? 'Não foi possível carregar os gráficos financeiros.'); this.analyticsLoading.set(false); },
    });
  }

  private refreshOutcome(result: { total: number; atualizadas: number; falhas: number; dataHora: string }) {
    const completedAt = new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(result.dataHora));
    if (result.atualizadas === 0) return `Nenhuma cotação foi atualizada; ${result.falhas} falharam. Concluído em ${completedAt}.`;
    if (result.falhas > 0) return `${result.atualizadas} de ${result.total} cotações atualizadas; ${result.falhas} falharam. Concluído em ${completedAt}.`;
    return `${result.atualizadas} de ${result.total} cotações atualizadas. Concluído em ${completedAt}.`;
  }
}
