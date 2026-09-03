import { Component } from '@angular/core';

interface ReportSummary {
  cedula: string;
  nombre: string;
  totalCompras: number;
}

@Component({
  selector: 'organism-reports',
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

        <div class="d-flex flex-wrap gap-2 mb-4">
          <atom-button variant="secondary" (onClick)="setReportType('users')">
            <i class="bi bi-people me-1"></i> Listado de Usuarios
          </atom-button>
          <atom-button variant="secondary" (onClick)="setReportType('clients')">
            <i class="bi bi-person-lines-fill me-1"></i> Listado de Clientes
          </atom-button>
          <atom-button variant="primary" (onClick)="setReportType('sales')">
            <i class="bi bi-cash-stack me-1"></i> Ventas por Cliente
          </atom-button>
        </div>

        <!-- Sales by Client Report View -->
        <div *ngIf="activeReport === 'sales'">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="h6 fw-bold mb-0 text-secondary">
              <i class="bi bi-graph-up me-2 text-primary"></i>Consolidado de Ventas por Cliente
            </h3>
            <span class="small text-muted">Corte de mes actual</span>
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
                <tr *ngFor="let row of salesReport">
                  <td class="fw-semibold">{{ row.cedula }}</td>
                  <td>{{ row.nombre }}</td>
                  <td class="text-end fw-bold text-success">
                    {{ row.totalCompras | currency:'USD':'symbol':'1.0-0' }}
                  </td>
                </tr>
              </tbody>
              <tfoot class="table-group-divider">
                <tr class="fw-bold table-light">
                  <td colspan="2" class="text-end">Total General Acumulado:</td>
                  <td class="text-end text-primary h6 mb-0">{{ grandTotal | currency:'USD':'symbol':'1.0-0' }}</td>
                </tr>
              </tfoot>
            </table>
          </div>
        </div>

        <!-- Placeholder for Other Reports -->
        <div *ngIf="activeReport !== 'sales'" class="p-4 bg-light rounded-3 text-center text-muted">
          <i class="bi bi-file-earmark-text fs-2 mb-2 d-block text-secondary"></i>
          <h4 class="h6 fw-bold">Reporte de {{ activeReport === 'users' ? 'Usuarios' : 'Clientes' }}</h4>
          <p class="small mb-0">Datos sincronizados con la base de datos distribuida PostgreSQL.</p>
        </div>
      </div>
    </div>
  `
})
export class ReportsOrganism {
  activeReport: 'users' | 'clients' | 'sales' = 'sales';

  salesReport: ReportSummary[] = [
    { cedula: '1020304050', nombre: 'Distribuidora del Norte', totalCompras: 12450000 },
    { cedula: '1098765432', nombre: 'Supermercados La Estrella', totalCompras: 5890000 },
    { cedula: '1018293847', nombre: 'Comercializadora San José', totalCompras: 3420000 }
  ];

  grandTotal = 21760000;

  setReportType(type: 'users' | 'clients' | 'sales'): void {
    this.activeReport = type;
  }
}
