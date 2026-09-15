import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { Page, Usuario } from '../../core/models/domain.models';
import { UsuarioService } from '../../core/services/usuario.service';
import { UsuarioDialogComponent } from '../../shared/components/registration-dialogs.component';
import { UserEditDialogComponent } from '../../shared/components/user-edit-dialog.component';
import { EmptyStateComponent, ErrorStateComponent, LoadingStateComponent, PaginationComponent } from '../../shared/components/ui-states.component';

@Component({ selector: 'app-usuarios-page', imports: [DatePipe, MatButtonModule, RouterLink, ErrorStateComponent, EmptyStateComponent, LoadingStateComponent, PaginationComponent], template: `<section><div class="page-heading"><div><p class="eyebrow">Gestão</p><h1>Usuários</h1><p>Clientes que possuem carteiras de investimento.</p></div><button mat-flat-button type="button" (click)="openCreateDialog()">Cadastrar usuário</button></div>@if(loading()){<app-loading-state message="Carregando usuários…"/>}@else if(error()){<app-error-state [message]="error()" (retry)="load()"/>}@else if(!page()?.content?.length){<app-empty-state message="Cadastre um usuário para criar uma carteira."/>}@else{<div class="table-scroll"><table><thead><tr><th>Nome</th><th>E-mail</th><th>Cadastro</th><th>Ações</th></tr></thead><tbody>@for(item of page()!.content;track item.id){<tr><td>{{item.nome}}</td><td>{{item.email}}</td><td>{{item.createdAt|date:'short'}}</td><td><a mat-button [routerLink]="['/usuarios',item.id]">Ver carteiras</a><button mat-button type="button" (click)="openEditDialog(item)">Editar usuário</button></td></tr>}</tbody></table></div><app-pagination [page]="page()!.number" [totalPages]="page()!.totalPages" [first]="page()!.first" [last]="page()!.last" (previous)="load(page()!.number-1)" (next)="load(page()!.number+1)"/>}</section>` })
export class UsuariosPage {
  private readonly service = inject(UsuarioService);
  private readonly dialog = inject(MatDialog);
  protected readonly page = signal<Page<Usuario> | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal('');
  constructor() { this.load(); }
  protected load(index = 0) { this.loading.set(true); this.error.set(''); this.service.list({ page: index, size: 10, sort: 'nome,asc' }).subscribe({ next: (page) => { this.page.set(page); this.loading.set(false); }, error: (error: { message?: string }) => { this.error.set(error.message ?? 'Não foi possível carregar os usuários.'); this.loading.set(false); } }); }
  protected openCreateDialog() { this.dialog.open(UsuarioDialogComponent, { autoFocus: 'first-tabbable', width: 'min(100% - 2rem, 32rem)' }).afterClosed().subscribe((created) => { if (created) this.load(this.page()?.number); }); }
  protected openEditDialog(user: Usuario) { this.dialog.open(UserEditDialogComponent, { data: user, autoFocus: 'first-tabbable', width: 'min(100% - 2rem, 34rem)' }).afterClosed().subscribe((updated: Usuario | undefined) => { if (!updated) return; this.page.update((current) => current ? { ...current, content: current.content.map((item) => item.id === updated.id ? updated : item) } : current); }); }
}
