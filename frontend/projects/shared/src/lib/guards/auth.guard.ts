import { inject } from '@angular/core';
import { Router, CanMatchFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Functional Guard: Ensures user is logged in before even matching a route.
 * Redirects to the home page if not authenticated.
 */
export const authGuard: CanMatchFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  // Redirect to home if not logged in
  router.navigate(['/']);
  return false;
};
