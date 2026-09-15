import { Component, input, output } from '@angular/core';
import { ChartComponent, ApexAnnotations, ApexAxisChartSeries, ApexChart, ApexDataLabels, ApexGrid, ApexLegend, ApexNonAxisChartSeries, ApexPlotOptions, ApexStroke, ApexTooltip, ApexXAxis, ApexYAxis } from 'ng-apexcharts';

@Component({
  selector: 'app-chart-panel',
  imports: [ChartComponent],
  template: `
    <section class="chart-panel" [attr.aria-label]="title()">
      <div class="chart-panel__header"><h2>{{ title() }}</h2><span class="chart-panel__live" aria-hidden="true">Dados oficiais</span></div>
      @if (loading()) { <div class="chart-skeleton" role="status" aria-live="polite">Carregando gráfico…</div> }
      @else if (error()) { <div class="chart-message" role="alert"><p>{{ error() }}</p>@if (retryable()) { <button type="button" (click)="retry.emit()">Tentar novamente</button> }</div> }
      @else if (!hasData()) { <div class="chart-message">{{ emptyMessage() }}</div> }
      @else { <div class="chart-canvas"><apx-chart [chart]="chart()" [series]="series()" [labels]="labels()" [colors]="colors()" [dataLabels]="dataLabels()" [grid]="grid()" [stroke]="stroke()" [xaxis]="xaxis()" [yaxis]="yaxis()" [plotOptions]="plotOptions()" [legend]="legend()" [tooltip]="tooltip()" [annotations]="annotations()" /></div> }
      @if (summary().length) {
        <div class="chart-text-summary">
          <h3>Resumo textual</h3>
          <ul>@for (item of summary(); track item) { <li>{{ item }}</li> }</ul>
        </div>
      }
    </section>`,
})
export class ChartPanelComponent {
  readonly title = input.required<string>();
  readonly chart = input.required<ApexChart>();
  readonly series = input.required<ApexAxisChartSeries | ApexNonAxisChartSeries>();
  readonly labels = input<string[]>([]);
  readonly colors = input<string[]>([]);
  readonly dataLabels = input<ApexDataLabels>({});
  readonly grid = input<ApexGrid>({});
  readonly stroke = input<ApexStroke>({});
  readonly xaxis = input<ApexXAxis>({});
  readonly yaxis = input<ApexYAxis | ApexYAxis[]>({});
  readonly plotOptions = input<ApexPlotOptions>({});
  readonly legend = input<ApexLegend>({});
  readonly tooltip = input<ApexTooltip>({});
  readonly annotations = input<ApexAnnotations>({});
  readonly loading = input(false);
  readonly error = input('');
  readonly retryable = input(false);
  readonly hasData = input(false);
  readonly emptyMessage = input('Ainda não há dados suficientes para este gráfico.');
  readonly summary = input<readonly string[]>([]);
  readonly retry = output<void>();
}
