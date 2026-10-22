import { Component, OnInit, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CurrencyPipe } from '@angular/common';
import { CustomerService, ProductService, SaleService } from '../services';
import { SaleItemRequest, SaleRequest } from '../models';
import { ButtonAtom, InputAtom, LabelAtom } from '../atoms';

interface SaleItemRow {
  codigo: string;
  nombre: string;
  cantidad: number;
  precioUnitario: number;
  ivaRate: number;
  valorTotal: number;
}

@Component({
  selector: 'organism-sales',
  standalone: true,
  imports: [FormsModule, CurrencyPipe, ButtonAtom, InputAtom, LabelAtom],
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
              <i class="bi bi-receipt me-1"></i> Consecutivo: {{ consecutivoVenta ? '#' + consecutivoVenta : 'Nuevo' }}
            </span>
          </div>
        </div>

        @if (notification(); as notif) {
          <div [class]="'alert py-2 px-3 mb-3 d-flex align-items-center gap-2 ' + notif.type">
            <i class="bi bi-check-circle-fill"></i>
            <span>{{ notif.text }}</span>
          </div>
        }

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
        <div class="d-flex justify-content-between align-items-center mb-2">
          <h3 class="h6 fw-bold mb-0 text-secondary">
            <i class="bi bi-bag-plus me-2 text-primary"></i>Artículos de la Venta (1 a 3 renglones)
          </h3>
          @if (items.length < 3) {
            <atom-button variant="outline" (onClick)="agregarFila()">
              <i class="bi bi-plus-lg me-1"></i> Agregar Renglón
            </atom-button>
          }
        </div>

        <div class="table-responsive mb-3">
          <table class="table table-bordered align-middle">
            <thead class="table-light small text-uppercase text-muted">
              <tr>
                <th style="width: 180px;">Cód. Producto</th>
                <th>Nombre del Producto</th>
                <th style="width: 120px;">Cant.</th>
                <th style="width: 140px;">P. Unitario</th>
                <th style="width: 100px;">IVA</th>
                <th style="width: 160px;">V. Total</th>
                <th style="width: 50px;" class="text-center">#</th>
              </tr>
            </thead>
            <tbody>
              @for (item of items; track $index; let i = $index) {
                <tr>
                  <td>
                    <input class="form-control-tg form-control-sm" [(ngModel)]="item.codigo" (blur)="lookupItem(i)" placeholder="Ej. 1" />
                  </td>
                  <td>
                    <input class="form-control-tg form-control-sm bg-light" [(ngModel)]="item.nombre" readonly placeholder="Descripción del producto" />
                  </td>
                  <td>
                    <input type="number" min="1" max="1000" class="form-control-tg form-control-sm text-center" [(ngModel)]="item.cantidad" (input)="calcTotals()" />
                  </td>
                  <td class="text-end small">
                    {{ item.precioUnitario | currency:'USD':'symbol':'1.0-0' }}
                  </td>
                  <td class="text-center small">
                    <span class="badge bg-light text-dark border">{{ item.ivaRate }}%</span>
                  </td>
                  <td class="fw-bold text-end">
                    {{ item.valorTotal | currency:'USD':'symbol':'1.0-0' }}
                  </td>
                  <td class="text-center">
                    @if (items.length > 1) {
                      <button (click)="quitarFila(i)" class="btn btn-sm btn-link text-danger p-0" title="Eliminar renglón">
                        <i class="bi bi-trash"></i>
                      </button>
                    }
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        <!-- Totals & Actions -->
        <div class="row justify-content-end mt-4 pt-3 border-top g-3">
          <div class="col-md-4 col-12">
            <div class="p-3 bg-light rounded-3 d-flex flex-column gap-2">
              <div class="d-flex justify-content-between text-muted small">
                <span>Subtotal Venta:</span>
                <span class="fw-semibold">{{ subtotal | currency:'USD':'symbol':'1.0-0' }}</span>
              </div>
              <div class="d-flex justify-content-between text-muted small">
                <span>Total IVA:</span>
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
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SalesOrganism implements OnInit {
  private readonly customerService = inject(CustomerService);
  private readonly productService = inject(ProductService);
  private readonly saleService = inject(SaleService);

  consecutivoVenta: number | null = null;
  cedulaCliente = '';
  nombreCliente = '';
  readonly notification = signal<{ text: string; type: string } | null>(null);

  items: SaleItemRow[] = [
    { codigo: '', nombre: '', cantidad: 1, precioUnitario: 0, ivaRate: 0, valorTotal: 0 }
  ];

  subtotal = 0;
  ivaTotal = 0;
  totalFinal = 0;

  ngOnInit(): void {
    this.limpiarVenta();
  }

  consultarCliente(): void {
    if (!this.cedulaCliente.trim()) {
      this.setNotification('Por favor ingrese la cédula del cliente', 'alert-warning');
      return;
    }
    this.customerService.getByCedula(this.cedulaCliente.trim()).subscribe({
      next: (c) => {
        this.nombreCliente = c.name;
        this.setNotification(`Cliente confirmado: ${c.name}`, 'alert-success');
      },
      error: () => {
        this.nombreCliente = '';
        this.setNotification('Cliente no encontrado en la base de datos', 'alert-warning');
      }
    });
  }

  lookupItem(index: number): void {
    const item = this.items[index];
    if (!item.codigo.trim()) return;
    this.productService.getByCode(item.codigo.trim()).subscribe({
      next: (p) => {
        item.nombre = p.name;
        item.precioUnitario = p.salePrice;
        item.ivaRate = p.ivaRate;
        this.calcTotals();
      },
      error: () => {
        item.nombre = 'Producto no encontrado';
        item.precioUnitario = 0;
        item.ivaRate = 0;
        this.calcTotals();
      }
    });
  }

  agregarFila(): void {
    if (this.items.length < 3) {
      this.items.push({ codigo: '', nombre: '', cantidad: 1, precioUnitario: 0, ivaRate: 0, valorTotal: 0 });
    }
  }

  quitarFila(index: number): void {
    if (this.items.length > 1) {
      this.items.splice(index, 1);
      this.calcTotals();
    }
  }

  calcTotals(): void {
    let sumSubtotal = 0;
    let sumIva = 0;
    for (const it of this.items) {
      const lineSubtotal = (Number(it.cantidad) || 0) * (Number(it.precioUnitario) || 0);
      const lineIva = lineSubtotal * ((Number(it.ivaRate) || 0) / 100);
      it.valorTotal = lineSubtotal + lineIva;
      sumSubtotal += lineSubtotal;
      sumIva += lineIva;
    }
    this.subtotal = Math.round(sumSubtotal * 100) / 100;
    this.ivaTotal = Math.round(sumIva * 100) / 100;
    this.totalFinal = Math.round((this.subtotal + this.ivaTotal) * 100) / 100;
  }

  confirmarVenta(): void {
    if (!this.cedulaCliente || !this.nombreCliente) {
      this.setNotification('Debe validar primero un cliente antes de registrar la venta', 'alert-warning');
      return;
    }
    const validLines = this.items.filter(it => it.codigo.trim() && it.cantidad > 0);
    if (validLines.length === 0) {
      this.setNotification('Debe registrar al menos un producto válido', 'alert-warning');
      return;
    }
    this.ejecutarVenta(validLines);
  }

  private ejecutarVenta(validLines: SaleItemRow[]): void {
    const payload: SaleRequest = {
      customerCedula: this.cedulaCliente,
      items: validLines.map(it => ({ productCode: it.codigo.trim(), quantity: Number(it.cantidad) }))
    };
    this.saleService.create(payload).subscribe({
      next: (res) => {
        this.consecutivoVenta = res.id;
        this.setNotification(`Venta #${res.id} registrada exitosamente por $${res.grandTotal}`, 'alert-success');
        this.limpiarFormulario();
      },
      error: (err) => {
        const msg = err?.error?.message || 'Error al procesar la venta en el backend';
        this.setNotification(msg, 'alert-danger');
      }
    });
  }

  private limpiarFormulario(): void {
    this.cedulaCliente = '';
    this.nombreCliente = '';
    this.items = [{ codigo: '', nombre: '', cantidad: 1, precioUnitario: 0, ivaRate: 0, valorTotal: 0 }];
    this.calcTotals();
  }

  limpiarVenta(): void {
    this.consecutivoVenta = null;
    this.limpiarFormulario();
    this.notification.set(null);
  }

  private setNotification(text: string, type: string): void {
    this.notification.set({ text, type });
  }
}
