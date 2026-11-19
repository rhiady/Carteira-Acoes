import { Component, ElementRef, inject, signal } from '@angular/core';
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
    <section class="account-page">
      <a class="back-link" routerLink="/inicio">Voltar para o início</a>
      <header class="account-heading"><h1>Minha conta</h1><p>Consulte sua identidade, atualize a segurança e encerre sua sessão.</p></header>
      @if (auth.currentUser(); as user) {
        <section class="account-section" aria-labelledby="identity-title">
          <h2 id="identity-title">Identidade</h2>
          <dl class="account-details"><div><dt>Login</dt><dd>{{ user.login }}</dd></div><div><dt>Identificador</dt><dd>{{ user.id }}</dd></div></dl>
        </section>
        <section class="account-section" aria-labelledby="security-title">
          <h2 id="security-title">Segurança</h2>
          <p>Defina uma nova senha para sua conta. A senha atual não é solicitada.</p>
          <form [formGroup]="passwordForm" (ngSubmit)="submitPassword()" novalidate aria-describedby="password-help">
            <p id="password-help" class="form-help">Use entre 8 e 72 caracteres.</p>
            <label for="new-password">Nova senha</label>
            <input id="new-password" type="password" formControlName="password" autocomplete="new-password" [attr.aria-invalid]="passwordInvalid()" [attr.aria-describedby]="passwordInvalid() ? 'new-password-error' : null" />
            @if (passwordInvalid()) { <p id="new-password-error" class="field-error">A senha deve ter entre 8 e 72 caracteres.</p> }
            <label for="password-confirmation">Confirmar nova senha</label>
            <input id="password-confirmation" type="password" formControlName="confirmation" autocomplete="new-password" [attr.aria-invalid]="confirmationInvalid()" [attr.aria-describedby]="confirmationInvalid() ? 'password-confirmation-error' : null" />
            @if (confirmationInvalid()) { <p id="password-confirmation-error" class="field-error">As senhas não coincidem.</p> }
            @if (success()) { <p class="form-success" role="status">Senha redefinida com sucesso.</p> }
            @if (errorMessage()) { <p class="field-error" role="alert">{{ errorMessage() }}</p> }
            <button class="account-action" type="submit" [disabled]="loading()">{{ loading() ? 'Salvando…' : 'Redefinir senha' }}</button>
          </form>
        </section>
        <section class="account-section account-session" aria-labelledby="session-title">
          <div><h2 id="session-title">Sessão</h2><p>Encerre o acesso neste dispositivo e volte para a tela de login.</p></div>
          <button class="account-action account-action--logout" type="button" (click)="logout()">Sair da conta</button>
        </section>
      } @else {
        <p role="status">Não foi possível identificar sua sessão.</p>
      }
    </section>
  `,
  styles: `
    :host { display:block; min-height:100%; }
    .account-page { display:grid; gap:1.5rem; margin:0 auto; max-width:58rem; }
    .back-link { color:var(--color-blue); font-weight:700; width:max-content; }
    .account-heading { border-bottom:1px solid var(--color-rule); padding-bottom:1.5rem; }
    .account-heading h1 { font-size:clamp(2rem,5vw,3.5rem); letter-spacing:-.06em; margin:0 0 .5rem; }
    .account-heading p, .account-section > p, .account-session p { color:var(--color-muted); margin:0; max-width:42rem; }
    .account-section { border-bottom:1px solid var(--color-rule); display:grid; gap:.8rem; padding:1.5rem 0; }
    .account-section h2 { font-size:1.25rem; margin:0; }
    .account-details { display:grid; gap:1rem; grid-template-columns:repeat(2,minmax(0,1fr)); margin:0; }
    .account-details div { background:var(--surface-raised); border:1px solid var(--border-subtle); display:grid; gap:.3rem; padding:1rem; }
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
    .account-session { align-items:center; grid-template-columns:1fr auto; }
    .account-session div { display:grid; gap:.5rem; }
    .account-action--logout { color:var(--color-danger); }
    a:focus-visible, button:focus-visible { outline:3px solid var(--focus-ring); outline-offset:3px; }
    @media (max-width:640px) { .account-details, .account-session { grid-template-columns:1fr; } .account-action { width:100%; } }
  `
})
export class MyAccountPage {
  private readonly element = inject<ElementRef<HTMLElement>>(ElementRef);
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
    if (this.passwordForm.invalid || password !== confirmation) {
      queueMicrotask(() => this.element.nativeElement.querySelector<HTMLElement>('[aria-invalid="true"]')?.focus());
      return;
    }
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

  protected logout(): void { this.auth.logout(); }
}
