import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { formatMoney, formatSignedMoney } from '../../core/formatters/financial.formatter';
import { AuthenticatedUser, Carteira, ResumoCarteira } from '../../core/models/domain.models';
import { CarteiraService } from '../../core/services/carteira.service';
import { AuthService } from '../../core/services/auth.service';
import { EmptyStateComponent, ErrorStateComponent, LoadingStateComponent } from '../../shared/components/ui-states.component';

interface PortfolioOverview {
  readonly portfolio: Carteira;
  readonly summary: ResumoCarteira | null;
}

@Component({
  selector: 'app-inicio-page',
  imports: [RouterLink, EmptyStateComponent, ErrorStateComponent, LoadingStateComponent],
  template: `
    <section class="inicio-page">
      <div class="page-heading inicio-heading"><div><p class="eyebrow">Carteira Ações</p><h1>Início</h1><p>Uma leitura rápida das suas carteiras, posições e próximos caminhos.</p></div><nav class="page-actions" aria-label="Acessos rápidos"><a routerLink="/dashboard">Visão geral</a><a routerLink="/carteiras">Carteiras</a></nav></div>
      @if (loading()) { <app-loading-state message="Carregando suas carteiras…" /> }
      @else if (error()) { <app-error-state [message]="error()" (retry)="load()" /> }
      @else if (!user()) { <app-empty-state message="Não foi possível identificar sua sessão." /> }
      @else if (!portfolios().length) { <div class="inicio-context"><span>Carteiras de</span><strong>{{ user()!.nome }}</strong></div><app-empty-state message="Você ainda não possui carteiras." /><a class="inicio-primary-link" routerLink="/carteiras/nova">Criar carteira</a> }
      @else {
        <div class="inicio-context"><span>Carteiras de</span><strong>{{ user()!.nome }}</strong><small>Dados organizados a partir da sua conta.</small></div>
        <div class="inicio-portfolio-grid" aria-label="Carteiras disponíveis">
          @for (overview of overviews(); track overview.portfolio.id) {
            <article class="inicio-portfolio-card"><div><span class="eyebrow">Carteira</span><h2>{{ overview.portfolio.nome }}</h2><p>Corretora #{{ overview.portfolio.corretoraId }}</p></div>@if (overview.summary) { <div class="inicio-portfolio-card__metrics">@for (subtotal of overview.summary.resumosPorMoeda; track subtotal.moeda) { <div><span>Valor · {{ subtotal.moeda }}</span><strong>{{ money(subtotal.valorAtual, subtotal.moeda) }}</strong><small>{{ signedMoney(subtotal.lucroPrejuizo, subtotal.moeda) }}</small></div> }</div> } @else { <p class="inicio-unavailable">Resumo financeiro indisponível.</p> }<a [routerLink]="['/carteiras', overview.portfolio.id]">Abrir carteira <span aria-hidden="true">→</span></a></article>
          }
        </div>
        <div class="inicio-next-steps"><div><h2>Continue acompanhando</h2><p>Consulte posições, operações e cotações usando os dados oficiais da aplicação.</p></div><div class="inicio-next-steps__links"><a routerLink="/acoes">Explorar ações</a><a routerLink="/operacoes">Ver operações</a></div></div>
      }
    </section>`,
})
export class InicioPage {
  private readonly auth = inject(AuthService);
  private readonly portfoliosService = inject(CarteiraService);
  protected readonly user = signal<AuthenticatedUser | null>(null);
  protected readonly portfolios = signal<readonly Carteira[]>([]);
  protected readonly overviews = signal<readonly PortfolioOverview[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal('');

  constructor() { this.load(); }

  protected load() {
    this.loading.set(true); this.error.set('');
    const selectedUser = this.auth.currentUser();
    this.user.set(selectedUser);
    if (!selectedUser) { this.portfolios.set([]); this.overviews.set([]); this.loading.set(false); return; }
        this.portfoliosService.listForUser(selectedUser.id).subscribe({
          next: (portfolioPage) => this.loadSummaries(portfolioPage.content),
          error: (httpError: { message?: string }) => { this.error.set(httpError.message ?? 'Não foi possível carregar as carteiras.'); this.loading.set(false); },
        });
  }

  protected money(value: number | null, currency: string) { return formatMoney(value, currency); }
  protected signedMoney(value: number | null, currency: string) { return formatSignedMoney(value, currency); }

  private loadSummaries(portfolios: Carteira[]) {
    this.portfolios.set(portfolios);
    if (!portfolios.length) { this.overviews.set([]); this.loading.set(false); return; }
    forkJoin(portfolios.map((portfolio) => this.portfoliosService.summary(portfolio.id).pipe(catchError(() => of(null)))))
      .subscribe({
        next: (summaries) => this.overviews.set(portfolios.map((portfolio, index) => ({ portfolio, summary: summaries[index] ?? null }))),
        complete: () => this.loading.set(false),
      });
  }
}
