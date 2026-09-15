import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { Moeda, Operacao, Page, TipoOperacao } from '../../core/models/domain.models';
import { OperacaoService } from '../../core/services/operacao.service';
import { EmptyStateComponent, ErrorStateComponent, LoadingStateComponent, PaginationComponent } from '../../shared/components/ui-states.component';
import { OperationDetailDialogComponent } from '../../shared/components/operation-detail-dialog.component';

@Component({
  selector: 'app-historico-page',
  imports: [RouterLink, CurrencyPipe, DatePipe, MatButtonModule, EmptyStateComponent, ErrorStateComponent, LoadingStateComponent, PaginationComponent],
  template: `
    <section class="operation-history-page"><div class="page-heading"><div><p class="eyebrow">Histórico financeiro</p><h1>Operações</h1><p>Compras e vendas registradas para esta carteira.</p></div><a routerLink="../">Voltar à carteira</a></div>
      @if (loading()) { <app-loading-state message="Carregando operações…" /> }
      @else if (error()) { <app-error-state [message]="error()" (retry)="load()" /> }
      @else if (!page()?.content?.length) { <app-empty-state message="Nenhuma operação registrada nesta carteira." /> }
      @else {
        <div class="operation-filter-bar" aria-label="Filtrar operações por tipo"><span>Mostrar</span>@for (option of filters; track option.value) { <button type="button" [class.operation-filter--active]="filter() === option.value" [attr.aria-pressed]="filter() === option.value" (click)="filter.set(option.value)">{{ option.label }}</button> }</div>
        <section class="operation-page-summary" aria-label="Resumo da página atual"><div class="section-heading"><div><h2>Resumo</h2><p>Valores desta página; a API não fornece totais históricos por tipo.</p></div></div><div class="operation-summary-grid">@for (summary of summaries(); track summary.moeda) { <article><span>Total comprado · {{ summary.moeda }}</span><strong>{{ summary.compras | currency:summary.moeda }}</strong></article><article><span>Total vendido · {{ summary.moeda }}</span><strong>{{ summary.vendas | currency:summary.moeda }}</strong></article><article><span>Custos · {{ summary.moeda }}</span><strong>{{ summary.custos | currency:summary.moeda }}</strong></article> }</div></section>
        <div class="section-heading"><div><h2>Histórico</h2><p>{{ filteredItems().length }} registro{{ filteredItems().length === 1 ? '' : 's' }} nesta página.</p></div></div>
        @if (!filteredItems().length) { <app-empty-state message="Não há operações deste tipo nesta página." /> }
        @else { <div class="operation-history-list">@for (item of filteredItems(); track item.id) { <article class="operation-history-item"><div class="operation-history-item__identity"><span [class]="item.tipo === 'VENDA' ? 'operation-chip operation-chip--sale' : 'operation-chip'">{{ item.tipo }}</span><h3>{{ item.ticker }}</h3><p>{{ item.nomeEmpresa }}</p><small>{{ item.mercado }} · {{ item.moeda }}</small><small>{{ item.quantidade }} × {{ item.precoUnitario | currency:item.moeda }}</small></div><dl><div><dt>Valor bruto</dt><dd>{{ item.valorBruto | currency:item.moeda }}</dd></div><div><dt>Valor líquido</dt><dd>{{ item.valorLiquido | currency:item.moeda }}</dd></div><div><dt>Custos</dt><dd>{{ costs(item) | currency:item.moeda }}</dd></div><div><dt>Data e hora</dt><dd>{{ item.dataHora | date:'short' }}</dd></div></dl><button mat-button type="button" (click)="openDetails(item)">Ver operação<span class="sr-only"> de {{ item.ticker }}</span></button></article> }</div> }
        <app-pagination [page]="page()!.number" [totalPages]="page()!.totalPages" [first]="page()!.first" [last]="page()!.last" (previous)="load(page()!.number - 1)" (next)="load(page()!.number + 1)" /> }
    </section>`,
})
export class HistoricoPage {
  private readonly service = inject(OperacaoService);
  private readonly dialog = inject(MatDialog);
  private readonly route = inject(ActivatedRoute);
  protected readonly carteiraId = Number(this.route.snapshot.paramMap.get('id'));
  protected readonly page = signal<Page<Operacao> | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal('');
  protected readonly filter = signal<TipoOperacao | 'TODAS'>('TODAS');
  protected readonly filters: readonly { readonly label: string; readonly value: TipoOperacao | 'TODAS' }[] = [
    { label: 'Todas', value: 'TODAS' }, { label: 'Compras', value: 'COMPRA' }, { label: 'Vendas', value: 'VENDA' },
  ];
  protected readonly filteredItems = computed(() => this.page()?.content.filter((item) => this.filter() === 'TODAS' || item.tipo === this.filter()) ?? []);
  protected readonly summaries = computed(() => {
    const grouped = new Map<Moeda, { moeda: Moeda; compras: number; vendas: number; custos: number }>();
    this.filteredItems().forEach((item) => {
      const current = grouped.get(item.moeda) ?? { moeda: item.moeda, compras: 0, vendas: 0, custos: 0 };
      const costs = this.costs(item);
      if (item.tipo === 'COMPRA') current.compras += item.valorLiquido;
      else current.vendas += item.valorLiquido;
      current.custos += costs;
      grouped.set(item.moeda, current);
    });
    return [...grouped.values()];
  });
  constructor() { this.load(); }
  protected load(index = 0) { this.loading.set(true); this.error.set(''); this.service.history(this.carteiraId, { page: index, size: 20 }).subscribe({ next: (page) => { this.page.set(page); this.loading.set(false); }, error: (httpError: { message?: string }) => { this.error.set(httpError.message ?? 'Não foi possível carregar as operações.'); this.loading.set(false); } }); }
  protected costs(item: Operacao) { return item.corretagem + item.impostos + item.valorAdicional; }
  protected openDetails(item: Operacao) { this.dialog.open(OperationDetailDialogComponent, { data: item, autoFocus: 'first-tabbable', width: 'min(100% - 2rem, 34rem)' }); }
}
