import { Component, OnInit, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import {
  CustomerService,
  ProductService,
  SupplierService,
  ReportService,
  GatewayService,
  AuthService
} from '../services';

interface KpiMetric {
  title: string;
  value: string;
  badge: string;
  icon: string;
  bgGradient: string;
}

interface ServiceRouteStatus {
  name: string;
  route: string;
  status: string;
}

@Component({
  selector: 'organism-dashboard',
  standalone: true,
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
        @for (kpi of metrics(); track kpi.title) {
          <div class="col-md-3 col-sm-6 col-12">
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
        }
      </div>

      <!-- Modules Quick Actions & Microservices Status -->
      <div class="row g-3">
        <div class="col-lg-8 col-12">
          <div class="tg-card p-4 h-100">
            <h3 class="h6 fw-bold mb-3 text-secondary">
              <i class="bi bi-lightning-charge me-2 text-primary"></i>Accesos Rápidos a Módulos
            </h3>
            <div class="row g-3">
              @for (mod of getFilteredModules(); track mod.route) {
                <div class="col-md-4 col-sm-6 col-12">
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
              }
            </div>
          </div>
        </div>

        <div class="col-lg-4 col-12">
          <div class="tg-card p-4 h-100">
            <h3 class="h6 fw-bold mb-3 text-secondary">
              <i class="bi bi-hdd-network me-2 text-info"></i>Rutas Gateway & Microservicios
            </h3>
            <ul class="list-group list-group-flush small">
              @for (svc of servicesStatus(); track svc.name) {
                <li class="list-group-item d-flex justify-content-between align-items-center px-0 bg-transparent">
                  <div>
                    <span class="fw-semibold">{{ svc.name }}</span>
                    <div class="text-muted text-xs">{{ svc.route }}</div>
                  </div>
                  <span class="badge bg-success-subtle text-success rounded-pill px-2 py-1">
                    <span class="status-dot-pulse me-1"></span> {{ svc.status }}
                  </span>
                </li>
              }
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
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardOrganism implements OnInit {
  private readonly router = inject(Router);
  private readonly customerService = inject(CustomerService);
  private readonly productService = inject(ProductService);
  private readonly supplierService = inject(SupplierService);
  private readonly reportService = inject(ReportService);
  private readonly gatewayService = inject(GatewayService);
  private readonly authService = inject(AuthService);

  readonly metrics = signal<KpiMetric[]>([
    { title: 'Ventas Totales', value: '$ 0', badge: 'En vivo', icon: 'bi-currency-dollar', bgGradient: 'bg-primary' },
    { title: 'Clientes Activos', value: '0', badge: 'En vivo', icon: 'bi-people', bgGradient: 'bg-info' },
    { title: 'Productos en Stock', value: '0', badge: 'Catálogo', icon: 'bi-box-seam', bgGradient: 'bg-warning text-dark' },
    { title: 'Proveedores Activos', value: '0', badge: 'Verificados', icon: 'bi-truck', bgGradient: 'bg-success' }
  ]);

  readonly allQuickModules = [
    { name: 'Usuarios', desc: 'Gestionar accesos y roles', icon: 'bi-people', route: '/users', adminOnly: true },
    { name: 'Clientes', desc: 'Directorio de clientes', icon: 'bi-person-badge', route: '/clients', adminOnly: true },
    { name: 'Proveedores', desc: 'Cadena de suministro', icon: 'bi-truck', route: '/providers', adminOnly: true },
    { name: 'Productos', desc: 'Inventario y precios', icon: 'bi-box-seam', route: '/products', adminOnly: true },
    { name: 'Ventas', desc: 'Facturación en línea', icon: 'bi-cart-check', route: '/sales', adminOnly: false },
    { name: 'Reportes', desc: 'Informes y analítica', icon: 'bi-bar-chart-line', route: '/reports', adminOnly: true }
  ];

  readonly servicesStatus = signal<ServiceRouteStatus[]>([
    { name: 'Gateway', route: '/api/gateway/health', status: 'Verificando...' },
    { name: 'user-service', route: '/api/users/**', status: 'Activo' },
    { name: 'catalog-service', route: '/api/products/**', status: 'Activo' },
    { name: 'order-service', route: '/api/orders/**', status: 'Activo' },
    { name: 'customer-service', route: '/api/customers/**', status: 'Activo' },
    { name: 'supplier-service', route: '/api/suppliers/**', status: 'Activo' }
  ]);

  ngOnInit(): void {
    this.cargarMetricas();
    this.verificarGateway();
  }

  getFilteredModules() {
    const isAdmin = this.authService.hasRole('ROLE_ADMIN');
    return this.allQuickModules.filter(m => !m.adminOnly || isAdmin);
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }

  private verificarGateway(): void {
    this.gatewayService.checkHealth().subscribe({
      next: (h) => this.updateGatewayStatus(h.status === 'UP' ? 'En línea' : 'Inactivo'),
      error: () => this.updateGatewayStatus('Sin conexión')
    });
  }

  private updateGatewayStatus(status: string): void {
    this.servicesStatus.update(list => {
      const copy = [...list];
      copy[0] = { ...copy[0], status };
      return copy;
    });
  }

  private cargarMetricas(): void {
    this.reportService.getSalesByCustomer().subscribe({
      next: (r) => this.actualizarMetrica(0, `$ ${r.grandTotal.toLocaleString()}`)
    });
    this.customerService.list().subscribe({
      next: (c) => this.actualizarMetrica(1, c.length.toString())
    });
    this.productService.list().subscribe({
      next: (p) => this.actualizarMetrica(2, p.length.toString())
    });
    this.supplierService.list().subscribe({
      next: (s) => this.actualizarMetrica(3, s.length.toString())
    });
  }

  private actualizarMetrica(index: number, val: string): void {
    this.metrics.update(curr => {
      const copy = [...curr];
      copy[index] = { ...copy[index], value: val };
      return copy;
    });
  }
}
