import { Component } from '@angular/core';

interface SaleItem {
  codigo: string;
  nombre: string;
  cantidad: number;
  precioUnitario: number;
  valorTotal: number;
}

@Component({
  selector: 'organism-sales',
  template: `
    <div class="d-flex flex-column gap-4">
      <div class="tg-card p-4">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
          <div class="d-flex align-items-center gap-3">
            <div class="p-2 bg-primary-subtle text-primary rounded-3">
              <i class="bi bi-cart-check fs-4"></i>
            </div>
            <div>
              <h2 class="h5 fw-bold mb-0 text-dark">Módulo de Ventas y Facturación</h2>
              <span class="text-muted small">Transacciones comerciales en tiempo real</span>
            </div>
          </div>
          <div class="d-flex align-items-center gap-2">
            <span class="badge bg-light text-dark border px-3 py-2">
              <i class="bi bi-receipt me-1"></i> Consecutivo: #00{{ consecutivoVenta }}
            </span>
          </div>
        </div>

        <div *ngIf="notification" [class]="'alert py-2 px-3 mb-3 d-flex align-items-center gap-2 ' + notification.type">
          <i class="bi bi-check-circle-fill"></i>
          <span>{{ notification.text }}</span>
        </div>

        <!-- Client Lookup Row -->
        <div class="row g-3 align-items-end mb-4 p-3 bg-light rounded-3">
          <div class="col-md-4 col-12">
            <atom-label text="Cédula del Cliente" [required]="true"></atom-label>
            <atom-input icon="bi-person-badge" placeholder="Ej. 1020304050" [model]="cedulaCliente" (modelChange)="cedulaCliente = $event.toString()"></atom-input>
          </div>
          <div class="col-md-2 col-12">
            <atom-button variant="secondary" (onClick)="consultarCliente()">
              <i class="bi bi-search me-1"></i> Validar
            </atom-button>
          </div>
          <div class="col-md-6 col-12">
            <atom-label text="Cliente Confirmado"></atom-label>
            <input class="form-control-tg w-100 bg-white" [value]="nombreCliente || 'Pendiente de consulta'" readonly />
          </div>
        </div>

        <!-- Product Line Items -->
        <h3 class="h6 fw-bold mb-3 text-secondary">
          <i class="bi bi-bag-plus me-2 text-primary"></i>Artículos de la Venta
        </h3>
        <div class="table-responsive mb-3">
          <table class="table table-bordered align-middle">
            <thead class="table-light small text-uppercase text-muted">
              <tr>
                <th style="width: 180px;">Cód. Producto</th>
                <th>Nombre del Producto</th>
                <th style="width: 120px;">Cant.</th>
                <th style="width: 160px;">V. Total</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let item of items; let i = index">
                <td>
                  <input class="form-control-tg form-control-sm" [(ngModel)]="item.codigo" (blur)="lookupItem(i)" placeholder="Ej. PRD-101" />
                </td>
                <td>
                  <input class="form-control-tg form-control-sm bg-light" [(ngModel)]="item.nombre" readonly placeholder="Descripción" />
                </td>
                <td>
                  <input type="number" min="1" class="form-control-tg form-control-sm text-center" [(ngModel)]="item.cantidad" (input)="calcTotals()" />
                </td>
                <td class="fw-bold text-end">
                  {{ item.valorTotal | currency:'USD':'symbol':'1.0-0' }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Totals & Actions -->
        <div class="row justify-content-end mt-4 pt-3 border-top g-3">
          <div class="col-md-4 col-12">
            <div class="p-3 bg-light rounded-3 d-flex flex-column gap-2">
              <div class="d-flex justify-content-between text-muted small">
                <span>Total Venta:</span>
                <span class="fw-semibold">{{ subtotal | currency:'USD':'symbol':'1.0-0' }}</span>
              </div>
              <div class="d-flex justify-content-between text-muted small">
                <span>Total IVA (19%):</span>
                <span class="fw-semibold">{{ ivaTotal | currency:'USD':'symbol':'1.0-0' }}</span>
              </div>
              <div class="d-flex justify-content-between h5 fw-bold text-primary border-top pt-2 mb-0">
                <span>Total con IVA:</span>
                <span>{{ totalFinal | currency:'USD':'symbol':'1.0-0' }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="d-flex justify-content-end gap-2 mt-4">
          <atom-button variant="secondary" (onClick)="limpiarVenta()">
            <i class="bi bi-x-circle me-1"></i> Cancelar Venta
          </atom-button>
          <atom-button variant="primary" (onClick)="confirmarVenta()">
            <i class="bi bi-check2-circle me-1"></i> Confirmar Transacción
          </atom-button>
        </div>
      </div>
    </div>
  `
})
export class SalesOrganism {
  consecutivoVenta = 104;
  cedulaCliente = '';
  nombreCliente = '';
  notification: { text: string; type: string } | null = null;

  items: SaleItem[] = [
    { codigo: 'PRD-101', nombre: 'Leche Entera 1000ml', cantidad: 2, precioUnitario: 4200, valorTotal: 8400 },
    { codigo: 'PRD-102', nombre: 'Café Especial Quindío 500g', cantidad: 1, precioUnitario: 19500, valorTotal: 19500 },
    { codigo: '', nombre: '', cantidad: 1, precioUnitario: 0, valorTotal: 0 }
  ];

  subtotal = 27900;
  ivaTotal = 5301;
  totalFinal = 33201;

  consultarCliente(): void {
    if (this.cedulaCliente === '1020304050') {
      this.nombreCliente = 'Distribuidora del Norte';
      this.setNotification('Cliente verificado con éxito', 'alert-success');
    } else {
      this.nombreCliente = 'Cliente Mostrador';
      this.setNotification('Cliente general asignado', 'alert-info');
    }
  }

  lookupItem(index: number): void {
    const item = this.items[index];
    if (item.codigo === 'PRD-103') {
      item.nombre = 'Arroz Premium 1000g';
      item.precioUnitario = 5300;
    }
    this.calcTotals();
  }

  calcTotals(): void {
    let sum = 0;
    for (const it of this.items) {
      it.valorTotal = (it.cantidad || 0) * (it.precioUnitario || 0);
      sum += it.valorTotal;
    }
    this.subtotal = sum;
    this.ivaTotal = Math.round(sum * 0.19);
    this.totalFinal = this.subtotal + this.ivaTotal;
  }

  confirmarVenta(): void {
    if (this.totalFinal <= 0) {
      this.setNotification('Debe agregar al menos un producto para facturar', 'alert-warning');
      return;
    }
    this.setNotification(`Factura #${this.consecutivoVenta} generada con éxito por ${this.totalFinal}`, 'alert-success');
    this.consecutivoVenta++;
  }

  limpiarVenta(): void {
    this.cedulaCliente = '';
    this.nombreCliente = '';
    this.items = [{ codigo: '', nombre: '', cantidad: 1, precioUnitario: 0, valorTotal: 0 }];
    this.calcTotals();
  }

  private setNotification(text: string, type: string): void {
    this.notification = { text, type };
  }
}
