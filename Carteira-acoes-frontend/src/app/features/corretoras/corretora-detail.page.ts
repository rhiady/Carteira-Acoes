import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Corretora } from '../../core/models/domain.models';
import { CorretoraService } from '../../core/services/corretora.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { maintenanceError } from '../../core/services/maintenance-error';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';

@Component({
  selector: 'app-corretora-detail',
  imports: [RouterLink, MatButtonModule],
  template: `<section class="detail-page">
    @if (item(); as broker) {
      <div class="page-heading detail-hero"><div><p class="eyebrow">Corretoras</p><h1>{{ broker.nomeFantasia || broker.razaoSocial || broker.cnpj }}</h1><p class="detail-hero__secondary">{{ broker.razaoSocial || 'Razão social não informada' }}</p><p class="detail-hero__identifier">{{ broker.cnpj }}</p></div><div class="detail-hero__status"><span class="status-chip">{{ broker.situacaoCadastral || 'Cadastro consultado' }}</span>@if (broker.registroCvm) {<span class="status-chip status-chip--neutral">CVM {{ broker.registroCvm }}</span>}</div></div>
      <div class="detail-groups"><section class="detail-group"><h2>Informações cadastrais</h2><dl class="detail-fields"><div><dt>CNPJ</dt><dd>{{ broker.cnpj }}</dd></div><div><dt>Razão social</dt><dd>{{ broker.razaoSocial || '—' }}</dd></div><div><dt>Registro CVM</dt><dd>{{ broker.registroCvm || '—' }}</dd></div></dl></section><section class="detail-group"><h2>Contato</h2><dl class="detail-fields"><div><dt>Telefone</dt><dd>{{ broker.telefone || '—' }}</dd></div><div><dt>E-mail</dt><dd>{{ broker.email || '—' }}</dd></div></dl></section><section class="detail-group"><h2>Endereço</h2><dl class="detail-fields"><div><dt>Logradouro</dt><dd>{{ broker.logradouro || '—' }}{{ broker.numero ? ', ' + broker.numero : '' }}</dd></div><div><dt>Cidade</dt><dd>{{ broker.cidade || '—' }}</dd></div><div><dt>UF</dt><dd>{{ broker.uf || '—' }}</dd></div></dl></section></div>
      <div class="page-actions"><button mat-stroked-button type="button" (click)="remove(broker)">Excluir corretora</button><a class="back-link" routerLink="/corretoras">Voltar para corretoras</a></div>
    } @else {<p class="ui-state" role="status">{{ error() || 'Carregando corretora…' }}</p>}
  </section>`
})
export class CorretoraDetailPage {
  private readonly service = inject(CorretoraService);
  private readonly route = inject(ActivatedRoute);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);
  private readonly feedback = inject(FeedbackService);
  protected readonly item = signal<Corretora | null>(null);
  protected readonly error = signal('');
  constructor() { this.service.get(Number(this.route.snapshot.paramMap.get('id'))).subscribe({ next: (item) => this.item.set(item), error: (error: { message?: string }) => this.error.set(error.message ?? 'Não foi possível carregar a corretora.') }); }
  protected remove(item: Corretora) { const name = item.nomeFantasia || item.razaoSocial || item.cnpj; this.dialog.open(ConfirmDialogComponent, { data: { title: 'Excluir corretora?', message: `Você está prestes a excluir "${name}". A corretora só poderá ser removida se não possuir registros vinculados.`, confirmLabel: 'Excluir corretora' }, autoFocus: 'first-tabbable', width: 'min(100% - 2rem,32rem)' }).afterClosed().subscribe((confirmed) => { if (!confirmed) return; this.service.delete(item.id).subscribe({ next: () => { this.feedback.success('Corretora excluída com sucesso.'); this.router.navigateByUrl('/corretoras'); }, error: (error: unknown) => this.feedback.error(maintenanceError(error, 'Não foi possível excluir a corretora.', 'Esta corretora não está mais disponível.')) }); }); }
}
