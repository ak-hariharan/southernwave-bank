import { inject } from '@angular/core';
import { Router, CanMatchFn, Route } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Functional Guard: Checks if the user's role matches any of the required roles.
 * Prevents the route from matching if unauthorized (Stealthy Approach).
 * Pass required roles as 'roles' key in route data: { data: { roles: ['SUPER_OFFICER'] } }
 */
export const roleGuard: CanMatchFn = (route: Route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const allowedRoles = route.data?.['roles'] as Array<string>;
  const userRole = authService.getUserRole();

  if (userRole && allowedRoles.includes(userRole)) {
    return true;
  }

  // Redirect to unauthorized page if user is not authorized
  router.navigate(['/unauthorized']);
  return false;
};
