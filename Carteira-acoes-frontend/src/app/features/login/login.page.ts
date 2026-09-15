import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/services/auth.service';
import { ThemeToggleComponent } from '../../shared/components/theme-toggle.component';

@Component({
  selector: 'app-login-page',
  imports: [MatButtonModule, MatIconModule, ReactiveFormsModule, RouterLink, ThemeToggleComponent],
  template: `
    <main class="login-page" id="main-content"><app-theme-toggle class="auth-theme-toggle" />
      <section class="login-shell" aria-labelledby="login-title">
        <div class="login-identity">
          <p class="login-rule" aria-hidden="true"></p>
          <p class="login-brand">Carteira Ações</p>
          <p class="login-context">Gestão de investimentos</p>
        </div>
        <div class="login-panel">
          <p class="login-index" aria-hidden="true">01 / acesso</p>
          <h1 id="login-title">Entrar</h1>
          <p class="login-subtitle">Acesse sua conta</p>
          <form [formGroup]="form" (ngSubmit)="submit()" novalidate>
            @if (errorMessage()) {
              <div class="login-error" role="alert" aria-live="assertive">{{ errorMessage() }}</div>
            }
            <div class="field-group">
              <label for="login-email">Email</label>
              <input id="login-email" type="email" autocomplete="email" inputmode="email" formControlName="email" placeholder="nome@email.com" [attr.aria-invalid]="showError('email')" aria-describedby="login-email-error">
              @if (showError('email')) {
                <p class="field-error" id="login-email-error" role="alert">{{ emailError() }}</p>
              }
            </div>
            <div class="field-group">
              <label for="login-password">Senha</label>
              <div class="password-control">
                <input id="login-password" [type]="passwordVisible() ? 'text' : 'password'" autocomplete="current-password" formControlName="senha" [attr.aria-invalid]="showError('senha')" aria-describedby="login-password-error">
                <button class="password-toggle" type="button" [attr.aria-label]="passwordVisible() ? 'Ocultar senha' : 'Mostrar senha'" [attr.aria-pressed]="passwordVisible()" (click)="passwordVisible.update((visible) => !visible)">
                  <mat-icon aria-hidden="true">{{ passwordVisible() ? 'visibility_off' : 'visibility' }}</mat-icon>
                </button>
              </div>
              @if (showError('senha')) {
                <p class="field-error" id="login-password-error" role="alert">Informe sua senha.</p>
              }
            </div>
            <button class="login-submit" mat-flat-button type="submit" [disabled]="loading()">
              {{ loading() ? 'Entrando…' : 'Entrar' }}
            </button>
            <p class="login-note">Acesso simples aos seus dados de investimento.</p>
            <p class="login-register">Ainda não tem uma conta? <a routerLink="/cadastro">Criar conta</a></p>
          </form>
        </div>
      </section>
    </main>
  `,
  styles: `
    :host { display: block; min-height: 100dvh; }
    .login-page { align-items: center; background: var(--surface-page); display: flex; min-height: 100dvh; padding: clamp(1.5rem, 5vw, 4rem); }
    .login-shell { background: var(--surface-base); border: 1px solid var(--border-subtle); display: grid; grid-template-columns: minmax(12rem, .8fr) minmax(18rem, 1fr); margin: auto; max-width: 58rem; width: 100%; }
    .login-identity { border-right: 1px solid var(--color-rule); padding: clamp(2rem, 5vw, 4rem); }
    .login-rule { background: var(--color-red, #e4002b); height: 3.5rem; margin: 0 0 2rem; width: 2px; }
    .login-brand { color: var(--color-ink); font-size: clamp(1.4rem, 2.8vw, 2rem); font-weight: 800; letter-spacing: -.045em; margin: 0; }
    .login-context, .login-index, .login-note { color: var(--color-muted); font-size: .78rem; margin: .7rem 0 0; }
    .login-panel { max-width: 26rem; padding: clamp(2rem, 5vw, 4rem); width: 100%; }
    .login-index { margin: 0 0 2.5rem; text-transform: uppercase; letter-spacing: .1em; }
    h1 { font-size: clamp(2rem, 4vw, 3rem); letter-spacing: -.06em; margin: 0; }
    .login-subtitle { color: var(--color-muted); margin: .5rem 0 2rem; }
    form { display: grid; gap: 1.25rem; }
    .field-group { display: grid; gap: .45rem; }
    label { color: var(--color-ink); font-size: .85rem; font-weight: 750; }
    input { background: var(--surface-raised); border: 1px solid var(--border-subtle); border-radius: 0; box-sizing: border-box; color: var(--text-primary); font: inherit; min-height: 3rem; padding: .75rem .85rem; width: 100%; }
    input:focus-visible, button:focus-visible { outline: 3px solid color-mix(in srgb, var(--color-blue) 45%, transparent); outline-offset: 2px; }
    input[aria-invalid="true"] { border-color: var(--color-red, #e4002b); }
    .password-control { position: relative; }
    .password-control input { padding-right: 3rem; }
    .password-toggle { align-items: center; background: transparent; border: 0; color: var(--color-muted); cursor: pointer; display: inline-flex; height: 2.75rem; justify-content: center; padding: 0; position: absolute; right: .2rem; top: .1rem; width: 2.75rem; }
    .field-error, .login-error { color: #a40020; font-size: .8rem; margin: 0; }
    .login-error { border-left: 3px solid var(--color-red, #e4002b); padding: .7rem .8rem; }
    .login-submit { --mdc-filled-button-label-text-color: #fff; background: var(--color-blue); border-radius: 0; color: #fff; min-height: 3rem; width: 100%; }
    .login-note { border-top: 1px solid var(--color-rule); padding-top: 1rem; }.login-register { color: var(--color-muted); font-size: .85rem; margin: 0; }.login-register a { color: var(--color-blue); font-weight: 750; }
    @media (max-width: 640px) { .login-page { padding: 1rem; }.login-shell { display: block; }.login-identity { border-bottom: 1px solid var(--color-rule); border-right: 0; padding: 1.5rem; }.login-rule { height: 2rem; margin-bottom: 1rem; }.login-panel { box-sizing: border-box; max-width: none; padding: 1.5rem; } }
  `
})
export class LoginPage {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal('');
  protected readonly passwordVisible = signal(false);
  protected readonly submitted = signal(false);
  protected readonly form = new FormGroup({
    email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    senha: new FormControl('', { nonNullable: true, validators: Validators.required })
  });

  protected showError(control: 'email' | 'senha'): boolean {
    const field = this.form.controls[control];
    return field.invalid && (field.touched || this.submitted());
  }

  protected emailError(): string {
    return this.form.controls.email.hasError('required') ? 'Informe seu email.' : 'Informe um email válido.';
  }

  protected submit(): void {
    this.submitted.set(true);
    this.errorMessage.set('');
    if (this.form.invalid || this.loading()) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => void this.router.navigateByUrl('/inicio'),
      error: (error: { status?: number }) => {
        this.errorMessage.set(error.status === 401 ? 'Email ou senha inválidos.' : 'Não foi possível entrar. Tente novamente.');
        this.loading.set(false);
      }
    });
  }
}
