import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UsuarioService } from '../../core/services/usuario.service';

@Component({
  selector: 'app-usuario-form-page', imports: [ReactiveFormsModule, RouterLink],
  template: `<section class="creation-page"><div class="page-heading"><div><p class="eyebrow">Cadastro</p><h1>Novo usuário</h1><p>Crie o perfil que será associado às suas carteiras de investimento.</p></div><a routerLink="/usuarios">Voltar para usuários</a></div>
  <form class="operation-form creation-form" [formGroup]="form" (ngSubmit)="submit()" novalidate>
    @if (submitted() && form.invalid) { <div class="form-error-summary" tabindex="-1" role="alert"><strong>Revise os campos destacados</strong><ul>@if(form.controls.nome.invalid){<li><a href="#nome">Informe seu nome.</a></li>}@if(form.controls.email.invalid){<li><a href="#email">Informe um e-mail válido.</a></li>}@if(form.controls.senha.invalid){<li><a href="#senha">A senha precisa ter ao menos 8 caracteres.</a></li>}</ul></div> }
    <fieldset><legend>Dados de acesso</legend><div><label for="nome">Nome <span aria-hidden="true">*</span></label><input id="nome" autocomplete="name" formControlName="nome" aria-describedby="nome-help nome-error"><p id="nome-help" class="helper-text">Como você quer ser identificado na plataforma.</p>@if(showError('nome')){<p id="nome-error" class="error" role="alert">Informe seu nome.</p>}</div><div><label for="email">E-mail <span aria-hidden="true">*</span></label><input id="email" type="email" autocomplete="email" inputmode="email" formControlName="email" aria-describedby="email-help email-error"><p id="email-help" class="helper-text">Usaremos este endereço para identificar sua conta.</p>@if(showError('email')){<p id="email-error" class="error" role="alert">Informe um e-mail válido.</p>}</div><div><label for="senha">Senha <span aria-hidden="true">*</span></label><input id="senha" type="password" autocomplete="new-password" minlength="8" formControlName="senha" aria-describedby="senha-help senha-error"><p id="senha-help" class="helper-text">Use pelo menos 8 caracteres.</p>@if(showError('senha')){<p id="senha-error" class="error" role="alert">A senha precisa ter ao menos 8 caracteres.</p>}</div></fieldset>
    @if(error()){<p class="error" role="alert">{{error()}}</p>}<div class="page-actions form-actions"><a routerLink="/usuarios">Cancelar</a><button type="submit" [disabled]="loading()">{{loading()?'Cadastrando…':'Cadastrar usuário'}}</button></div>
  </form></section>`,
})
export class UsuarioFormPage {
  private readonly service = inject(UsuarioService); private readonly router = inject(Router);
  protected readonly loading = signal(false); protected readonly submitted = signal(false); protected readonly error = signal('');
  protected readonly form = new FormGroup({ nome: new FormControl('', { nonNullable: true, validators: Validators.required }), email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }), senha: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(8)] }) });
  protected showError(control: 'nome' | 'email' | 'senha') { const field = this.form.controls[control]; return field.invalid && (field.touched || this.submitted()); }
  protected submit() { this.submitted.set(true); if (this.form.invalid) { this.form.markAllAsTouched(); return; } this.loading.set(true); this.error.set(''); this.service.create(this.form.getRawValue()).subscribe({ next: () => this.router.navigateByUrl('/usuarios'), error: (response: { message?: string }) => { this.error.set(response.message ?? 'Não foi possível cadastrar o usuário. Tente novamente.'); this.loading.set(false); } }); }
}
