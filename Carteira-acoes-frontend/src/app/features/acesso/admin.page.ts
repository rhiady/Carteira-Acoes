import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin-page',
  imports: [RouterLink],
  template: `<main class="access-page"><p class="eyebrow">Área administrativa</p><h1>Administração</h1><p>Gerencie os recursos administrativos autorizados pela aplicação.</p><nav aria-label="Navegação administrativa"><a routerLink="/usuarios">Usuários</a><a routerLink="/corretoras">Corretoras</a><a routerLink="/acoes">Ações</a><button type="button" (click)="auth.logout()">Sair</button></nav></main>`,
  styles: `.access-page { display:grid; gap:1rem; margin:auto; max-width:52rem; padding:clamp(2rem,6vw,5rem); } nav { display:flex; flex-wrap:wrap; gap:.75rem; } a,button { background:var(--surface-raised); border:1px solid var(--border-subtle); color:var(--text-primary); padding:.8rem 1rem; text-decoration:none; } a:focus-visible,button:focus-visible { outline:3px solid var(--focus-ring); outline-offset:2px; }`
})
export class AdminPage { protected readonly auth = inject(AuthService); }
