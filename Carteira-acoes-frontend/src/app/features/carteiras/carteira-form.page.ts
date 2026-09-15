import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Corretora } from '../../core/models/domain.models';
import { AuthService } from '../../core/services/auth.service';
import { CarteiraService } from '../../core/services/carteira.service';
import { CorretoraService } from '../../core/services/corretora.service';

@Component({
  selector: 'app-carteira-form-page',
  imports: [ReactiveFormsModule, RouterLink],
  template: `<section class="creation-page"><div class="page-heading"><div><p class="eyebrow">Cadastro</p><h1>Nova carteira</h1><p>Organize seus investimentos em uma nova carteira.</p></div><a routerLink="/carteiras">Voltar para carteiras</a></div><form class="operation-form creation-form" [formGroup]="form" (ngSubmit)="submit()" novalidate>
@if(submitted()&&form.invalid){<div class="form-error-summary" tabindex="-1" role="alert"><strong>Revise os campos destacados</strong><ul>@if(form.controls.nome.invalid){<li><a href="#nome">Informe o nome da carteira.</a></li>}@if(form.controls.corretoraId.invalid){<li><a href="#corretora">Selecione uma corretora.</a></li>}</ul></div>}
<fieldset><legend>Identificação</legend><div><label for="nome">Nome da carteira <span aria-hidden="true">*</span></label><input id="nome" formControlName="nome" aria-describedby="nome-help nome-error"><p id="nome-help" class="helper-text">Ex.: Investimentos de longo prazo.</p>@if(showError('nome')){<p id="nome-error" class="error" role="alert">Informe o nome da carteira.</p>}</div></fieldset>
<fieldset><legend>Corretora</legend>@if(loadingDependencies()){<p class="helper-text" role="status">Carregando corretoras…</p>}@else{<div><label for="corretora">Corretora <span aria-hidden="true">*</span></label><select id="corretora" formControlName="corretoraId"><option [ngValue]="0">Selecione uma corretora</option>@for(broker of brokers();track broker.id){<option [ngValue]="broker.id">{{broker.nomeFantasia || broker.razaoSocial}}</option>}</select>@if(showError('corretoraId')){<p class="error" role="alert">Selecione uma corretora.</p>}</div>}@if(dependenciesError()){<p class="error" role="alert">{{dependenciesError()}}</p>}@if(!loadingDependencies()&&!brokers().length){<p class="helper-text">Cadastre uma corretora antes de continuar.</p>}</fieldset>
@if(error()){<p class="error" role="alert">{{error()}}</p>}<div class="page-actions form-actions"><a routerLink="/carteiras">Cancelar</a><button type="submit" [disabled]="loading()||loadingDependencies()||!brokers().length">{{loading()?'Criando…':'Criar carteira'}}</button></div></form></section>`,
})
export class CarteiraFormPage {
  private readonly auth = inject(AuthService);
  private readonly service = inject(CarteiraService);
  private readonly brokerService = inject(CorretoraService);
  private readonly router = inject(Router);
  protected readonly loading = signal(false);
  protected readonly submitted = signal(false);
  protected readonly error = signal('');
  protected readonly dependenciesError = signal('');
  protected readonly loadingDependencies = signal(true);
  protected readonly brokers = signal<Corretora[]>([]);
  protected readonly form = new FormGroup({
    nome: new FormControl('', { nonNullable: true, validators: Validators.required }),
    corretoraId: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(1)] }),
  });

  constructor() { this.loadDependencies(); }

  protected showError(control: 'nome' | 'corretoraId') {
    const field = this.form.controls[control];
    return field.invalid && (field.touched || this.submitted());
  }

  protected submit() {
    this.submitted.set(true);
    const user = this.auth.currentUser();
    if (!user || this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading.set(true);
    this.error.set('');
    this.service.create({ ...this.form.getRawValue(), usuarioId: user.id }).subscribe({
      next: () => this.router.navigateByUrl('/carteiras'),
      error: (response: { message?: string }) => { this.error.set(response.message ?? 'Não foi possível criar a carteira. Tente novamente.'); this.loading.set(false); },
    });
  }

  private loadDependencies() {
    this.brokerService.list({ size: 100, sort: 'nomeFantasia,asc' }).subscribe({
      next: page => { this.brokers.set(page.content); this.loadingDependencies.set(false); },
      error: () => { this.dependenciesError.set('Não foi possível carregar as corretoras. Atualize a página para tentar novamente.'); this.loadingDependencies.set(false); },
    });
  }
}
