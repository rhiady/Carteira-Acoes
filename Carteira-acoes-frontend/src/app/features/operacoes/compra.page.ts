import { Component, computed, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { Acao, PosicaoAcao } from '../../core/models/domain.models';
import { AcaoService } from '../../core/services/acao.service';
import { AuthService } from '../../core/services/auth.service';
import { CarteiraService } from '../../core/services/carteira.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { OperacaoService } from '../../core/services/operacao.service';

@Component({ selector: 'app-compra-page', imports: [CurrencyPipe, MatButtonModule, ReactiveFormsModule, RouterLink], template: `
  <section class="operation-page purchase-page">
    <div class="page-heading purchase-heading"><div><h1>Comprar ação</h1><p>Defina o ativo, revise a ordem e confirme a compra.</p></div><a mat-stroked-button [routerLink]="['/carteiras', carteiraId]">Voltar à carteira</a></div>
    <form class="operation-form purchase-form" [formGroup]="form" (ngSubmit)="submit()">
      <fieldset class="purchase-section"><legend>Escolha a ação</legend>
        @if (isCommonUser()) {
          @if (positionsLoading()) { <p class="helper-text" role="status">Carregando posições da carteira…</p> }
          @else if (positionsError()) { <p class="error" role="alert">{{ positionsError() }}</p> }
          @else if (positions().length) {
            <div class="purchase-position-list" role="list" aria-label="Posições da carteira">
              @for (position of positions(); track position.acaoId) {
                <button class="purchase-position" type="button" [class.purchase-position--selected]="selectedPosition()?.acaoId === position.acaoId" [attr.aria-pressed]="selectedPosition()?.acaoId === position.acaoId" (click)="selectPosition(position)">
                  <span><strong>{{ position.ticker }}</strong><small>{{ position.nomeEmpresa }} · {{ position.mercado }}</small></span>
                  <span><small>Cotação atual</small><strong>{{ position.cotacaoAtual === null ? '—' : (position.cotacaoAtual | currency:position.moeda) }}</strong></span>
                </button>
              }
            </div>
          }
          @if (selectedPosition(); as position) {
            <div class="trade-asset purchase-asset"><span class="trade-asset__label">Ativo selecionado</span><strong>{{ position.ticker }}</strong><span>{{ position.nomeEmpresa }} · {{ position.mercado }} · {{ position.moeda }}</span><span>{{ position.cotacaoAtual === null ? 'Sem cotação sugerida; informe o preço unitário.' : 'Cotação atual sugerida no preço unitário.' }}</span></div>
          }
        }
        @if (assetsLoading()) { <p class="helper-text" role="status">Carregando ações cadastradas…</p> }
        @else if (assetsError()) { <p class="error" role="alert">{{ assetsError() }}</p> }
        @else if (assets().length) {
          <div class="purchase-asset-selector"><label for="acao">Ação cadastrada</label><select id="acao" formControlName="acaoId" (change)="selectCatalogAsset()" aria-describedby="asset-help"><option [ngValue]="null">Selecione uma ação</option>@for (asset of assets(); track asset.id) { <option [ngValue]="asset.id">{{ asset.ticker }} · {{ asset.nomeEmpresa }} · {{ asset.mercado }}</option> }</select></div>
          <p id="asset-help" class="helper-text">Selecione uma ação cadastrada para usar seu identificador na compra.</p>
        } @else {
          <p class="helper-text">Não há ações cadastradas disponíveis para compra.</p>
        }
        @if (!selectedPosition() && selectedAsset()) {
          <div class="trade-asset purchase-asset"><span class="trade-asset__label">Ativo selecionado</span><strong>{{ selectedAsset()!.ticker }}</strong><span>{{ selectedAsset()!.nomeEmpresa }} · {{ selectedAsset()!.mercado }} · {{ selectedAsset()!.moeda }}</span><span>Cotação atual: <b>{{ selectedAsset()!.cotacaoAtual | currency:selectedAsset()!.moeda }}</b></span></div>
        }
      </fieldset>
      @if (!hasValidIdentifier() && form.touched) {<p class="error purchase-error" role="alert">Selecione uma ação cadastrada.</p>}
      <fieldset class="purchase-section"><legend>Ordem</legend><div class="field-grid"><div><label for="quantidade">Quantidade</label><div class="quantity-control"><button mat-stroked-button type="button" (click)="adjustQuantity(-1)" aria-label="Diminuir quantidade">−</button><input id="quantidade" type="number" min="0.0001" step="any" formControlName="quantidade" aria-describedby="quantity-error"><button mat-stroked-button type="button" (click)="adjustQuantity(1)" aria-label="Aumentar quantidade">+</button></div>@if(form.controls.quantidade.touched && form.controls.quantidade.invalid){<p id="quantity-error" class="error" role="alert">Informe uma quantidade maior que zero.</p>}</div><div><label for="preco">Preço unitário</label><input id="preco" type="number" min="0.0001" step="any" formControlName="precoUnitario"><p class="helper-text">Opcional: usamos a cotação atual quando disponível.</p></div></div></fieldset>
      <button mat-button class="costs-toggle" type="button" (click)="costsOpen.set(!costsOpen())" [attr.aria-expanded]="costsOpen()" aria-controls="buy-costs">{{ costsOpen() ? 'Ocultar custos adicionais' : 'Adicionar custos e impostos' }}</button>
      @if (costsOpen()) { <fieldset id="buy-costs" class="purchase-section purchase-section--costs"><legend>Custos adicionais</legend><div class="field-grid field-grid--three"><div><label for="corretagem">Corretagem</label><input id="corretagem" type="number" min="0" step="any" formControlName="corretagem"></div><div><label for="impostos">Impostos</label><input id="impostos" type="number" min="0" step="any" formControlName="impostos"></div><div><label for="adicional">Valor adicional</label><input id="adicional" type="number" min="0" step="any" formControlName="valorAdicional"></div></div></fieldset> }
      @if (estimate() !== null) {<section class="trade-summary purchase-review" aria-label="Revisão da compra" role="status" aria-live="polite"><span>Total estimado</span><strong>{{ estimate() | currency:estimateCurrency() }}</strong><small>A API confirma os valores ao registrar a operação.</small></section>}
      @if (error()) {<p class="error purchase-error" role="alert">{{error()}}</p>}
      <div class="page-actions purchase-actions"><a mat-stroked-button [routerLink]="['/carteiras', carteiraId]">Cancelar</a><button mat-flat-button type="submit" [disabled]="form.invalid || !hasValidIdentifier() || loading() || !walletReady()">{{loading() ? 'Processando compra…' : 'Confirmar compra'}}</button></div>
    </form>
  </section>`, styles: `
    .purchase-page { gap: var(--space-8); }
    .purchase-heading { align-items: end; }
    .purchase-form { background: transparent; border: 0; gap: var(--space-5); max-width: 70rem; padding: 0; }
    .purchase-section { background: var(--surface-base); border: 1px solid var(--border-subtle); gap: var(--space-4); padding: var(--space-5); }
    .purchase-section legend { font-size: 1rem; letter-spacing: -.02em; }
    .purchase-asset-selector { display: grid; gap: var(--space-2); max-width: 36rem; }
    .purchase-position-list { display: grid; gap: var(--space-2); }
    .purchase-form .purchase-position { align-items: center; background: var(--surface-base); border: 1px solid var(--border-subtle); color: var(--text-primary); display: flex; font: inherit; justify-content: space-between; padding: var(--space-3) var(--space-4); text-align: left; width: 100%; }
    .purchase-form .purchase-position:hover, .purchase-form .purchase-position--selected { background: var(--surface-hover); border-color: var(--color-blue); }
    .purchase-position span { display: grid; gap: .2rem; }
    .purchase-position span:last-child { text-align: right; }
    .purchase-position strong { font-variant-numeric: tabular-nums; }
    .purchase-position small { color: var(--text-secondary); }
    .purchase-review { align-items: start; background: var(--surface-raised); border: 1px solid var(--color-blue); gap: var(--space-2); margin: 0; padding: var(--space-5); }
    .purchase-review span { color: var(--text-secondary); font-size: .75rem; font-weight: 800; letter-spacing: .07em; text-transform: uppercase; }
    .purchase-review strong { font-size: clamp(2rem, 5vw, 3.5rem); font-variant-numeric: tabular-nums; letter-spacing: -.04em; line-height: 1; }
    .purchase-review small { color: var(--text-secondary); }
    .purchase-actions { align-items: center; border-top: 1px solid var(--border-subtle); justify-content: flex-end; padding-top: var(--space-4); }
    .purchase-actions button { min-width: 12rem; }
    .purchase-error { margin: 0; }
    @media (max-width: 640px) { .purchase-section { padding: var(--space-4); } .purchase-position { align-items: start; gap: var(--space-3); } .purchase-actions { align-items: stretch; flex-direction: column-reverse; } .purchase-actions > * { justify-content: center; width: 100%; } }
  ` })
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
  protected readonly assets = signal<readonly Acao[]>([]);
  protected readonly assetsLoading = signal(false);
  protected readonly assetsError = signal('');
  protected readonly positions = signal<readonly PosicaoAcao[]>([]);
  protected readonly selectedPosition = signal<PosicaoAcao | null>(null);
  protected readonly positionsLoading = signal(false);
  protected readonly positionsError = signal('');
  protected readonly costsOpen = signal(false);
  protected readonly isCommonUser = computed(() => this.auth.currentUser()?.authorities.includes('ROLE_USER') === true && !this.auth.currentUser()?.authorities.includes('ROLE_ADMIN'));
  protected readonly estimateCurrency = computed(() => this.selectedPosition()?.moeda ?? this.selectedAsset()?.moeda ?? 'BRL');
  protected readonly form = new FormGroup({
    acaoId: new FormControl<number | null>(Number(this.route.snapshot.queryParamMap.get('acaoId')) || null, Validators.min(1)),
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
        this.loadAssets();
        if (this.isCommonUser()) {
          this.loadPositions();
        } else {
          const acaoId = this.form.controls.acaoId.value;
          if (acaoId !== null) this.loadAsset(acaoId);
        }
      },
      error: (response: { status?: number; message?: string }) => this.error.set(response.status === 403 || response.status === 404 ? 'Carteira não encontrada.' : (response.message ?? 'Não foi possível carregar a carteira.')),
    });
  }
  private loadAssets() {
    this.assetsLoading.set(true); this.assetsError.set('');
    this.acaoService.list({ page: 0, size: 10, sort: 'ticker,asc' }).subscribe({
      next: (page) => { this.assets.set(page.content); this.assetsLoading.set(false); },
      error: (response: { message?: string }) => { this.assetsError.set(response.message ?? 'Não foi possível carregar as ações cadastradas.'); this.assetsLoading.set(false); },
    });
  }
  private loadAsset(acaoId: number) { this.acaoService.get(acaoId).subscribe({ next: (asset) => { this.selectedAsset.set(asset); this.form.controls.precoUnitario.setValue(asset.cotacaoAtual); }, error: () => this.error.set('Não foi possível carregar o ativo selecionado.') }); }
  private loadPositions() {
    this.positionsLoading.set(true); this.positionsError.set('');
    this.carteiraService.positions(this.carteiraId, { page: 0, size: 100, sort: 'ticker,asc' }).subscribe({
      next: (page) => { this.positions.set(page.content); const selected = page.content.find((item) => item.acaoId === this.form.controls.acaoId.value); if (selected) this.selectPosition(selected); this.positionsLoading.set(false); },
      error: (response: { message?: string }) => { this.positionsError.set(response.message ?? 'Não foi possível carregar as posições da carteira.'); this.positionsLoading.set(false); },
    });
  }
  protected selectPosition(position: PosicaoAcao) {
    this.selectedPosition.set(position);
    this.selectedAsset.set(null);
    this.form.patchValue({ acaoId: position.acaoId, precoUnitario: position.cotacaoAtual });
  }
  protected selectCatalogAsset() {
    const asset = this.assets().find((item) => item.id === this.form.controls.acaoId.value) ?? null;
    this.selectedPosition.set(null);
    this.selectedAsset.set(asset);
    if (asset) this.form.controls.precoUnitario.setValue(asset.cotacaoAtual);
  }
  protected hasValidIdentifier() { return this.form.controls.acaoId.value !== null; }
  protected adjustQuantity(amount: number) { this.form.controls.quantidade.setValue(Math.max(.0001, this.form.controls.quantidade.value + amount)); }
  protected estimate() { const value = this.form.getRawValue(); if (value.precoUnitario === null || value.quantidade <= 0) return null; return value.quantidade * value.precoUnitario + (value.corretagem ?? 0) + (value.impostos ?? 0) + (value.valorAdicional ?? 0); }
  protected submit() { if (this.loading() || this.form.invalid || !this.hasValidIdentifier()) { this.form.markAllAsTouched(); return; } this.loading.set(true); const value = this.form.getRawValue(); this.service.buy({ carteiraId: this.carteiraId, acaoId: value.acaoId!, quantidade: value.quantidade, ...(value.precoUnitario === null ? {} : { precoUnitario: value.precoUnitario }), ...(value.corretagem === null ? {} : { corretagem: value.corretagem }), ...(value.impostos === null ? {} : { impostos: value.impostos }), ...(value.valorAdicional === null ? {} : { valorAdicional: value.valorAdicional }) }).subscribe({ next: () => { this.feedback.success('Compra registrada. Os valores confirmados são os retornados pela API.'); this.router.navigate(['/carteiras', this.carteiraId]); }, error: (error: { message?: string }) => { this.error.set(error.message?.trim() || 'Não foi possível concluir a operação.'); this.loading.set(false); } }); }
}
