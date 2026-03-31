import { Routes } from '@angular/router';
import { authGuard, roleGuard } from 'shared';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./landing/landing.component').then(m => m.LandingComponent)
  },
  {
    path: 'landing',
    loadComponent: () => import('./landing/landing.component').then(m => m.LandingComponent)
  },
  {
    path: 'unauthorized',
    loadComponent: () => import('./unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent)
  },
  {
    path: 'register',
    canMatch: [authGuard, roleGuard],
    data: { roles: ['SUPER_OFFICER'] },
    loadComponent: () => import('./register-officer/register-officer.component').then(m => m.RegisterOfficerComponent)
  },
  {
    path: 'dashboard',
    canMatch: [authGuard],
    loadComponent: () => import('./dashboard-placeholder/dashboard-placeholder.component').then(m => m.DashboardPlaceholderComponent)
  }
];
