import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CorretoraService } from '../../core/services/corretora.service';

@Component({
  selector: 'app-corretora-form-page',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <section class="creation-page">
      <div class="page-heading"><div><p class="eyebrow">Cadastro</p><h1>Nova corretora</h1><p>Informe o CNPJ para consultar e vincular a instituição às carteiras.</p></div><a routerLink="/corretoras">Voltar para corretoras</a></div>
      <form class="operation-form creation-form" [formGroup]="form" (ngSubmit)="submit()" novalidate>
        <fieldset><legend>Identificação da instituição</legend><div><label for="cnpj">CNPJ <span aria-hidden="true">*</span></label><input id="cnpj" formControlName="cnpj" autocomplete="off" inputmode="numeric" aria-describedby="cnpj-help cnpj-error"><p id="cnpj-help" class="helper-text">Digite apenas os números do CNPJ.</p>@if (form.controls.cnpj.touched && form.controls.cnpj.hasError('required')) {<p id="cnpj-error" class="error" role="alert">Informe o CNPJ.</p>}</div></fieldset>
        @if (error()) {
          <p class="error" role="alert">{{ error() }}</p>
        }
        <div class="page-actions form-actions"><a routerLink="/corretoras">Cancelar</a><button type="submit" [disabled]="form.invalid || loading()">{{ loading() ? 'Cadastrando…' : 'Cadastrar corretora' }}</button></div>
      </form>
    </section>
  `,
})
export class CorretoraFormPage {
  private readonly service = inject(CorretoraService);
  private readonly router = inject(Router);

  protected readonly loading = signal(false);
  protected readonly error = signal('');
  protected readonly form = new FormGroup({
    cnpj: new FormControl('', { nonNullable: true, validators: Validators.required }),
  });

  protected submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.service.create(this.form.getRawValue()).subscribe({
      next: () => this.router.navigateByUrl('/corretoras'),
      error: (error: { message?: string }) => {
        this.error.set(error.message ?? 'Não foi possível cadastrar a corretora.');
        this.loading.set(false);
      },
    });
  }
}
