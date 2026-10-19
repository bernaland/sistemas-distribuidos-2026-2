import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { UsersOrganism } from './users.organism';
import { UserService } from '../services';
import { User } from '../models';

describe('UsersOrganism', () => {
  let component: UsersOrganism;
  let fixture: ComponentFixture<UsersOrganism>;
  let userServiceSpy: jasmine.SpyObj<UserService>;

  const mockUsers: User[] = [
    { id: 1, cedula: '101', username: 'u1', name: 'User One', email: 'u1@t.com', roles: 'ROLE_USER', enabled: true }
  ];

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['list', 'getByCedula', 'create', 'update', 'delete']);
    userServiceSpy.list.and.returnValue(of(mockUsers));

    await TestBed.configureTestingModule({
      imports: [UsersOrganism],
      providers: [{ provide: UserService, useValue: userServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(UsersOrganism);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar lista de usuarios', () => {
    expect(component).toBeTruthy();
    expect(component.usersList().length).toBe(1);
  });

  it('cargarUsuarios: maneja error al cargar usuarios del servidor', () => {
    userServiceSpy.list.and.returnValue(throwError(() => new Error('Error')));
    component.cargarUsuarios();
    expect(component.notification()?.text).toBe('Error al cargar usuarios del servidor');
  });

  it('selectUser: selecciona y llena campos', () => {
    component.selectUser(mockUsers[0]);
    expect(component.selectedId).toBe(1);
    expect(component.cedula).toBe('101');
    expect(component.usuario).toBe('u1');
    expect(component.notification()?.text).toContain('u1');
  });

  it('consultar: valida cédula vacía y consulta usuario existente o no encontrado', () => {
    component.cedula = '';
    component.consultar();
    expect(component.notification()?.text).toContain('Ingrese una cédula');

    userServiceSpy.getByCedula.and.returnValue(of(mockUsers[0]));
    component.cedula = '101';
    component.consultar();
    expect(component.selectedId).toBe(1);

    userServiceSpy.getByCedula.and.returnValue(throwError(() => new Error('404')));
    component.consultar();
    expect(component.notification()?.text).toContain('no encontrado');
  });

  it('crear: valida datos obligatorios y registra nuevo usuario', () => {
    component.cedula = '';
    component.crear();
    expect(component.notification()?.text).toContain('complete cédula');

    userServiceSpy.create.and.returnValue(of(mockUsers[0]));
    component.cedula = '101';
    component.usuario = 'u1';
    component.nombre = 'User One';
    component.crear();
    expect(component.notification()?.text).toContain('registrado');
  });

  it('crear: expone error 400/409 del backend', () => {
    userServiceSpy.create.and.returnValue(throwError(() => ({
      error: { message: 'El usuario ya existe' }
    })));
    component.cedula = '101';
    component.usuario = 'u1';
    component.nombre = 'User One';
    component.crear();
    expect(component.notification()?.text).toBe('El usuario ya existe');
  });

  it('actualizar y borrar: valida selección previa y ejecuta operación', () => {
    component.selectedId = null;
    component.actualizar();
    expect(component.notification()?.text).toContain('Seleccione un usuario');
    component.borrar();
    expect(component.notification()?.text).toContain('Seleccione un usuario');

    component.selectedId = 1;
    userServiceSpy.update.and.returnValue(of(mockUsers[0]));
    component.actualizar();
    expect(component.notification()?.text).toContain('actualizado');

    userServiceSpy.delete.and.returnValue(of(undefined));
    component.borrar();
    expect(component.notification()?.text).toContain('eliminado');
  });
});
