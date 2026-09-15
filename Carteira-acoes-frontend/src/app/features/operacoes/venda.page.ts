import { CurrencyPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PosicaoAcao } from '../../core/models/domain.models';
import { CarteiraService } from '../../core/services/carteira.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { OperacaoService } from '../../core/services/operacao.service';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';

@Component({
  selector: 'app-venda-page',
  imports: [CurrencyPipe, MatButtonModule, ReactiveFormsModule, RouterLink],
  template: `
    <section class="operation-page"><div class="page-heading"><div><p class="eyebrow">Nova operação</p><h1>Vender ação</h1><p>Escolha uma posição disponível e confirme a ordem antes do envio.</p></div><a [routerLink]="['/carteiras',carteiraId]">Voltar à carteira</a></div>
      <form class="operation-form operation-form--sale" [formGroup]="form" (ngSubmit)="confirmSale()">
        <fieldset><legend>Ativo e posição</legend><label for="acao">Ação</label>
        <select id="acao" formControlName="acaoId"><option [ngValue]="0">Selecione uma posição</option>@for(item of positions();track item.acaoId){<option [ngValue]="item.acaoId">{{item.ticker}} ({{item.quantidade}} disponíveis)</option>}</select>
        @if (selectedPosition()) { <div class="trade-asset"><strong>{{ selectedPosition()!.ticker }} · {{ selectedPosition()!.nomeEmpresa }}</strong><span>Disponível: {{ selectedPosition()!.quantidade }} ações</span><span>Preço médio: {{ selectedPosition()!.precoMedio === null ? '—' : (selectedPosition()!.precoMedio | currency:selectedPosition()!.moeda) }}</span><span>Cotação atual: {{ selectedPosition()!.cotacaoAtual === null ? '—' : (selectedPosition()!.cotacaoAtual | currency:selectedPosition()!.moeda) }}</span><span>Preço médio de venda: {{ selectedPosition()!.precoMedioVenda === null ? '—' : (selectedPosition()!.precoMedioVenda | currency:selectedPosition()!.moeda) }}</span></div> }
        </fieldset><fieldset><legend>Ordem</legend><label for="quantidade">Quantidade</label><div class="quantity-control"><button mat-stroked-button type="button" (click)="adjustQuantity(-1)" aria-label="Diminuir quantidade">−</button><input id="quantidade" type="number" min="0.0001" step="any" formControlName="quantidade" aria-describedby="quantity-error"><button mat-stroked-button type="button" (click)="adjustQuantity(1)" aria-label="Aumentar quantidade">+</button></div>@if(form.controls.quantidade.touched && form.controls.quantidade.invalid){<p id="quantity-error" class="error" role="alert">Informe uma quantidade maior que zero.</p>}</fieldset>
        @if(exceedsAvailable()){<p class="error" role="alert">A quantidade excede a posição disponível.</p>}
        @if (estimatedTotal() !== null) { <div class="trade-summary trade-summary--sale" role="status" aria-live="polite"><span>Valor líquido estimado</span><strong>{{ estimatedTotal()! | currency:selectedPosition()?.moeda }}</strong><small>Custos e impostos serão descontados na operação.</small></div> }
        <button mat-button class="costs-toggle" type="button" (click)="costsOpen.set(!costsOpen())" [attr.aria-expanded]="costsOpen()" aria-controls="sale-costs">{{ costsOpen() ? 'Ocultar custos adicionais' : 'Adicionar custos e impostos' }}</button>
        @if (costsOpen()) { <fieldset id="sale-costs"><legend>Custos adicionais</legend><div class="field-grid field-grid--three"><div><label for="corretagem">Corretagem</label><input id="corretagem" type="number" min="0" step="any" formControlName="corretagem"></div><div><label for="impostos">Impostos</label><input id="impostos" type="number" min="0" step="any" formControlName="impostos"></div><div><label for="adicional">Valor adicional</label><input id="adicional" type="number" min="0" step="any" formControlName="valorAdicional"></div></div></fieldset> }
        @if(error()){<p class="error" role="alert">{{error()}}</p>}
        <div class="page-actions"><a [routerLink]="['/carteiras',carteiraId]">Cancelar</a><button [disabled]="form.invalid||loading()||exceedsAvailable()">{{loading()?'Processando venda…':'Confirmar venda'}}</button></div>
      </form>
    </section>`,
})
export class VendaPage {
  private readonly positionsService = inject(CarteiraService);
  private readonly service = inject(OperacaoService);
  private readonly feedback = inject(FeedbackService);
  private readonly dialog = inject(MatDialog);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  protected readonly carteiraId = Number(this.route.snapshot.paramMap.get('id'));
  protected readonly positions = signal<PosicaoAcao[]>([]);
  protected readonly loading = signal(false);
  protected readonly error = signal('');
  protected readonly costsOpen = signal(false);
  protected readonly form = new FormGroup({
    acaoId: new FormControl(Number(this.route.snapshot.queryParamMap.get('acaoId')) || 0, { nonNullable: true, validators: [Validators.required, Validators.min(1)] }),
    quantidade: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(.0001)] }),
    corretagem: new FormControl<number | null>(null, Validators.min(0)),
    impostos: new FormControl<number | null>(null, Validators.min(0)),
    valorAdicional: new FormControl<number | null>(null, Validators.min(0)),
  });
  private readonly formValue = toSignal(this.form.valueChanges, { initialValue: this.form.getRawValue() });
  protected readonly selectedPosition = computed(() => this.positions().find((item) => item.acaoId === this.formValue().acaoId) ?? null);
  protected readonly estimatedTotal = computed(() => {
    const item = this.selectedPosition();
    const value = this.formValue();
    if (!item || item.cotacaoAtual === null || !value.quantidade || value.quantidade <= 0) return null;
    const costs = (value.corretagem ?? 0) + (value.impostos ?? 0) + (value.valorAdicional ?? 0);
    return item.cotacaoAtual * value.quantidade - costs;
  });

  constructor() {
    this.positionsService.positions(this.carteiraId, { size: 100 }).subscribe({
      next: (page) => this.positions.set(page.content),
      error: (error: { message?: string }) => this.error.set(error.message ?? 'Não foi possível carregar as posições.'),
    });
  }

  protected exceedsAvailable() {
    const selected = this.positions().find((item) => item.acaoId === this.form.controls.acaoId.value);
    return selected !== undefined && this.form.controls.quantidade.value > selected.quantidade;
  }
  protected adjustQuantity(amount: number) { this.form.controls.quantidade.setValue(Math.max(.0001, this.form.controls.quantidade.value + amount)); }

  protected confirmSale() {
    if (this.form.invalid || this.exceedsAvailable()) { this.form.markAllAsTouched(); return; }
    const selected = this.positions().find((item) => item.acaoId === this.form.controls.acaoId.value);
    this.dialog.open(ConfirmDialogComponent, {
      data: { title: 'Confirmar venda', message: `Confirma a venda de ${this.form.controls.quantidade.value} ${selected?.ticker ?? 'ações'}?`, confirmLabel: 'Confirmar venda' },
      autoFocus: 'first-tabbable', width: 'min(100% - 2rem, 28rem)',
    }).afterClosed().subscribe((confirmed: boolean) => { if (confirmed) this.submit(); });
  }

  private submit() {
    this.loading.set(true); this.error.set('');
    const value = this.form.getRawValue();
    this.service.sell({ carteiraId: this.carteiraId, acaoId: value.acaoId, quantidade: value.quantidade, ...(value.corretagem === null ? {} : { corretagem: value.corretagem }), ...(value.impostos === null ? {} : { impostos: value.impostos }), ...(value.valorAdicional === null ? {} : { valorAdicional: value.valorAdicional }) }).subscribe({
      next: () => { this.feedback.success('Venda registrada.'); this.router.navigate(['/carteiras', this.carteiraId]); },
      error: (error: { message?: string }) => { this.error.set(error.message?.trim() || 'Não foi possível concluir a operação.'); this.loading.set(false); },
    });
  }
}
