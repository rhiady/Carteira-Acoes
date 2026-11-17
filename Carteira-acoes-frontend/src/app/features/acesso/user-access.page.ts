import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-user-access-page',
  imports: [RouterLink],
  template: `<main class="access-page"><p class="eyebrow">Área do usuário</p><h1>Seus investimentos</h1><p>Consulte suas carteiras, posições e operações.</p><nav aria-label="Navegação do usuário"><a routerLink="/inicio">Início</a><a routerLink="/dashboard">Visão geral</a><a routerLink="/carteiras">Carteiras</a><a routerLink="/operacoes">Operações</a><a routerLink="/acoes">Ações</a><button type="button" (click)="auth.logout()">Sair</button></nav></main>`,
  styles: `.access-page { display:grid; gap:1rem; margin:auto; max-width:52rem; padding:clamp(2rem,6vw,5rem); } nav { display:flex; flex-wrap:wrap; gap:.75rem; } a,button { background:var(--surface-raised); border:1px solid var(--border-subtle); color:var(--text-primary); padding:.8rem 1rem; text-decoration:none; } a:focus-visible,button:focus-visible { outline:3px solid var(--focus-ring); outline-offset:2px; }`
})
export class UserAccessPage { protected readonly auth = inject(AuthService); }
