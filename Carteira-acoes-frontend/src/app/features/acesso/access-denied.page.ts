import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-access-denied-page',
  imports: [RouterLink],
  template: `<main class="access-page"><h1>Acesso negado</h1><p>Você não tem permissão para acessar este recurso.</p><a routerLink="/usuario">Voltar para sua área</a></main>`,
  styles: `.access-page { display:grid; gap:1rem; margin:auto; max-width:40rem; padding:clamp(2rem,6vw,5rem); } a:focus-visible { outline:3px solid var(--focus-ring); outline-offset:2px; }`
})
export class AccessDeniedPage {}
