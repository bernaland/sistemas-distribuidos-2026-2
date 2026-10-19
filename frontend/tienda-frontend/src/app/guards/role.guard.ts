import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services';

function checkRoleAccess(route: ActivatedRouteSnapshot, auth: AuthService): boolean {
  const allowedRoles = (route.data?.['roles'] as string[]) ?? [];
  if (allowedRoles.length === 0) {
    return true;
  }
  return allowedRoles.some(role => auth.hasRole(role));
}

export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (checkRoleAccess(route, auth)) {
    return true;
  }

  return router.parseUrl('/dashboard');
};
