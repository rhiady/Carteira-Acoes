import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { ApexAxisChartSeries, ApexChart, ApexNonAxisChartSeries, ApexPlotOptions, ApexXAxis } from 'ng-apexcharts';
import { formatMoney, formatSignedMoney } from '../../core/formatters/financial.formatter';
import { compositionItems, resultItems } from '../../core/mappers/portfolio-analytics.mapper';
import { AuthenticatedUser, Carteira, Moeda, Page, PosicaoAcao, ResumoCarteira } from '../../core/models/domain.models';
import { AuthService } from '../../core/services/auth.service';
import { CarteiraService } from '../../core/services/carteira.service';
import { ChartPanelComponent } from '../../shared/components/chart-panel.component';
import { EmptyStateComponent, ErrorStateComponent, LoadingStateComponent } from '../../shared/components/ui-states.component';

@Component({
  selector: 'app-inicio-page',
  imports: [MatButtonModule, MatIconModule, RouterLink, ChartPanelComponent, EmptyStateComponent, ErrorStateComponent, LoadingStateComponent],
  template: `
    <section class="cockpit-page">
      <header class="cockpit-heading">
        <div><h1>Início</h1><p>Acompanhe patrimônio, resultado e composição das suas carteiras.</p></div>
        @if (user(); as currentUser) { <span class="cockpit-identity">{{ currentUser.login }}</span> }
      </header>

      @if (loading()) { <app-loading-state message="Carregando suas carteiras…" /> }
      @else if (error()) { <app-error-state [message]="error()" (retry)="load()" /> }
      @else if (!user()) { <app-empty-state message="Não foi possível identificar sua sessão." /> }
      @else if (!portfolios()?.content?.length) {
        <app-empty-state message="Você ainda não possui carteiras. Crie a primeira para registrar operações." />
        <a mat-flat-button routerLink="/carteiras/nova">Criar carteira</a>
      } @else {
        <div class="cockpit-controls">
          <div class="portfolio-field"><label for="portfolio-in-focus">Carteira em foco</label><select id="portfolio-in-focus" [value]="selectedPortfolioId() ?? portfolios()!.content[0].id" (change)="selectPortfolio($event)">@for (portfolio of portfolios()!.content; track portfolio.id) { <option [value]="portfolio.id">{{ portfolio.nome }}</option> }</select></div>
          <div class="cockpit-actions"><a mat-flat-button [routerLink]="['/carteiras', activePortfolioId(), 'comprar']"><mat-icon aria-hidden="true">add</mat-icon>Comprar ação</a><a [routerLink]="['/carteiras', activePortfolioId()]">Abrir carteira <mat-icon aria-hidden="true">arrow_forward</mat-icon></a></div>
        </div>

        @if (summaryLoading()) { <p class="inline-status" role="status">Atualizando resumo financeiro…</p> }
        @if (summaryError()) { <div class="inline-error" role="alert"><span>{{ summaryError() }}</span><button type="button" (click)="retrySummary()">Tentar novamente</button></div> }
        <div class="metric-ledger" role="group" aria-label="Resumo financeiro da carteira">
          <div class="metric-ledger__assets"><span>Ativos</span><strong>{{ summary()?.quantidadeAtivos ?? '—' }}</strong><small>posições acompanhadas</small></div>
          @for (subtotal of summary()?.resumosPorMoeda ?? []; track subtotal.moeda) {
            <div><span>Valor atual · {{ subtotal.moeda }}</span><strong>{{ money(subtotal.valorAtual, subtotal.moeda) }}</strong><small>subtotal sem conversão cambial</small></div>
            <div><span>Lucro / prejuízo · {{ subtotal.moeda }}</span><strong [class]="resultClass(subtotal.lucroPrejuizo)">{{ signedMoney(subtotal.lucroPrejuizo, subtotal.moeda) }}</strong><small>{{ resultDescription(subtotal.lucroPrejuizo) }}</small></div>
          }
        </div>

        <section class="analytics-stage" aria-labelledby="analytics-title">
          <div class="analytics-heading"><div><h2 id="analytics-title">Composição e resultado</h2><p>Cada moeda é apresentada separadamente para preservar a leitura financeira.</p></div><a routerLink="/operacoes">Ver operações</a></div>
          @if (analyticsLoading()) { <app-loading-state message="Carregando gráficos da carteira…" /> }
          @else if (analyticsError()) { <app-error-state [message]="analyticsError()" (retry)="retryAnalytics()" /> }
          @else if (chartCurrencies().length) {
            @for (currency of chartCurrencies(); track currency) {
              <section class="analytics-currency-group" [attr.aria-label]="'Gráficos em ' + currency">
                <div class="currency-section-heading"><h3>Análise em {{ currency }}</h3><p>Valores em {{ currency }}; moedas diferentes não são combinadas.</p></div>
                <div class="chart-grid cockpit-chart-grid">
                  <app-chart-panel [title]="'Composição por ação · ' + currency" [chart]="compositionChart" [series]="compositionSeries(currency)" [labels]="compositionLabels(currency)" [hasData]="composition(currency).length > 0" [summary]="compositionSummary(currency)" emptyMessage="Ainda não há posições com valor atual disponível nesta moeda." />
                  <app-chart-panel [title]="'Resultado por ação · ' + currency" [chart]="resultChart" [series]="resultSeries(currency)" [xaxis]="resultAxis(currency)" [plotOptions]="resultPlotOptions" [hasData]="results(currency).length > 0" [summary]="resultSummary(currency)" emptyMessage="Ainda não há resultados disponíveis nesta moeda." />
                </div>
              </section>
            }
          } @else { <app-empty-state message="Ainda não há posições disponíveis para os gráficos desta carteira." /> }
        </section>
      }
    </section>`,
  styles: `
    :host { display: block; min-width: 0; }
    .cockpit-page { display: grid; gap: clamp(1.5rem, 3vw, 2.5rem); }
    .cockpit-heading { align-items: end; border-bottom: 1px solid var(--border-subtle); display: flex; gap: var(--space-5); justify-content: space-between; padding-bottom: var(--space-5); }
    .cockpit-heading h1 { font-size: clamp(2rem, 4vw, 3.25rem); letter-spacing: -.055em; margin: 0 0 var(--space-2); }
    .cockpit-heading p, .analytics-heading p { color: var(--color-muted); margin: 0; }
    .cockpit-identity { color: var(--color-muted); font-size: .82rem; overflow-wrap: anywhere; }
    .cockpit-controls { align-items: end; display: flex; gap: var(--space-5); justify-content: space-between; }
    .portfolio-field { display: grid; gap: var(--space-2); min-width: min(100%, 20rem); }
    .portfolio-field label { color: var(--color-muted); font-size: .72rem; font-weight: 800; letter-spacing: .08em; text-transform: uppercase; }
    .portfolio-field select { background: var(--surface-raised); border: 1px solid var(--border-subtle); color: var(--text-primary); font: inherit; min-height: 3rem; padding: .65rem 2.25rem .65rem .75rem; width: 100%; }
    .cockpit-actions { align-items: center; display: flex; flex-wrap: wrap; gap: var(--space-4); }
    .cockpit-actions > a:last-child, .analytics-heading > a { align-items: center; color: var(--color-blue); display: inline-flex; font-weight: 800; gap: .25rem; text-decoration: none; }
    .metric-ledger { background: var(--border-subtle); display: grid; gap: 1px; grid-template-columns: repeat(auto-fit, minmax(min(100%, 14rem), 1fr)); margin: 0; }
    .metric-ledger > div { background: var(--surface-raised); display: grid; gap: var(--space-2); min-width: 0; padding: var(--space-5); }
    .metric-ledger__assets { border-top: 2px solid var(--color-blue); }
    .metric-ledger span { color: var(--color-muted); font-size: .72rem; font-weight: 800; letter-spacing: .07em; text-transform: uppercase; }
    .metric-ledger strong { font-size: clamp(1.5rem, 2.7vw, 2.2rem); font-variant-numeric: tabular-nums; letter-spacing: -.04em; overflow-wrap: anywhere; }
    .metric-ledger small { color: var(--color-muted); }
    .inline-status { color: var(--color-muted); margin: calc(var(--space-4) * -1) 0; }
    .inline-error { align-items: center; border: 1px solid var(--color-danger); display: flex; gap: var(--space-3); justify-content: space-between; padding: var(--space-3); }
    .inline-error button { background: transparent; border: 0; color: var(--color-blue); cursor: pointer; font: inherit; font-weight: 800; }
    .analytics-stage { border-top: 2px solid var(--color-ink); display: grid; gap: var(--space-6); padding-top: var(--space-5); }
    .analytics-heading { align-items: baseline; display: flex; gap: var(--space-5); justify-content: space-between; }
    .analytics-heading h2 { font-size: 1.45rem; letter-spacing: -.035em; margin: 0 0 var(--space-1); }
    .currency-section-heading h3 { font-size: 1rem; margin: 0; }
    .cockpit-chart-grid { grid-template-columns: minmax(0, 1.15fr) minmax(0, .85fr); }
    select:focus-visible, a:focus-visible, button:focus-visible { outline: 3px solid var(--focus-ring); outline-offset: 3px; }
    @media (max-width: 768px) { .cockpit-controls, .analytics-heading { align-items: stretch; flex-direction: column; }.cockpit-actions { justify-content: space-between; }.cockpit-chart-grid { grid-template-columns: 1fr; } }
    @media (max-width: 480px) { .cockpit-heading { align-items: start; flex-direction: column; }.cockpit-actions { align-items: stretch; flex-direction: column; }.cockpit-actions a { justify-content: center; width: 100%; }.metric-ledger { background: transparent; gap: var(--space-2); }.metric-ledger > div { border: 1px solid var(--border-subtle); padding: var(--space-4); }.inline-error { align-items: stretch; flex-direction: column; } }
  `,
})
export class InicioPage {
  private readonly auth = inject(AuthService);
  private readonly carteiras = inject(CarteiraService);
  protected readonly user = signal<AuthenticatedUser | null>(null);
  protected readonly portfolios = signal<Page<Carteira> | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal('');
  protected readonly summary = signal<ResumoCarteira | null>(null);
  protected readonly summaryLoading = signal(false);
  protected readonly summaryError = signal('');
  protected readonly analyticsPositions = signal<readonly PosicaoAcao[]>([]);
  protected readonly analyticsLoading = signal(false);
  protected readonly analyticsError = signal('');
  protected readonly selectedPortfolioId = signal<number | null>(null);
  protected readonly activePortfolioId = computed(() => this.selectedPortfolioId() ?? this.portfolios()?.content[0]?.id ?? 0);
  protected readonly chartCurrencies = computed<Moeda[]>(() => [...new Set([
    ...(this.summary()?.resumosPorMoeda.map((subtotal) => subtotal.moeda) ?? []),
    ...this.analyticsPositions().map((position) => position.moeda),
  ])]);
  protected readonly compositionChart: ApexChart = { type: 'donut', height: 330, toolbar: { show: false } };
  protected readonly resultChart: ApexChart = { type: 'bar', height: 330, toolbar: { show: false } };
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
        const firstPortfolio = portfolios.content.at(0);
        if (firstPortfolio) this.loadPortfolio(firstPortfolio.id);
      },
      error: (httpError: { message?: string }) => { this.error.set(httpError.message ?? 'Não foi possível carregar as carteiras.'); this.loading.set(false); },
    });
  }

  protected selectPortfolio(event: Event) {
    const id = Number((event.target as HTMLSelectElement).value);
    if (Number.isInteger(id) && id > 0 && id !== this.selectedPortfolioId()) this.loadPortfolio(id);
  }

  protected retrySummary() { const id = this.selectedPortfolioId(); if (id !== null) this.loadSummary(id); }
  protected retryAnalytics() { const id = this.selectedPortfolioId(); if (id !== null) this.loadPositions(id); }
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

  private loadPortfolio(id: number) {
    this.selectedPortfolioId.set(id);
    this.summary.set(null); this.analyticsPositions.set([]);
    this.loadSummary(id); this.loadPositions(id);
  }

  private loadSummary(id: number) {
    this.summaryLoading.set(true); this.summaryError.set('');
    this.carteiras.summary(id).subscribe({
      next: (summary) => { if (id === this.selectedPortfolioId()) this.summary.set(summary); },
      error: (httpError: { message?: string }) => { if (id === this.selectedPortfolioId()) { this.summaryError.set(httpError.message ?? 'Resumo financeiro indisponível.'); this.summaryLoading.set(false); } },
      complete: () => { if (id === this.selectedPortfolioId()) this.summaryLoading.set(false); },
    });
  }

  private loadPositions(id: number) {
    this.analyticsLoading.set(true); this.analyticsError.set('');
    this.carteiras.allPositions(id).subscribe({
      next: (positions) => { if (id === this.selectedPortfolioId()) this.analyticsPositions.set(positions); },
      error: (httpError: { message?: string }) => { if (id === this.selectedPortfolioId()) { this.analyticsError.set(httpError.message ?? 'Não foi possível carregar os gráficos financeiros.'); this.analyticsLoading.set(false); } },
      complete: () => { if (id === this.selectedPortfolioId()) this.analyticsLoading.set(false); },
    });
  }
}
