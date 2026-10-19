import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { Router, provideRouter } from '@angular/router';
import { AuthService } from './auth.service';
import { CustomerService } from './customer.service';
import { ProductService } from './product.service';
import { SaleService } from './sale.service';
import { GatewayService } from './gateway.service';
import { jwtInterceptor } from './jwt.interceptor';
import { errorInterceptor } from './error.interceptor';
import { authGuard, roleGuard } from '../guards';
import { environment } from '../../environments/environment';

function createMockJwt(expSeconds: number, roles = 'ROLE_ADMIN'): string {
  const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
  const payload = btoa(JSON.stringify({ sub: 'admin', roles, exp: expSeconds }));
  return `${header}.${payload}.signature`;
}

describe('Frontend Modern Services Suite', () => {
  let httpMock: HttpTestingController;
  let authService: AuthService;
  let customerService: CustomerService;
  let productService: ProductService;
  let saleService: SaleService;
  let gatewayService: GatewayService;
  let httpClient: HttpClient;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        CustomerService,
        ProductService,
        SaleService,
        GatewayService,
        provideRouter([]),
        provideHttpClient(withInterceptors([jwtInterceptor, errorInterceptor])),
        provideHttpClientTesting()
      ]
    });
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
    customerService = TestBed.inject(CustomerService);
    productService = TestBed.inject(ProductService);
    saleService = TestBed.inject(SaleService);
    gatewayService = TestBed.inject(GatewayService);
    httpClient = TestBed.inject(HttpClient);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
  });

  it('AuthService: debe autenticar y almacenar token en sessionStorage', () => {
    const mockRes = {
      token: 'jwt-xyz',
      tokenType: 'Bearer',
      user: { cedula: '0', username: 'admin', name: 'Admin', email: 'a@b.com', roles: 'ROLE_ADMIN' }
    };
    authService.login({ username: 'admin', password: '123' }).subscribe(res => {
      expect(res.token).toBe('jwt-xyz');
      expect(authService.getToken()).toBe('jwt-xyz');
      expect(authService.isAuthenticated()).toBeTrue();
      expect(authService.hasRole('ROLE_ADMIN')).toBeTrue();
    });
    const req = httpMock.expectOne(`${environment.apiUrl}/api/users/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockRes);
  });

  it('CustomerService: debe consultar cliente por cédula', () => {
    const mockCustomer = { cedula: '102030', name: 'Cliente Test', address: 'Calle 1', phone: '123', email: 'c@test.com' };
    customerService.getByCedula('102030').subscribe(c => {
      expect(c.name).toBe('Cliente Test');
    });
    const req = httpMock.expectOne(`${environment.apiUrl}/api/customers/102030`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCustomer);
  });

  it('ProductService: debe consultar producto por código', () => {
    const mockProduct = { code: '1', name: 'Leche', purchasePrice: 1000, salePrice: 1500, ivaRate: 0, supplierNit: '900112233' };
    productService.getByCode('1').subscribe(p => {
      expect(p.name).toBe('Leche');
    });
    const req = httpMock.expectOne(`${environment.apiUrl}/api/products/code/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProduct);
  });

  it('GatewayService: debe consultar el estado de salud del gateway', () => {
    gatewayService.checkHealth().subscribe(res => {
      expect(res.status).toBe('UP');
      expect(res.service).toBe('api-gateway');
    });
    const req = httpMock.expectOne(`${environment.apiUrl}/api/gateway/health`);
    expect(req.request.method).toBe('GET');
    req.flush({ status: 'UP', service: 'api-gateway' });
  });

  it('SaleService: reutiliza Idempotency-Key tras fallo y la renueva solo tras éxito', () => {
    const saleReq = { customerCedula: '102030', items: [{ productCode: '1', quantity: 1 }] };
    const keyOriginal = saleService.getOrCreateKey();
    expect(saleService.getOrCreateKey()).toBe(keyOriginal);

    saleService.create(saleReq).subscribe({
      next: () => fail('Se esperaba fallo en primer intento'),
      error: () => expect(saleService.getCurrentKey()).toBe(keyOriginal)
    });
    const req1 = httpMock.expectOne(`${environment.apiUrl}/api/sales`);
    expect(req1.request.headers.get('Idempotency-Key')).toBe(keyOriginal);
    req1.flush({ message: 'Error temporal' }, { status: 500, statusText: 'Server Error' });

    saleService.create(saleReq).subscribe(res => expect(res.id).toBe(101));
    const req2 = httpMock.expectOne(`${environment.apiUrl}/api/sales`);
    expect(req2.request.headers.get('Idempotency-Key')).toBe(keyOriginal);
    req2.flush({ id: 101, customerCedula: '102030', grandTotal: 1500 });
    expect(saleService.getCurrentKey()).toBeNull();
  });

  it('JwtInterceptor: adjunta token si URL empieza con apiUrl', () => {
    const validExp = Math.floor(Date.now() / 1000) + 900;
    const token = createMockJwt(validExp);
    sessionStorage.setItem('tg_auth_token', token);
    authService.tokenSignal.set(token);

    httpClient.get(`${environment.apiUrl}/api/customers`).subscribe();
    const req = httpMock.expectOne(`${environment.apiUrl}/api/customers`);
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${token}`);
    req.flush([]);
  });

  it('ErrorInterceptor: 401 ejecuta logout y redirección a login', () => {
    spyOn(authService, 'logout').and.callThrough();
    spyOn(router, 'navigate');

    httpClient.get(`${environment.apiUrl}/api/customers`).subscribe({
      error: (err) => expect(err.status).toBe(401)
    });
    const req = httpMock.expectOne(`${environment.apiUrl}/api/customers`);
    req.flush({ message: 'No autorizado' }, { status: 401, statusText: 'Unauthorized' });

    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('ErrorInterceptor: 503 produce mensaje amigable de indisponibilidad', () => {
    httpClient.get(`${environment.apiUrl}/api/customers`).subscribe({
      error: (err) => {
        expect(err.status).toBe(503);
        expect(err.error?.message).toContain('El servicio no está disponible');
      }
    });
    const req = httpMock.expectOne(`${environment.apiUrl}/api/customers`);
    req.flush('Gateway timeout', { status: 503, statusText: 'Service Unavailable' });
  });

  it('ErrorInterceptor: 400 y 409 exponen mensajes de validación para formularios', () => {
    httpClient.post(`${environment.apiUrl}/api/users`, {}).subscribe({
      error: (err) => {
        expect(err.status).toBe(400);
        expect(err.error?.message).toBe('Cédula duplicada');
      }
    });
    const req = httpMock.expectOne(`${environment.apiUrl}/api/users`);
    req.flush({ message: 'Cédula duplicada', errors: ['Cédula duplicada'] }, { status: 400, statusText: 'Bad Request' });
  });

  it('JWT Expiración: detecta token vencido y limpia sesión', () => {
    const pastExp = Math.floor(Date.now() / 1000) - 300;
    const expiredToken = createMockJwt(pastExp);
    sessionStorage.setItem('tg_auth_token', expiredToken);
    authService.tokenSignal.set(expiredToken);

    expect(authService.isTokenExpired()).toBeTrue();
    expect(authService.getToken()).toBeNull();
    expect(authService.isAuthenticated()).toBeFalse();
  });

  it('Guards: authGuard y roleGuard restringen accesos por rol', () => {
    const validExp = Math.floor(Date.now() / 1000) + 900;
    const adminToken = createMockJwt(validExp, 'ROLE_ADMIN');
    sessionStorage.setItem('tg_auth_token', adminToken);
    authService.tokenSignal.set(adminToken);
    authService.currentUser.set({
      cedula: '1', username: 'admin', name: 'Admin', email: 'adm@test.com', roles: 'ROLE_ADMIN'
    });

    const routeSnapshotMock: any = { data: { roles: ['ROLE_ADMIN'] } };
    const stateSnapshotMock: any = { url: '/users' };

    const canActivateAuth = TestBed.runInInjectionContext(() => authGuard(routeSnapshotMock, stateSnapshotMock));
    const canActivateRole = TestBed.runInInjectionContext(() => roleGuard(routeSnapshotMock, stateSnapshotMock));

    expect(canActivateAuth).toBeTrue();
    expect(canActivateRole).toBeTrue();
  });
});
