import { Component } from '@angular/core';
import { Router } from '@angular/router';

interface KpiMetric {
  title: string;
  value: string;
  badge: string;
  icon: string;
  bgGradient: string;
}

@Component({
  selector: 'organism-dashboard',
  template: `
    <div class="d-flex flex-column gap-4">
      <!-- Welcome Hero Banner -->
      <div class="tg-card p-4 hero-banner text-white position-relative overflow-hidden">
        <div class="position-relative z-1">
          <div class="d-flex align-items-center gap-2 mb-2">
            <span class="badge bg-white text-primary fw-bold px-3 py-1 rounded-pill">
              <i class="bi bi-stars me-1"></i> Sistema Operativo
            </span>
            <span class="small text-white-50">Nodo Central v1.0.0</span>
          </div>
          <h2 class="h3 fw-bold mb-1">Bienvenido al Panel de Control</h2>
          <p class="text-white-75 mb-0" style="max-width: 620px;">
            Monitoreo en tiempo real de operaciones distribuidas, transacciones comerciales y catálogos sincronizados.
          </p>
        </div>
      </div>

      <!-- KPI Metrics Grid -->
      <div class="row g-3">
        <div *ngFor="let kpi of metrics" class="col-md-3 col-sm-6 col-12">
          <div class="tg-card tg-card-interactive p-3 h-100 d-flex flex-column justify-content-between">
            <div class="d-flex justify-content-between align-items-start mb-2">
              <span class="text-muted small fw-semibold">{{ kpi.title }}</span>
              <div [class]="'p-2 rounded-3 text-white ' + kpi.bgGradient">
                <i [class]="'bi ' + kpi.icon"></i>
              </div>
            </div>
            <div>
              <div class="h4 fw-bold mb-1 text-dark">{{ kpi.value }}</div>
              <span class="badge bg-success-subtle text-success small">
                <i class="bi bi-arrow-up-right me-1"></i>{{ kpi.badge }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Modules Quick Actions & Microservices Status -->
      <div class="row g-3">
        <div class="col-lg-8 col-12">
          <div class="tg-card p-4 h-100">
            <h3 class="h6 fw-bold mb-3 text-secondary">
              <i class="bi bi-lightning-charge me-2 text-primary"></i>Accesos Rápidos a Módulos
            </h3>
            <div class="row g-3">
              <div *ngFor="let mod of quickModules" class="col-md-4 col-sm-6 col-12">
                <div
                  role="button"
                  (click)="navigateTo(mod.route)"
                  class="p-3 border rounded-3 text-center module-quick-card h-100 d-flex flex-column align-items-center justify-content-center gap-2">
                  <div class="p-3 bg-light rounded-circle text-primary fs-4">
                    <i [class]="'bi ' + mod.icon"></i>
                  </div>
                  <span class="fw-bold small text-dark">{{ mod.name }}</span>
                  <span class="text-muted text-xs" style="font-size: 0.78rem;">{{ mod.desc }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-lg-4 col-12">
          <div class="tg-card p-4 h-100">
            <h3 class="h6 fw-bold mb-3 text-secondary">
              <i class="bi bi-hdd-network me-2 text-info"></i>Estado de Microservicios
            </h3>
            <ul class="list-group list-group-flush small">
              <li *ngFor="let svc of servicesStatus" class="list-group-item d-flex justify-content-between align-items-center px-0 bg-transparent">
                <div>
                  <span class="fw-semibold">{{ svc.name }}</span>
                  <div class="text-muted text-xs">{{ svc.port }}</div>
                </div>
                <span class="badge bg-success-subtle text-success rounded-pill px-2 py-1">
                  <span class="status-dot-pulse me-1"></span> {{ svc.status }}
                </span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .hero-banner {
      background: linear-gradient(135deg, #4338ca 0%, #3b82f6 50%, #06b6d4 100%);
      border: none;
      box-shadow: 0 10px 25px -5px rgba(67, 56, 202, 0.4);
    }
    .module-quick-card {
      background: rgba(255, 255, 255, 0.6);
      transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
      cursor: pointer;
    }
    .module-quick-card:hover {
      transform: translateY(-3px);
      box-shadow: 0 6px 18px rgba(15, 23, 42, 0.08);
      border-color: #6366f1 !important;
    }
  `]
})
export class DashboardOrganism {
  readonly metrics: readonly KpiMetric[] = [
    { title: 'Ventas Totales', value: '$ 18,450,200', badge: '+14.2% mes', icon: 'bi-currency-dollar', bgGradient: 'bg-primary' },
    { title: 'Clientes Activos', value: '1,280', badge: '+8.5% mes', icon: 'bi-people', bgGradient: 'bg-info' },
    { title: 'Productos en Stock', value: '342', badge: '12 por agotar', icon: 'bi-box-seam', bgGradient: 'bg-warning text-dark' },
    { title: 'Proveedores Activos', value: '48', badge: '100% verificados', icon: 'bi-truck', bgGradient: 'bg-success' }
  ];

  readonly quickModules = [
    { name: 'Usuarios', desc: 'Gestionar accesos y roles', icon: 'bi-people', route: '/users' },
    { name: 'Clientes', desc: 'Directorio de clientes', icon: 'bi-person-badge', route: '/clients' },
    { name: 'Proveedores', desc: 'Cadena de suministro', icon: 'bi-truck', route: '/providers' },
    { name: 'Productos', desc: 'Inventario y precios', icon: 'bi-box-seam', route: '/products' },
    { name: 'Ventas', desc: 'Facturación en línea', icon: 'bi-cart-check', route: '/sales' },
    { name: 'Reportes', desc: 'Informes y analítica', icon: 'bi-bar-chart-line', route: '/reports' }
  ];

  readonly servicesStatus = [
    { name: 'Service: Usuarios', port: 'REST :8081', status: 'Activo' },
    { name: 'Service: Clientes', port: 'REST :8082', status: 'Activo' },
    { name: 'Service: Proveedores', port: 'REST :8083', status: 'Activo' },
    { name: 'Service: Productos', port: 'REST :8084', status: 'Activo' },
    { name: 'Service: Ventas', port: 'REST :8085', status: 'Activo' }
  ];

  constructor(private router: Router) {}

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }
}
