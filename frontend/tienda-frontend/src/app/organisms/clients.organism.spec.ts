import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ClientsOrganism } from './clients.organism';
import { CustomerService } from '../services';
import { Customer } from '../models';

describe('ClientsOrganism', () => {
  let component: ClientsOrganism;
  let fixture: ComponentFixture<ClientsOrganism>;
  let customerServiceSpy: jasmine.SpyObj<CustomerService>;

  const mockClients: Customer[] = [
    { cedula: '102030', name: 'Maria Gomez', address: 'Calle 10 # 20', phone: '3101234567', email: 'm@t.com' }
  ];

  beforeEach(async () => {
    customerServiceSpy = jasmine.createSpyObj('CustomerService', ['list', 'getByCedula', 'create', 'update', 'delete']);
    customerServiceSpy.list.and.returnValue(of(mockClients));

    await TestBed.configureTestingModule({
      imports: [ClientsOrganism],
      providers: [{ provide: CustomerService, useValue: customerServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(ClientsOrganism);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar clientes', () => {
    expect(component).toBeTruthy();
    expect(component.clientsList().length).toBe(1);
  });

  it('cargarClientes: maneja error al consultar lista', () => {
    customerServiceSpy.list.and.returnValue(throwError(() => new Error('Error')));
    component.cargarClientes();
    expect(component.notification()?.text).toBe('Error al cargar clientes del servidor');
  });

  it('selectClient: llena campos con el cliente seleccionado', () => {
    component.selectClient(mockClients[0]);
    expect(component.cedula).toBe('102030');
    expect(component.nombre).toBe('Maria Gomez');
    expect(component.notification()?.text).toContain('Maria Gomez');
  });

  it('consultar: valida cédula y realiza búsqueda', () => {
    component.cedula = '';
    component.consultar();
    expect(component.notification()?.text).toContain('Ingrese una cédula');

    customerServiceSpy.getByCedula.and.returnValue(of(mockClients[0]));
    component.cedula = '102030';
    component.consultar();
    expect(component.nombre).toBe('Maria Gomez');

    customerServiceSpy.getByCedula.and.returnValue(throwError(() => new Error('404')));
    component.consultar();
    expect(component.notification()?.text).toContain('no encontrado');
  });

  it('crear: valida datos obligatorios y registra nuevo cliente o expone error', () => {
    component.cedula = '';
    component.crear();
    expect(component.notification()?.text).toContain('complete cédula');

    customerServiceSpy.create.and.returnValue(of(mockClients[0]));
    component.cedula = '102030';
    component.nombre = 'Maria Gomez';
    component.crear();
    expect(component.notification()?.text).toContain('registrado');

    customerServiceSpy.create.and.returnValue(throwError(() => ({
      error: { message: 'Cédula ya registrada' }
    })));
    component.cedula = '102030';
    component.nombre = 'Maria Gomez';
    component.crear();
    expect(component.notification()?.text).toBe('Cédula ya registrada');
  });

  it('actualizar y borrar: valida cédula y ejecuta operación', () => {
    component.cedula = '';
    component.actualizar();
    expect(component.notification()?.text).toContain('Seleccione o ingrese');
    component.borrar();
    expect(component.notification()?.text).toContain('Seleccione un cliente');

    component.cedula = '102030';
    customerServiceSpy.update.and.returnValue(of(mockClients[0]));
    component.actualizar();
    expect(component.notification()?.text).toContain('actualizado');

    customerServiceSpy.delete.and.returnValue(of(undefined));
    component.borrar();
    expect(component.notification()?.text).toContain('eliminado');
  });
});
