import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { Page, UsuarioResumo } from '../../core/models/domain.models';
import { UsuarioService } from '../../core/services/usuario.service';
import { UsuarioDialogComponent } from '../../shared/components/registration-dialogs.component';
import { EmptyStateComponent, ErrorStateComponent, LoadingStateComponent, PaginationComponent } from '../../shared/components/ui-states.component';

@Component({ selector: 'app-usuarios-page', imports: [MatButtonModule, RouterLink, ErrorStateComponent, EmptyStateComponent, LoadingStateComponent, PaginationComponent], template: `<section><div class="page-heading"><div><p class="eyebrow">Gestão administrativa</p><h1>Usuários</h1><p>Consulte as contas cadastradas e suas carteiras vinculadas.</p></div><button mat-flat-button type="button" (click)="openCreateDialog()">Cadastrar usuário</button></div>@if(loading()){<app-loading-state message="Carregando usuários…"/>}@else if(error()){<app-error-state [message]="error()" [retryAllowed]="!accessDenied()" (retry)="load()"/>}@else if(!page()?.content?.length){<app-empty-state message="Cadastre um usuário para criar uma carteira."/>}@else{<div class="table-scroll"><table><thead><tr><th>Login</th><th>Papel</th><th>Status</th><th>Ações</th></tr></thead><tbody>@for(item of page()!.content;track item.id){<tr><td>{{item.login}}</td><td>{{item.papel}}</td><td>{{item.ativo ? 'Ativo' : 'Inativo'}}</td><td><a mat-button [routerLink]="['/usuarios',item.id]">Ver carteiras</a></td></tr>}</tbody></table></div><app-pagination [page]="page()!.number" [totalPages]="page()!.totalPages" [first]="page()!.first" [last]="page()!.last" (previous)="load(page()!.number-1)" (next)="load(page()!.number+1)"/>}</section>` })
export class UsuariosPage {
  private readonly service = inject(UsuarioService);
  private readonly dialog = inject(MatDialog);
  protected readonly page = signal<Page<UsuarioResumo> | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal('');
  protected readonly accessDenied = signal(false);
  constructor() { this.load(); }
  protected load(index = 0) { this.loading.set(true); this.error.set(''); this.accessDenied.set(false); this.page.set(null); this.service.list({ page: index, size: 10, sort: 'id,asc' }).subscribe({ next: (page) => { this.page.set(page); this.loading.set(false); }, error: (error: { status?: number; message?: string }) => { const denied = error.status === 403; this.accessDenied.set(denied); this.error.set(denied ? 'Seu acesso à lista de usuários não foi autorizado. A sessão permanece ativa.' : error.message?.trim() || 'Não foi possível carregar os usuários.'); this.loading.set(false); } }); }
  protected openCreateDialog() { this.dialog.open(UsuarioDialogComponent, { autoFocus: 'first-tabbable', width: 'min(100% - 2rem, 32rem)' }).afterClosed().subscribe((created) => { if (created) this.load(this.page()?.number); }); }
}
