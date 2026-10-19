import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ProvidersOrganism } from './providers.organism';
import { SupplierService } from '../services';
import { Supplier } from '../models';

describe('ProvidersOrganism', () => {
  let component: ProvidersOrganism;
  let fixture: ComponentFixture<ProvidersOrganism>;
  let supplierServiceSpy: jasmine.SpyObj<SupplierService>;

  const mockProviders: Supplier[] = [
    { nit: '900112233', name: 'Lácteos del Valle', address: 'Zona Franca', phone: '6013456789', city: 'Bogotá' }
  ];

  beforeEach(async () => {
    supplierServiceSpy = jasmine.createSpyObj('SupplierService', ['list', 'getByNit', 'create', 'update', 'delete']);
    supplierServiceSpy.list.and.returnValue(of(mockProviders));

    await TestBed.configureTestingModule({
      imports: [ProvidersOrganism],
      providers: [{ provide: SupplierService, useValue: supplierServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(ProvidersOrganism);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar lista de proveedores', () => {
    expect(component).toBeTruthy();
    expect(component.providersList().length).toBe(1);
  });

  it('cargarProveedores: maneja error al consultar lista', () => {
    supplierServiceSpy.list.and.returnValue(throwError(() => new Error('Error')));
    component.cargarProveedores();
    expect(component.notification()?.text).toBe('Error al cargar proveedores del servidor');
  });

  it('selectProvider: llena los campos con el proveedor seleccionado', () => {
    component.selectProvider(mockProviders[0]);
    expect(component.nit).toBe('900112233');
    expect(component.nombre).toBe('Lácteos del Valle');
    expect(component.notification()?.text).toContain('Lácteos del Valle');
  });

  it('consultar: valida NIT y busca proveedor existente o inexistente', () => {
    component.nit = '';
    component.consultar();
    expect(component.notification()?.text).toContain('Ingrese un NIT');

    supplierServiceSpy.getByNit.and.returnValue(of(mockProviders[0]));
    component.nit = '900112233';
    component.consultar();
    expect(component.nombre).toBe('Lácteos del Valle');

    supplierServiceSpy.getByNit.and.returnValue(throwError(() => new Error('404')));
    component.consultar();
    expect(component.notification()?.text).toContain('no encontrado');
  });

  it('crear: valida datos obligatorios y registra nuevo proveedor o expone error', () => {
    component.nit = '';
    component.crear();
    expect(component.notification()?.text).toContain('complete NIT');

    supplierServiceSpy.create.and.returnValue(of(mockProviders[0]));
    component.nit = '900112233';
    component.nombre = 'Lácteos del Valle';
    component.crear();
    expect(component.notification()?.text).toContain('registrado');

    supplierServiceSpy.create.and.returnValue(throwError(() => ({
      error: { message: 'NIT ya registrado' }
    })));
    component.nit = '900112233';
    component.nombre = 'Lácteos del Valle';
    component.crear();
    expect(component.notification()?.text).toBe('NIT ya registrado');
  });

  it('actualizar y borrar: valida NIT y ejecuta operación', () => {
    component.nit = '';
    component.actualizar();
    expect(component.notification()?.text).toContain('Seleccione o ingrese');
    component.borrar();
    expect(component.notification()?.text).toContain('Seleccione un proveedor');

    component.nit = '900112233';
    supplierServiceSpy.update.and.returnValue(of(mockProviders[0]));
    component.actualizar();
    expect(component.notification()?.text).toContain('actualizado');

    supplierServiceSpy.delete.and.returnValue(of(undefined));
    component.borrar();
    expect(component.notification()?.text).toContain('eliminado');
  });
});
