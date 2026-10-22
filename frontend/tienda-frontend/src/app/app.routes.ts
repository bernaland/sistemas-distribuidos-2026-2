import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './guards';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/login.page').then(m => m.LoginPage)
  },
  {
    path: 'login',
    redirectTo: '',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard.page').then(m => m.DashboardPage),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_USER'] }
  },
  {
    path: 'sales',
    loadComponent: () => import('./pages/sales.page').then(m => m.SalesPage),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_USER'] }
  },
  {
    path: 'users',
    loadComponent: () => import('./pages/users.page').then(m => m.UsersPage),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },
  {
    path: 'clients',
    loadComponent: () => import('./pages/clients.page').then(m => m.ClientsPage),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },
  {
    path: 'providers',
    loadComponent: () => import('./pages/providers.page').then(m => m.ProvidersPage),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },
  {
    path: 'products',
    loadComponent: () => import('./pages/products.page').then(m => m.ProductsPage),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },
  {
    path: 'reports',
    loadComponent: () => import('./pages/reports.page').then(m => m.ReportsPage),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },
  {
    path: '**',
    redirectTo: ''
  }
];
