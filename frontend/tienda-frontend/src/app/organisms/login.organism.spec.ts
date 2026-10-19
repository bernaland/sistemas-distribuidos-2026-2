import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginOrganism } from './login.organism';
import { AuthService } from '../services';

describe('LoginOrganism', () => {
  let component: LoginOrganism;
  let fixture: ComponentFixture<LoginOrganism>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);
    await TestBed.configureTestingModule({
      imports: [LoginOrganism],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginOrganism);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('debe crearse correctamente', () => {
    expect(component).toBeTruthy();
  });

  it('accept: valida campos obligatorios si están vacíos', () => {
    component.username = '';
    component.password = '';
    component.accept();
    expect(component.errorMessage).toBe('Por favor ingrese usuario y contraseña');
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('accept: autentica con éxito y navega a /dashboard', () => {
    spyOn(router, 'navigate');
    authServiceSpy.login.and.returnValue(of({
      token: 'jwt-123',
      tokenType: 'Bearer',
      user: { cedula: '0', username: 'admin', name: 'Admin', email: 'a@t.com', roles: 'ROLE_ADMIN' }
    }));
    component.username = 'admin';
    component.password = 'Clave123';
    component.accept();
    expect(authServiceSpy.login).toHaveBeenCalledWith({ username: 'admin', password: 'Clave123' });
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
    expect(component.errorMessage).toBe('');
  });

  it('accept: maneja error de autenticación con mensaje del servidor', () => {
    authServiceSpy.login.and.returnValue(throwError(() => ({
      error: { message: 'Credenciales inválidas' }
    })));
    component.username = 'admin';
    component.password = 'wrong';
    component.accept();
    expect(component.errorMessage).toBe('Credenciales inválidas');
  });

  it('cancel: limpia los campos del formulario', () => {
    component.username = 'test';
    component.password = '123';
    component.errorMessage = 'error';
    component.cancel();
    expect(component.username).toBe('');
    expect(component.password).toBe('');
    expect(component.errorMessage).toBe('');
  });
});
