import { Routes } from '@angular/router';
import { authGuard, roleGuard } from 'shared';

export const routes: Routes = [
  {
    path: '', // default route for dashboard
    canActivate: [authGuard],
    loadComponent: () => import('./app').then(m => m.App)
  },
  {
    path: 'register-officer',
    canMatch: [authGuard, roleGuard],
    data: { roles: ['SUPER_OFFICER'] },
    loadComponent: () => import('./features/register-officer/register-officer.component').then(m => m.RegisterOfficerComponent)
  }
];
