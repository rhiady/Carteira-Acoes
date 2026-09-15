import { Component, inject, signal } from '@angular/core';
import { BreakpointObserver } from '@angular/cdk/layout';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AuthService } from './core/services/auth.service';
import { ThemeService } from './core/services/theme.service';
import { ThemeToggleComponent } from './shared/components/theme-toggle.component';

@Component({
  selector: 'app-root',
  imports: [MatButtonModule, MatIconModule, MatListModule, MatSidenavModule, MatToolbarModule, RouterOutlet, RouterLink, RouterLinkActive, ThemeToggleComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private readonly breakpointObserver = inject(BreakpointObserver);
  protected readonly router = inject(Router);
  protected readonly auth = inject(AuthService);
  protected readonly theme = inject(ThemeService);

  protected readonly title = signal('Carteira Ações');
  protected readonly compactNavigation = signal(this.breakpointObserver.isMatched('(max-width: 760px)'));
  protected readonly navigationOpen = signal(!this.compactNavigation());
  protected readonly pageTitle = signal('Início');

  constructor() {
    this.router.events.pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd)).subscribe((event) => this.pageTitle.set(this.titleForUrl(event.urlAfterRedirects)));
    this.pageTitle.set(this.titleForUrl(this.router.url));
    this.breakpointObserver.observe('(max-width: 760px)').subscribe(({ matches }) => {
      this.compactNavigation.set(matches);
      this.navigationOpen.set(!matches);
    });
  }

  private titleForUrl(url: string): string {
    if (url.startsWith('/dashboard')) return 'Dashboard';
    if (url.startsWith('/carteiras')) return 'Carteiras';
    if (url.startsWith('/acoes')) return 'Ações';
    if (url.startsWith('/operacoes')) return 'Operações';
    if (url.startsWith('/corretoras')) return 'Corretoras';
    if (url.startsWith('/usuarios')) return 'Usuários';
    return 'Início';
  }

  protected toggleNavigation() {
    this.navigationOpen.update((isOpen) => !isOpen);
  }

  protected closeNavigationOnCompactViewport() {
    if (this.compactNavigation()) {
      this.navigationOpen.set(false);
    }
  }

  protected logout() {
    this.auth.logout();
  }
}
