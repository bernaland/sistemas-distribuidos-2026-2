import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { authGuard, roleGuard } from './index';
import { AuthService } from '../services';

function mockJwt(expSec: number, roles = 'ROLE_ADMIN'): string {
  const h = btoa(JSON.stringify({ alg: 'HS256' }));
  const p = btoa(JSON.stringify({ sub: 'admin', roles, exp: expSec }));
  return `${h}.${p}.sig`;
}

describe('Guards Unit Tests', () => {
  let authService: AuthService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideRouter([])
      ]
    });
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    sessionStorage.clear();
  });

  afterEach(() => {
    sessionStorage.clear();
  });

  it('authGuard: permite acceso si el token es válido y no ha expirado', () => {
    const validExp = Math.floor(Date.now() / 1000) + 900;
    authService.tokenSignal.set(mockJwt(validExp));
    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );
    expect(result).toBeTrue();
  });

  it('authGuard: deniega y redirige al login si no está autenticado', () => {
    authService.tokenSignal.set(null);
    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );
    expect(result.toString()).toBe('/');
  });

  it('authGuard: deniega y limpia sesión si el token expiró', () => {
    const pastExp = Math.floor(Date.now() / 1000) - 60;
    authService.tokenSignal.set(mockJwt(pastExp));
    spyOn(authService, 'logout').and.callThrough();
    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as unknown as ActivatedRouteSnapshot, {} as unknown as RouterStateSnapshot)
    );
    expect(authService.logout).toHaveBeenCalled();
    expect(result.toString()).toBe('/');
  });

  it('roleGuard: permite acceso si la ruta no especifica roles', () => {
    const route = { data: {} } as unknown as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(() =>
      roleGuard(route, {} as unknown as RouterStateSnapshot)
    );
    expect(result).toBeTrue();
  });

  it('roleGuard: permite acceso a usuario con rol requerido', () => {
    authService.currentUser.set({
      cedula: '1', username: 'adm', name: 'Admin', email: 'a@t.com', roles: 'ROLE_ADMIN'
    });
    const route = { data: { roles: ['ROLE_ADMIN'] } } as unknown as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(() =>
      roleGuard(route, {} as unknown as RouterStateSnapshot)
    );
    expect(result).toBeTrue();
  });

  it('roleGuard: bloquea y redirige a /dashboard si usuario carece de rol', () => {
    authService.currentUser.set({
      cedula: '2', username: 'cajero', name: 'Cajero', email: 'c@t.com', roles: 'ROLE_USER'
    });
    const route = { data: { roles: ['ROLE_ADMIN'] } } as unknown as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(() =>
      roleGuard(route, {} as unknown as RouterStateSnapshot)
    );
    expect(result.toString()).toBe('/dashboard');
  });
});
