import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ReportsOrganism } from './reports.organism';
import { ReportService } from '../services';

describe('ReportsOrganism', () => {
  let component: ReportsOrganism;
  let fixture: ComponentFixture<ReportsOrganism>;
  let reportServiceSpy: jasmine.SpyObj<ReportService>;

  beforeEach(async () => {
    reportServiceSpy = jasmine.createSpyObj('ReportService', ['getSalesByCustomer', 'getUsers', 'getCustomers']);
    reportServiceSpy.getSalesByCustomer.and.returnValue(of({
      customers: [{ cedula: '101', name: 'Cliente Uno', totalSales: 25000 }],
      grandTotal: 25000
    }));
    reportServiceSpy.getUsers.and.returnValue(of([
      { cedula: '1', username: 'adm', name: 'Admin', email: 'a@t.com', roles: 'ROLE_ADMIN', enabled: true }
    ]));
    reportServiceSpy.getCustomers.and.returnValue(of([
      { cedula: '101', name: 'Cliente Uno', phone: '123', address: 'Dir', email: 'c@t.com' }
    ]));

    await TestBed.configureTestingModule({
      imports: [ReportsOrganism],
      providers: [{ provide: ReportService, useValue: reportServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(ReportsOrganism);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar el reporte consolidado de ventas', () => {
    expect(component).toBeTruthy();
    expect(component.activeReport()).toBe('sales');
    expect(component.salesReport().length).toBe(1);
    expect(component.grandTotal()).toBe(25000);
  });

  it('cargarReporteVentas: maneja error del servidor', () => {
    reportServiceSpy.getSalesByCustomer.and.returnValue(throwError(() => new Error('Error')));
    component['cargarReporteVentas']();
    expect(component.errorMessage()).toContain('No fue posible cargar el reporte de ventas');
  });

  it('setReportType: cambia a reporte de usuarios y maneja error', () => {
    component.setReportType('users');
    expect(component.activeReport()).toBe('users');
    expect(component.usersReport().length).toBe(1);

    reportServiceSpy.getUsers.and.returnValue(throwError(() => new Error('Error')));
    component.setReportType('users');
    expect(component.errorMessage()).toContain('No fue posible cargar el reporte de usuarios');
  });

  it('setReportType: cambia a reporte de clientes y maneja error', () => {
    component.setReportType('clients');
    expect(component.activeReport()).toBe('clients');
    expect(component.clientsReport().length).toBe(1);

    reportServiceSpy.getCustomers.and.returnValue(throwError(() => new Error('Error')));
    component.setReportType('clients');
    expect(component.errorMessage()).toContain('No fue posible cargar el reporte de clientes');
  });
});
