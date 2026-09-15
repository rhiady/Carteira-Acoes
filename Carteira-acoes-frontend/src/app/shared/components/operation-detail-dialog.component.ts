import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { Operacao } from '../../core/models/domain.models';

@Component({
  selector: 'app-operation-detail-dialog',
  imports: [CurrencyPipe, DatePipe, MatDialogModule],
  template: `<h2 mat-dialog-title>{{ data.ticker }} · {{ data.tipo }}</h2><div mat-dialog-content class="operation-dialog-content"><p class="dialog-entity">{{ data.nomeEmpresa }}</p><p>{{ data.mercado }} · {{ data.moeda }}</p><dl class="detail-fields"><div><dt>Quantidade × preço</dt><dd>{{ data.quantidade }} × {{ data.precoUnitario | currency:data.moeda }}</dd></div><div><dt>Valor bruto</dt><dd>{{ data.valorBruto | currency:data.moeda }}</dd></div><div><dt>Custos</dt><dd>{{ costs | currency:data.moeda }}</dd></div><div><dt>Valor líquido</dt><dd>{{ data.valorLiquido | currency:data.moeda }}</dd></div><div><dt>Data</dt><dd>{{ data.dataHora | date:'medium' }}</dd></div></dl></div><div mat-dialog-actions align="end"><button mat-button type="button" (click)="ref.close()">Fechar</button></div>`
})
export class OperationDetailDialogComponent {
  protected readonly ref = inject(MatDialogRef<OperationDetailDialogComponent>);
  protected readonly data = inject<Operacao>(MAT_DIALOG_DATA);
  protected readonly costs = this.data.corretagem + this.data.impostos + this.data.valorAdicional;
}
