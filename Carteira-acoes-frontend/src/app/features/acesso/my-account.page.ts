import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { ApiError } from '../../core/models/domain.models';
import { AuthService } from '../../core/services/auth.service';

type PasswordForm = { password: FormControl<string>; confirmation: FormControl<string> };

@Component({
  selector: 'app-my-account-page',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <main class="account-page" id="main-content">
      <a class="back-link" routerLink="/usuario">Voltar para sua área</a>
      <header class="account-heading"><p class="eyebrow">Conta</p><h1>Minha conta</h1><p>Dados da identidade autenticada e opções disponíveis para sua conta.</p></header>
      @if (auth.currentUser(); as user) {
        <section class="account-section" aria-labelledby="identity-title">
          <p class="section-index" aria-hidden="true">01 / identidade</p>
          <h2 id="identity-title">Seus dados</h2>
          <dl class="account-details"><div><dt>Login</dt><dd>{{ user.login }}</dd></div><div><dt>Identificador</dt><dd>{{ user.id }}</dd></div></dl>
        </section>
        <section class="account-section" aria-labelledby="security-title">
          <p class="section-index" aria-hidden="true">02 / segurança</p>
          <h2 id="security-title">Redefinir senha</h2>
          <p>Defina uma nova senha para sua conta. A senha atual não é solicitada.</p>
          <form [formGroup]="passwordForm" (ngSubmit)="submitPassword()" novalidate aria-describedby="password-help">
            <p id="password-help" class="form-help">Use entre 8 e 72 caracteres.</p>
            <label for="new-password">Nova senha</label>
            <input id="new-password" type="password" formControlName="password" autocomplete="new-password" [attr.aria-invalid]="passwordInvalid()" />
            @if (passwordInvalid()) { <p class="field-error" role="alert">A senha deve ter entre 8 e 72 caracteres.</p> }
            <label for="password-confirmation">Confirmar nova senha</label>
            <input id="password-confirmation" type="password" formControlName="confirmation" autocomplete="new-password" [attr.aria-invalid]="confirmationInvalid()" />
            @if (confirmationInvalid()) { <p class="field-error" role="alert">As senhas não coincidem.</p> }
            @if (success()) { <p class="form-success" role="status">Senha redefinida com sucesso.</p> }
            @if (errorMessage()) { <p class="field-error" role="alert">{{ errorMessage() }}</p> }
            <button class="account-action" type="submit" [disabled]="loading()">{{ loading() ? 'Salvando…' : 'Redefinir senha' }}</button>
          </form>
        </section>
      } @else {
        <p role="status">Não foi possível identificar sua sessão.</p>
      }
    </main>
  `,
  styles: `
    :host { display:block; min-height:100%; }
    .account-page { display:grid; gap:1.5rem; margin:0 auto; max-width:58rem; padding:clamp(1.5rem,5vw,4rem); }
    .back-link { color:var(--color-blue); font-weight:700; width:max-content; }
    .account-heading { border-bottom:1px solid var(--color-rule); padding-bottom:1.5rem; }
    .account-heading h1 { font-size:clamp(2rem,5vw,3.5rem); letter-spacing:-.06em; margin:.25rem 0 .5rem; }
    .account-heading p:last-child, .account-section > p:not(.section-index) { color:var(--color-muted); margin:0; max-width:42rem; }
    .account-section { border-bottom:1px solid var(--color-rule); display:grid; gap:.8rem; padding:1.5rem 0; }
    .section-index { color:var(--color-muted); font-size:.75rem; letter-spacing:.1em; margin:0; text-transform:uppercase; }
    .account-section h2 { font-size:1.25rem; margin:0; }
    .account-details { display:grid; gap:1rem; grid-template-columns:repeat(2,minmax(0,1fr)); margin:0; }
    .account-details div { border-left:2px solid var(--color-blue); display:grid; gap:.3rem; padding-left:.8rem; }
    dt { color:var(--color-muted); font-size:.8rem; }
    dd { color:var(--text-primary); font-weight:700; margin:0; overflow-wrap:anywhere; }
    .account-action { background:var(--surface-raised); border:1px solid var(--border-subtle); color:var(--text-primary); min-height:3rem; padding:.75rem 1rem; width:max-content; }
    form { display:grid; gap:.55rem; max-width:32rem; }
    label { font-weight:700; margin-top:.35rem; }
    input { background:var(--surface-raised); border:1px solid var(--border-subtle); color:var(--text-primary); min-height:3rem; padding:.65rem .75rem; }
    input[aria-invalid="true"] { border-color:var(--color-red); }
    .form-help { color:var(--color-muted); margin:0 0 .2rem; }
    .field-error { color:var(--color-red); margin:0; }
    .form-success { color:var(--color-blue); margin:0; }
    a:focus-visible, button:focus-visible { outline:3px solid var(--focus-ring); outline-offset:3px; }
    @media (max-width:640px) { .account-details { grid-template-columns:1fr; } .account-action { width:100%; } }
  `
})
export class MyAccountPage {
  protected readonly auth = inject(AuthService);
  protected readonly loading = signal(false);
  protected readonly success = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly passwordForm = new FormGroup<PasswordForm>({
    password: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(8), Validators.maxLength(72)] }),
    confirmation: new FormControl('', { nonNullable: true, validators: [Validators.required] })
  });

  protected passwordInvalid(): boolean {
    const control = this.passwordForm.controls.password;
    return control.touched && control.invalid;
  }

  protected confirmationInvalid(): boolean {
    const control = this.passwordForm.controls.confirmation;
    return control.touched && (control.invalid || control.value !== this.passwordForm.controls.password.value);
  }

  protected submitPassword(): void {
    if (this.loading()) return;
    this.passwordForm.markAllAsTouched();
    this.success.set(false);
    this.errorMessage.set(null);
    const password = this.passwordForm.controls.password.value;
    const confirmation = this.passwordForm.controls.confirmation.value;
    if (this.passwordForm.invalid || password !== confirmation) return;
    this.loading.set(true);
    this.auth.changePassword({ password }).pipe(finalize(() => this.loading.set(false))).subscribe({
      next: () => { this.passwordForm.reset(); this.success.set(true); },
      error: (error: unknown) => {
        const apiError = error as Partial<ApiError>;
        this.errorMessage.set(apiError.message || 'Não foi possível redefinir a senha.');
      },
      complete: () => undefined
    });
  }
}
