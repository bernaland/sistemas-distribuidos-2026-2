import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'organism-login',
  template: `
    <div class="login-wrapper d-flex align-items-center justify-content-center py-5">
      <div class="tg-card p-4 p-md-5 w-100 login-card animate-fade-in">
        <div class="text-center mb-4">
          <div class="login-badge-icon mx-auto mb-3 d-flex align-items-center justify-content-center">
            <i class="bi bi-shield-lock-fill text-white fs-3"></i>
          </div>
          <h2 class="h4 fw-bold text-dark mb-1">Iniciar Sesión</h2>
          <p class="text-muted small">Acceda al portal de administración de la Tienda Genérica</p>
        </div>

        <form (ngSubmit)="accept()" class="d-flex flex-column gap-3">
          <div>
            <atom-label text="Nombre de Usuario" [required]="true"></atom-label>
            <atom-input
              icon="bi-person"
              placeholder="Ej. admin"
              [model]="username"
              (modelChange)="username = $event.toString()">
            </atom-input>
          </div>

          <div>
            <atom-label text="Contraseña de Acceso" [required]="true"></atom-label>
            <atom-input
              type="password"
              icon="bi-lock"
              placeholder="••••••••"
              [model]="password"
              (modelChange)="password = $event.toString()">
            </atom-input>
          </div>

          <div *ngIf="errorMessage" class="alert alert-danger py-2 px-3 small d-flex align-items-center gap-2 mb-0">
            <i class="bi bi-exclamation-triangle-fill"></i>
            <span>{{ errorMessage }}</span>
          </div>

          <div class="d-flex justify-content-between align-items-center text-xs mt-1">
            <span class="badge bg-success-subtle text-success border border-success-subtle rounded-pill px-2 py-1">
              <i class="bi bi-shield-check me-1"></i> Sesión Cifrada
            </span>
            <small class="text-muted">Demo: admin / admin</small>
          </div>

          <div class="d-flex gap-2 justify-content-end mt-3">
            <atom-button variant="secondary" (onClick)="cancel()">
              <i class="bi bi-x-circle me-1"></i> Limpiar
            </atom-button>
            <atom-button variant="primary" type="submit">
              <i class="bi bi-box-arrow-in-right me-1"></i> Ingresar al Sistema
            </atom-button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .login-wrapper {
      min-height: calc(100vh - 280px);
    }
    .login-card {
      max-width: 460px;
      position: relative;
    }
    .login-badge-icon {
      width: 56px;
      height: 56px;
      border-radius: 16px;
      background: linear-gradient(135deg, #4f46e5 0%, #06b6d4 100%);
      box-shadow: 0 8px 20px rgba(79, 70, 229, 0.35);
    }
  `]
})
export class LoginOrganism {
  username = '';
  password = '';
  errorMessage = '';

  constructor(private router: Router) {}

  accept(): void {
    if (!this.username.trim()) {
      this.errorMessage = 'Por favor ingrese su usuario';
      return;
    }
    this.errorMessage = '';
    this.router.navigate(['/dashboard']);
  }

  cancel(): void {
    this.username = '';
    this.password = '';
    this.errorMessage = '';
  }
}
