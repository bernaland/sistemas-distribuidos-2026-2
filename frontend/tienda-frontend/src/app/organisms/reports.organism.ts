import { Component, OnInit, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CurrencyPipe, NgClass } from '@angular/common';
import { ReportService } from '../services';
import { UserReportRow, CustomerReportRow, CustomerSaleRow } from '../models';
import { ButtonAtom } from '../atoms';

@Component({
  selector: 'organism-reports',
  standalone: true,
  imports: [CurrencyPipe, NgClass, ButtonAtom],
  template: `
    <div class="d-flex flex-column gap-4">
      <div class="tg-card p-4">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
          <div class="d-flex align-items-center gap-3">
            <div class="p-2 bg-info-subtle text-info rounded-3">
              <i class="bi bi-bar-chart-line fs-4"></i>
            </div>
            <div>
              <h2 class="h5 fw-bold mb-0 text-dark">Módulo de Reportes e Informes</h2>
              <span class="text-muted small">Generación y auditoría de transacciones comerciales</span>
            </div>
          </div>
          <span class="badge bg-primary-subtle text-primary border border-primary-subtle rounded-pill px-3 py-2">
            <i class="bi bi-file-earmark-bar-graph me-1"></i> Auditoría Consolidada
          </span>
        </div>

        @if (errorMessage()) {
          <div class="alert alert-danger py-2 px-3 mb-3 small d-flex align-items-center gap-2">
            <i class="bi bi-exclamation-octagon-fill"></i>
            <span>{{ errorMessage() }}</span>
          </div>
        }

        <div class="d-flex flex-wrap gap-2 mb-4">
          <atom-button [variant]="activeReport() === 'users' ? 'primary' : 'secondary'" (onClick)="setReportType('users')">
            <i class="bi bi-people me-1"></i> Listado de Usuarios
          </atom-button>
          <atom-button [variant]="activeReport() === 'clients' ? 'primary' : 'secondary'" (onClick)="setReportType('clients')">
            <i class="bi bi-person-lines-fill me-1"></i> Listado de Clientes
          </atom-button>
          <atom-button [variant]="activeReport() === 'sales' ? 'primary' : 'secondary'" (onClick)="setReportType('sales')">
            <i class="bi bi-cash-stack me-1"></i> Ventas por Cliente
          </atom-button>
        </div>

        <!-- Sales by Client Report View -->
        @if (activeReport() === 'sales') {
          <div>
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h3 class="h6 fw-bold mb-0 text-secondary">
                <i class="bi bi-graph-up me-2 text-primary"></i>Consolidado de Ventas por Cliente
              </h3>
              <span class="small text-muted">Total acumulado en el sistema</span>
            </div>
            <div class="table-responsive">
              <table class="table table-hover align-middle mb-0">
                <thead class="table-light small text-uppercase text-muted">
                  <tr>
                    <th>Cédula</th>
                    <th>Nombre del Cliente</th>
                    <th class="text-end">Valor Total Compras</th>
                  </tr>
                </thead>
                <tbody>
                  @for (row of salesReport(); track row.cedula) {
                    <tr>
                      <td class="fw-semibold">{{ row.cedula }}</td>
                      <td>{{ row.name }}</td>
                      <td class="text-end fw-bold text-success">
                        {{ row.totalSales | currency:'USD':'symbol':'1.0-0' }}
                      </td>
                    </tr>
                  }
                  @if (salesReport().length === 0) {
                    <tr>
                      <td colspan="3" class="text-center text-muted py-3">No hay registros de ventas disponibles</td>
                    </tr>
                  }
                </tbody>
                @if (salesReport().length > 0) {
                  <tfoot class="table-group-divider">
                    <tr class="fw-bold table-light">
                      <td colspan="2" class="text-end">Total General Acumulado:</td>
                      <td class="text-end text-primary h6 mb-0">{{ grandTotal() | currency:'USD':'symbol':'1.0-0' }}</td>
                    </tr>
                  </tfoot>
                }
              </table>
            </div>
          </div>
        }

        <!-- Users Report View -->
        @if (activeReport() === 'users') {
          <div>
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h3 class="h6 fw-bold mb-0 text-secondary">
                <i class="bi bi-people me-2 text-primary"></i>Directorio de Usuarios del Sistema
              </h3>
              <span class="small text-muted">{{ usersReport().length }} usuarios registrados</span>
            </div>
            <div class="table-responsive">
              <table class="table table-hover align-middle mb-0">
                <thead class="table-light small text-uppercase text-muted">
                  <tr>
                    <th>Cédula</th>
                    <th>Nombre</th>
                    <th>Usuario</th>
                    <th>Correo</th>
                    <th>Roles</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  @for (u of usersReport(); track u.cedula) {
                    <tr>
                      <td class="fw-semibold">{{ u.cedula }}</td>
                      <td>{{ u.name }}</td>
                      <td><span class="badge bg-light text-dark border">{{ u.username }}</span></td>
                      <td class="text-muted small">{{ u.email }}</td>
                      <td><span class="badge bg-primary-subtle text-primary">{{ u.roles }}</span></td>
                      <td>
                        <span class="badge" [ngClass]="u.enabled ? 'bg-success-subtle text-success' : 'bg-secondary-subtle text-secondary'">
                          {{ u.enabled ? 'Activo' : 'Inactivo' }}
                        </span>
                      </td>
                    </tr>
                  }
                  @if (usersReport().length === 0) {
                    <tr>
                      <td colspan="6" class="text-center text-muted py-3">No hay usuarios disponibles</td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        }

        <!-- Clients Report View -->
        @if (activeReport() === 'clients') {
          <div>
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h3 class="h6 fw-bold mb-0 text-secondary">
                <i class="bi bi-person-lines-fill me-2 text-primary"></i>Listado Oficial de Clientes
              </h3>
              <span class="small text-muted">{{ clientsReport().length }} clientes registrados</span>
            </div>
            <div class="table-responsive">
              <table class="table table-hover align-middle mb-0">
                <thead class="table-light small text-uppercase text-muted">
                  <tr>
                    <th>Cédula</th>
                    <th>Nombre</th>
                    <th>Teléfono</th>
                    <th>Dirección</th>
                    <th>Correo Electrónico</th>
                  </tr>
                </thead>
                <tbody>
                  @for (c of clientsReport(); track c.cedula) {
                    <tr>
                      <td class="fw-semibold">{{ c.cedula }}</td>
                      <td>{{ c.name }}</td>
                      <td>{{ c.phone }}</td>
                      <td class="text-muted small">{{ c.address }}</td>
                      <td>{{ c.email }}</td>
                    </tr>
                  }
                  @if (clientsReport().length === 0) {
                    <tr>
                      <td colspan="5" class="text-center text-muted py-3">No hay clientes disponibles</td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        }
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReportsOrganism implements OnInit {
  private readonly reportService = inject(ReportService);

  readonly activeReport = signal<'users' | 'clients' | 'sales'>('sales');
  readonly salesReport = signal<CustomerSaleRow[]>([]);
  readonly usersReport = signal<UserReportRow[]>([]);
  readonly clientsReport = signal<CustomerReportRow[]>([]);
  readonly grandTotal = signal(0);
  readonly errorMessage = signal('');

  ngOnInit(): void {
    this.cargarReporteVentas();
  }

  setReportType(type: 'users' | 'clients' | 'sales'): void {
    this.activeReport.set(type);
    this.errorMessage.set('');
    if (type === 'sales') this.cargarReporteVentas();
    if (type === 'users') this.cargarReporteUsuarios();
    if (type === 'clients') this.cargarReporteClientes();
  }

  private cargarReporteVentas(): void {
    this.reportService.getSalesByCustomer().subscribe({
      next: (res) => {
        this.salesReport.set(res?.customers || []);
        this.grandTotal.set(res?.grandTotal || 0);
      },
      error: () => this.errorMessage.set('No fue posible cargar el reporte de ventas del servidor.')
    });
  }

  private cargarReporteUsuarios(): void {
    this.reportService.getUsers().subscribe({
      next: (data) => this.usersReport.set(data || []),
      error: () => this.errorMessage.set('No fue posible cargar el reporte de usuarios del servidor.')
    });
  }

  private cargarReporteClientes(): void {
    this.reportService.getCustomers().subscribe({
      next: (data) => this.clientsReport.set(data || []),
      error: () => this.errorMessage.set('No fue posible cargar el reporte de clientes del servidor.')
    });
  }
}
