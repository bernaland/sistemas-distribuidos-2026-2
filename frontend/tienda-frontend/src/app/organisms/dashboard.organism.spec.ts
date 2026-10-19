import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { DashboardOrganism } from './dashboard.organism';
import {
  CustomerService,
  ProductService,
  SupplierService,
  ReportService,
  GatewayService,
  AuthService
} from '../services';

describe('DashboardOrganism', () => {
  let component: DashboardOrganism;
  let fixture: ComponentFixture<DashboardOrganism>;
  let customerServiceSpy: jasmine.SpyObj<CustomerService>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let supplierServiceSpy: jasmine.SpyObj<SupplierService>;
  let reportServiceSpy: jasmine.SpyObj<ReportService>;
  let gatewayServiceSpy: jasmine.SpyObj<GatewayService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    customerServiceSpy = jasmine.createSpyObj('CustomerService', ['list']);
    productServiceSpy = jasmine.createSpyObj('ProductService', ['list']);
    supplierServiceSpy = jasmine.createSpyObj('SupplierService', ['list']);
    reportServiceSpy = jasmine.createSpyObj('ReportService', ['getSalesByCustomer']);
    gatewayServiceSpy = jasmine.createSpyObj('GatewayService', ['checkHealth']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['hasRole']);

    customerServiceSpy.list.and.returnValue(of([{ cedula: '1', name: 'C1', address: 'A', phone: 'P', email: 'E' }]));
    productServiceSpy.list.and.returnValue(of([{ code: '1', name: 'P1', purchasePrice: 1, salePrice: 2, ivaRate: 19, supplierNit: 'N' }]));
    supplierServiceSpy.list.and.returnValue(of([{ nit: '1', name: 'S1', address: 'A', phone: 'P', city: 'B' }]));
    reportServiceSpy.getSalesByCustomer.and.returnValue(of({ customers: [], grandTotal: 50000 }));
    gatewayServiceSpy.checkHealth.and.returnValue(of({ status: 'UP', service: 'api-gateway' }));
    authServiceSpy.hasRole.and.returnValue(true);

    await TestBed.configureTestingModule({
      imports: [DashboardOrganism],
      providers: [
        { provide: CustomerService, useValue: customerServiceSpy },
        { provide: ProductService, useValue: productServiceSpy },
        { provide: SupplierService, useValue: supplierServiceSpy },
        { provide: ReportService, useValue: reportServiceSpy },
        { provide: GatewayService, useValue: gatewayServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardOrganism);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('debe inicializarse y cargar métricas de microservicios', () => {
    expect(component).toBeTruthy();
    expect(component.metrics()[0].value).toContain('50,000');
    expect(component.metrics()[1].value).toBe('1');
    expect(component.servicesStatus()[0].status).toBe('En línea');
  });

  it('verificarGateway: maneja error marcando sin conexión', () => {
    gatewayServiceSpy.checkHealth.and.returnValue(throwError(() => new Error('Offline')));
    component['verificarGateway']();
    expect(component.servicesStatus()[0].status).toBe('Sin conexión');
  });

  it('getFilteredModules: muestra todos los módulos para ROLE_ADMIN', () => {
    authServiceSpy.hasRole.and.returnValue(true);
    const modules = component.getFilteredModules();
    expect(modules.length).toBe(6);
  });

  it('getFilteredModules: restringe módulos para ROLE_USER mostrando solo Ventas', () => {
    authServiceSpy.hasRole.and.returnValue(false);
    const modules = component.getFilteredModules();
    expect(modules.length).toBe(1);
    expect(modules[0].name).toBe('Ventas');
  });

  it('navigateTo: invoca la navegación del router', () => {
    spyOn(router, 'navigate');
    component.navigateTo('/sales');
    expect(router.navigate).toHaveBeenCalledWith(['/sales']);
  });
});
