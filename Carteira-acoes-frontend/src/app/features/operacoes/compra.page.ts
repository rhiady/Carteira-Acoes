import { Component, computed, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { Acao, Mercado } from '../../core/models/domain.models';
import { AcaoService } from '../../core/services/acao.service';
import { AuthService } from '../../core/services/auth.service';
import { CarteiraService } from '../../core/services/carteira.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { OperacaoService } from '../../core/services/operacao.service';

@Component({ selector: 'app-compra-page', imports: [CurrencyPipe, MatButtonModule, ReactiveFormsModule, RouterLink], template: `
  <section class="operation-page"><div class="page-heading"><div><p class="eyebrow">Nova operação</p><h1>Comprar ação</h1><p>Informe o ativo e revise o valor estimado antes de registrar a compra.</p></div><a [routerLink]="['/carteiras',carteiraId]">Voltar à carteira</a></div>
  <form class="operation-form" [formGroup]="form" (ngSubmit)="submit()">
    <fieldset><legend>Ativo</legend>
    @if (selectedAsset()) { <div class="trade-asset"><span class="trade-asset__label">Ativo selecionado</span><strong>{{ selectedAsset()!.ticker }}</strong><span>{{ selectedAsset()!.nomeEmpresa }} · {{ selectedAsset()!.mercado }} · {{ selectedAsset()!.moeda }}</span><span>Cotação atual: <b>{{ selectedAsset()!.cotacaoAtual | currency:selectedAsset()!.moeda }}</b></span></div> }
    @else { <div class="field-grid field-grid--asset"><div><label for="acao">ID da ação existente</label><input id="acao" type="number" min="1" formControlName="acaoId" aria-describedby="asset-help"></div><p class="field-or" aria-hidden="true">ou</p><div><label for="ticker">Ticker</label><input id="ticker" formControlName="ticker" placeholder="Ex.: PETR4" autocomplete="off"></div><div><label for="mercado">Mercado</label><select id="mercado" formControlName="mercado"><option value="">Selecione</option><option value="BRASIL">Brasil</option><option value="EUA">EUA</option></select></div></div><p id="asset-help" class="helper-text">Use o ID, ou informe ticker e mercado.</p> }
    </fieldset>
    @if (!hasValidIdentifier() && form.touched) {<p class="error" role="alert">Informe somente o ID da ação ou ticker e mercado.</p>}
    <fieldset><legend>Ordem</legend><div class="field-grid"><div><label for="quantidade">Quantidade</label><div class="quantity-control"><button mat-stroked-button type="button" (click)="adjustQuantity(-1)" aria-label="Diminuir quantidade">−</button><input id="quantidade" type="number" min="0.0001" step="any" formControlName="quantidade" aria-describedby="quantity-error"><button mat-stroked-button type="button" (click)="adjustQuantity(1)" aria-label="Aumentar quantidade">+</button></div>@if(form.controls.quantidade.touched && form.controls.quantidade.invalid){<p id="quantity-error" class="error" role="alert">Informe uma quantidade maior que zero.</p>}</div><div><label for="preco">Preço unitário</label><input id="preco" type="number" min="0.0001" step="any" formControlName="precoUnitario"><p class="helper-text">Opcional: preenchemos com a cotação atual quando disponível.</p></div></div></fieldset>
    <button mat-button class="costs-toggle" type="button" (click)="costsOpen.set(!costsOpen())" [attr.aria-expanded]="costsOpen()" aria-controls="buy-costs">{{ costsOpen() ? 'Ocultar custos adicionais' : 'Adicionar custos e impostos' }}</button>
    @if (costsOpen()) { <fieldset id="buy-costs"><legend>Custos adicionais</legend><div class="field-grid field-grid--three"><div><label for="corretagem">Corretagem</label><input id="corretagem" type="number" min="0" step="any" formControlName="corretagem"></div><div><label for="impostos">Impostos</label><input id="impostos" type="number" min="0" step="any" formControlName="impostos"></div><div><label for="adicional">Valor adicional</label><input id="adicional" type="number" min="0" step="any" formControlName="valorAdicional"></div></div></fieldset> }
    @if (estimate() !== null) {<div class="trade-summary" role="status" aria-live="polite"><span>Total estimado</span><strong>{{ estimate() | currency:estimateCurrency() }}</strong><small>A API confirma os valores ao registrar a operação.</small></div>}
    @if (error()) {<p class="error" role="alert">{{error()}}</p>}<div class="page-actions"><a [routerLink]="['/carteiras',carteiraId]">Cancelar</a><button [disabled]="form.invalid||!hasValidIdentifier()||loading()||!walletReady()">{{loading()?'Processando compra…':'Confirmar compra'}}</button></div>
  </form></section>` })
export class CompraPage {
  private readonly service = inject(OperacaoService);
  private readonly acaoService = inject(AcaoService);
  private readonly auth = inject(AuthService);
  private readonly carteiraService = inject(CarteiraService);
  private readonly feedback = inject(FeedbackService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  protected readonly carteiraId = Number(this.route.snapshot.paramMap.get('id'));
  protected readonly loading = signal(false);
  protected readonly error = signal('');
  protected readonly walletReady = signal(false);
  protected readonly selectedAsset = signal<Acao | null>(null);
  protected readonly costsOpen = signal(false);
  protected readonly estimateCurrency = computed(() => this.selectedAsset()?.moeda ?? 'BRL');
  protected readonly form = new FormGroup({
    acaoId: new FormControl<number | null>(Number(this.route.snapshot.queryParamMap.get('acaoId')) || null, Validators.min(1)), ticker: new FormControl('', { nonNullable: true }), mercado: new FormControl<Mercado | ''>('', { nonNullable: true }),
    quantidade: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(.0001)] }), precoUnitario: new FormControl<number | null>(null, Validators.min(.0001)),
    corretagem: new FormControl<number | null>(null, Validators.min(0)), impostos: new FormControl<number | null>(null, Validators.min(0)), valorAdicional: new FormControl<number | null>(null, Validators.min(0)),
  });
  constructor() {
    const user = this.auth.currentUser();
    if (!user) { this.error.set('Sessão expirada. Entre novamente para continuar.'); return; }
    this.carteiraService.get(this.carteiraId).subscribe({
      next: (wallet) => {
        if (wallet.usuarioId !== user.id) { this.error.set('Carteira não encontrada.'); return; }
        this.walletReady.set(true);
        const acaoId = this.form.controls.acaoId.value;
        if (acaoId !== null) this.loadAsset(acaoId);
      },
      error: (response: { status?: number; message?: string }) => this.error.set(response.status === 403 || response.status === 404 ? 'Carteira não encontrada.' : (response.message ?? 'Não foi possível carregar a carteira.')),
    });
  }
  private loadAsset(acaoId: number) { this.acaoService.get(acaoId).subscribe({ next: (asset) => { this.selectedAsset.set(asset); this.form.controls.precoUnitario.setValue(asset.cotacaoAtual); }, error: () => this.error.set('Não foi possível carregar o ativo selecionado.') }); }
  protected hasValidIdentifier() { const { acaoId, ticker, mercado } = this.form.getRawValue(); const hasAction = acaoId !== null; const hasTickerReference = ticker.trim().length > 0 || mercado !== ''; return hasAction ? !hasTickerReference : ticker.trim().length > 0 && mercado !== ''; }
  protected adjustQuantity(amount: number) { this.form.controls.quantidade.setValue(Math.max(.0001, this.form.controls.quantidade.value + amount)); }
  protected estimate() { const value = this.form.getRawValue(); if (value.precoUnitario === null || value.quantidade <= 0) return null; return value.quantidade * value.precoUnitario + (value.corretagem ?? 0) + (value.impostos ?? 0) + (value.valorAdicional ?? 0); }
  protected submit() { if (this.loading() || this.form.invalid || !this.hasValidIdentifier()) { this.form.markAllAsTouched(); return; } this.loading.set(true); const value = this.form.getRawValue(); const assetReference = value.acaoId !== null ? { acaoId: value.acaoId } : { ticker: value.ticker.trim().toUpperCase(), mercado: value.mercado as Mercado }; this.service.buy({ carteiraId: this.carteiraId, ...assetReference, quantidade: value.quantidade, ...(value.precoUnitario === null ? {} : { precoUnitario: value.precoUnitario }), ...(value.corretagem === null ? {} : { corretagem: value.corretagem }), ...(value.impostos === null ? {} : { impostos: value.impostos }), ...(value.valorAdicional === null ? {} : { valorAdicional: value.valorAdicional }) }).subscribe({ next: () => { this.feedback.success('Compra registrada. Os valores confirmados são os retornados pela API.'); this.router.navigate(['/carteiras', this.carteiraId]); }, error: (error: { message?: string }) => { this.error.set(error.message?.trim() || 'Não foi possível concluir a operação.'); this.loading.set(false); } }); }
}
