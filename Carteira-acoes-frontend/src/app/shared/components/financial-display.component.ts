import { Component, computed, input } from '@angular/core';
import { Moeda, Mercado, TipoOperacao } from '../../core/models/domain.models';

@Component({
  selector: 'app-currency-value',
  template: `<span>{{ formatted() }}</span>`,
})
export class CurrencyValueComponent {
  readonly value = input.required<number>();
  readonly currency = input<Moeda>('BRL');
  protected readonly formatted = computed(() => new Intl.NumberFormat(
    this.currency() === 'BRL' ? 'pt-BR' : 'en-US',
    { style: 'currency', currency: this.currency() },
  ).format(this.value()));
}

@Component({
  selector: 'app-market-chip',
  template: `<span class="market-chip">{{ market() === 'BRASIL' ? 'Brasil' : 'EUA' }}</span>`,
})
export class MarketChipComponent {
  readonly market = input.required<Mercado>();
}

@Component({
  selector: 'app-operation-chip',
  template: `<span class="operation-chip" [class.operation-chip--sale]="type() === 'VENDA'">{{ type() }}</span>`,
})
export class OperationChipComponent {
  readonly type = input.required<TipoOperacao>();
}
