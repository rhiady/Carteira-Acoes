import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { RoleEntryComponent, RoleEntryLink } from '../../shared/components/role-entry.component';

@Component({
  selector: 'app-admin-page',
  imports: [RoleEntryComponent],
  template: `<app-role-entry contextLabel="Área administrativa" [identity]="identity" indexLabel="Gestão" title="Administração" description="Gerencie os recursos administrativos publicados pela aplicação." navigationLabel="Navegação administrativa" [links]="links" (logout)="auth.logout()" />`,
})
export class AdminPage {
  protected readonly auth = inject(AuthService);
  protected readonly identity = this.auth.currentUser()?.login ?? 'Sessão autenticada';
  protected readonly links: readonly RoleEntryLink[] = [
    { label: 'Usuários', path: '/usuarios', primary: true },
    { label: 'Ações', path: '/acoes' },
    { label: 'Carteiras', path: '/carteiras' },
    { label: 'Operações', path: '/operacoes' },
    { label: 'Corretoras', path: '/corretoras' },
  ];
}
