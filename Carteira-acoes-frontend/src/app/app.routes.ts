import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./features/login/login.page').then((m) => m.LoginPage) },
  { path: 'cadastro', loadComponent: () => import('./features/cadastro/cadastro.page').then((m) => m.CadastroPage) },
  { path: '', pathMatch: 'full', redirectTo: 'inicio' },
  { path: 'inicio', canActivate: [authGuard], loadComponent: () => import('./features/inicio/inicio.page').then((m) => m.InicioPage) },
  { path: 'dashboard', canActivate: [authGuard], loadComponent: () => import('./features/dashboard/dashboard.page').then((m) => m.DashboardPage) },
  { path: 'acoes', canActivate: [authGuard], loadComponent: () => import('./features/acoes/acoes.page').then((m) => m.AcoesPage) },
  { path: 'acoes/:id', canActivate: [authGuard], loadComponent: () => import('./features/acoes/acao-detail.page').then((m) => m.AcaoDetailPage) },
  { path: 'usuarios', canActivate: [authGuard], loadComponent: () => import('./features/usuarios/usuarios.page').then((m) => m.UsuariosPage) },
  { path: 'usuarios/novo', canActivate: [authGuard], loadComponent: () => import('./features/usuarios/usuario-form.page').then((m) => m.UsuarioFormPage) },
  { path: 'usuarios/:id', canActivate: [authGuard], loadComponent: () => import('./features/usuarios/usuario-detail.page').then((m) => m.UsuarioDetailPage) },
  { path: 'corretoras', canActivate: [authGuard], loadComponent: () => import('./features/corretoras/corretoras.page').then((m) => m.CorretorasPage) },
  { path: 'corretoras/nova', canActivate: [authGuard], loadComponent: () => import('./features/corretoras/corretora-form.page').then((m) => m.CorretoraFormPage) },
  { path: 'corretoras/buscar', canActivate: [authGuard], loadComponent: () => import('./features/corretoras/corretora-lookup.page').then((m) => m.CorretoraLookupPage) },
  { path: 'corretoras/:id', canActivate: [authGuard], loadComponent: () => import('./features/corretoras/corretora-detail.page').then((m) => m.CorretoraDetailPage) },
  { path: 'carteiras', canActivate: [authGuard], loadComponent: () => import('./features/carteiras/carteiras.page').then((m) => m.CarteirasPage) },
  { path: 'carteiras/nova', canActivate: [authGuard], loadComponent: () => import('./features/carteiras/carteira-form.page').then((m) => m.CarteiraFormPage) },
  { path: 'carteiras/:id', canActivate: [authGuard], loadComponent: () => import('./features/carteiras/carteira-detail.page').then((m) => m.CarteiraDetailPage) },
  { path: 'carteiras/:id/comprar', canActivate: [authGuard], loadComponent: () => import('./features/operacoes/compra.page').then((m) => m.CompraPage) },
  { path: 'carteiras/:id/vender', canActivate: [authGuard], loadComponent: () => import('./features/operacoes/venda.page').then((m) => m.VendaPage) },
  { path: 'carteiras/:id/operacoes', canActivate: [authGuard], loadComponent: () => import('./features/operacoes/historico.page').then((m) => m.HistoricoPage) },
  { path: 'operacoes', canActivate: [authGuard], loadComponent: () => import('./features/operacoes/operacoes.page').then((m) => m.OperacoesPage) },
  { path: 'operacoes/:id', canActivate: [authGuard], loadComponent: () => import('./features/operacoes/operacao-detail.page').then((m) => m.OperacaoDetailPage) },
  { path: '**', redirectTo: 'dashboard' },
];
