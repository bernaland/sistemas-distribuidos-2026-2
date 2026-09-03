import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  template: `
    <div class="min-vh-100 d-flex flex-column">
      <header class="main-header py-3 px-3 px-md-4 border-bottom d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div class="d-flex align-items-center gap-3">
          <div class="brand-badge-icon d-flex align-items-center justify-content-center shadow-sm">
            <i class="bi bi-shop text-white fs-4"></i>
          </div>
          <div>
            <h1 class="h5 mb-0 fw-bold tracking-tight text-dark">Tienda Genérica</h1>
            <p class="small text-muted mb-0">Plataforma Empresarial de Microservicios Distribuidos</p>
          </div>
        </div>

        <div class="d-flex align-items-center gap-2">
          <div class="d-none d-md-flex flex-column text-end">
            <span class="fw-semibold small text-dark">Administrador Central</span>
            <span class="text-muted text-xs" style="font-size: 0.75rem;">admin&#64;tiendagenerica.com</span>
          </div>
          <button (click)="goToLogin()" class="btn btn-sm btn-outline-secondary rounded-pill px-3 py-1 ms-2" title="Cerrar Sesión / Ir al Login">
            <i class="bi bi-box-arrow-right me-1"></i> Salir
          </button>
        </div>
      </header>

      <molecule-nav-tabs></molecule-nav-tabs>

      <main class="content-container flex-grow-1 p-3 p-md-4">
        <div class="container-fluid maxw-xl mx-auto animate-fade-in">
          <router-outlet></router-outlet>
        </div>
      </main>

      <footer class="footer-bar py-3 px-4 border-top text-center text-muted small mt-auto">
        <div class="container d-flex justify-content-center align-items-center">
          <span>&copy; 2026 Universidad El Bosque — Sistemas Distribuidos</span>
        </div>
      </footer>
    </div>
  `,
  styles: [`
    .main-header {
      background: rgba(255, 255, 255, 0.88);
      backdrop-filter: blur(14px);
      border-color: rgba(226, 232, 240, 0.85) !important;
    }
    .brand-badge-icon {
      width: 44px;
      height: 44px;
      border-radius: 12px;
      background: linear-gradient(135deg, #4f46e5 0%, #06b6d4 100%);
      box-shadow: 0 4px 14px rgba(79, 70, 229, 0.3);
    }
    .content-container {
      min-height: calc(100vh - 200px);
    }
    .maxw-xl {
      max-width: 1280px;
    }
    .footer-bar {
      background: rgba(255, 255, 255, 0.7);
      backdrop-filter: blur(8px);
      border-color: rgba(226, 232, 240, 0.8) !important;
    }
  `]
})
export class MainLayoutTemplate {
  constructor(private router: Router) {}

  goToLogin(): void {
    this.router.navigate(['/']);
  }
}
