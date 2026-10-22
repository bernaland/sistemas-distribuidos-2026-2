import { Component, OnInit, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { ProductService } from '../services';
import { Product } from '../models';
import { ButtonAtom, InputAtom, LabelAtom } from '../atoms';

@Component({
  selector: 'organism-products',
  standalone: true,
  imports: [CurrencyPipe, ButtonAtom, InputAtom, LabelAtom],
  template: `
    <div class="d-flex flex-column gap-4">
      <!-- Form Card -->
      <div class="tg-card p-4">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
          <div class="d-flex align-items-center gap-3">
            <div class="p-2 bg-warning-subtle text-warning-emphasis rounded-3">
              <i class="bi bi-box-seam fs-4"></i>
            </div>
            <div>
              <h2 class="h5 fw-bold mb-0 text-dark">Gestión de Productos</h2>
              <span class="text-muted small">Control de catálogo, precios y márgenes de venta</span>
            </div>
          </div>
          <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle rounded-pill px-3 py-2">
            <i class="bi bi-boxes me-1"></i> {{ productsList().length }} Referencias
          </span>
        </div>

        @if (notification(); as notif) {
          <div [class]="'alert py-2 px-3 mb-3 d-flex align-items-center gap-2 ' + notif.type">
            <i class="bi bi-info-circle-fill"></i>
            <span>{{ notif.text }}</span>
          </div>
        }

        <div class="row g-3">
          <div class="col-md-4 col-12">
            <atom-label text="Código del Producto" [required]="true"></atom-label>
            <atom-input icon="bi-upc-scan" placeholder="Ej. 1" [model]="codigo" (modelChange)="codigo = $event.toString()"></atom-input>
          </div>
          <div class="col-md-4 col-12">
            <atom-label text="Nombre del Producto" [required]="true"></atom-label>
            <atom-input icon="bi-tag" placeholder="Ej. Café Gourmet 500g" [model]="nombre" (modelChange)="nombre = $event.toString()"></atom-input>
          </div>
          <div class="col-md-4 col-12">
            <atom-label text="NIT Proveedor" [required]="true"></atom-label>
            <atom-input icon="bi-briefcase" placeholder="Ej. 900112233" [model]="nitProveedor" (modelChange)="nitProveedor = $event.toString()"></atom-input>
          </div>
          <div class="col-md-4 col-12">
            <atom-label text="Precio de Compra" [required]="true"></atom-label>
            <atom-input type="number" icon="bi-cash" placeholder="0.00" [model]="precioCompra" (modelChange)="precioCompra = $event.toString()"></atom-input>
          </div>
          <div class="col-md-4 col-12">
            <atom-label text="IVA Compra (%)" [required]="true"></atom-label>
            <atom-input type="number" icon="bi-percent" placeholder="19" [model]="ivaCompra" (modelChange)="ivaCompra = $event.toString()"></atom-input>
          </div>
          <div class="col-md-4 col-12">
            <atom-label text="Precio de Venta" [required]="true"></atom-label>
            <atom-input type="number" icon="bi-cash-coin" placeholder="0.00" [model]="precioVenta" (modelChange)="precioVenta = $event.toString()"></atom-input>
          </div>
        </div>

        <div class="d-flex flex-wrap justify-content-end gap-2 mt-4 pt-3 border-top">
          <atom-button variant="secondary" (onClick)="consultar()">
            <i class="bi bi-search me-1"></i> Consultar
          </atom-button>
          <atom-button variant="primary" (onClick)="crear()">
            <i class="bi bi-plus-circle me-1"></i> Crear Producto
          </atom-button>
          <atom-button variant="secondary" (onClick)="actualizar()">
            <i class="bi bi-arrow-repeat me-1"></i> Actualizar
          </atom-button>
          <atom-button variant="danger" (onClick)="borrar()">
            <i class="bi bi-trash3 me-1"></i> Borrar
          </atom-button>
        </div>
      </div>

      <!-- CSV Bulk Import Card -->
      <div class="tg-card p-4 border-start border-4 border-primary">
        <div class="d-flex align-items-center gap-3 mb-3">
          <div class="p-2 bg-primary-subtle text-primary rounded-3">
            <i class="bi bi-file-earmark-spreadsheet fs-4"></i>
          </div>
          <div>
            <h3 class="h6 fw-bold mb-0 text-dark">Carga Masiva de Productos (CSV)</h3>
            <span class="text-muted small">Cargue un archivo CSV con las 6 columnas en orden para actualizar el catálogo</span>
          </div>
        </div>
        <div class="row align-items-center g-3">
          <div class="col-md-8 col-12">
            <input type="file" class="form-control" accept=".csv" (change)="onFileSelected($event)">
            <small class="text-muted">Formato: codigo_producto, nombre_producto, nitproveedor, precio_compra, ivacompra, precio_venta</small>
          </div>
          <div class="col-md-4 col-12 d-flex justify-content-end">
            <atom-button variant="primary" [disabled]="!selectedFile" (onClick)="importarCsv()">
              <i class="bi bi-upload me-1"></i> Subir Catálogo CSV
            </atom-button>
          </div>
        </div>
      </div>

      <!-- Table Card -->
      <div class="tg-card p-4">
        <h3 class="h6 fw-bold mb-3 text-secondary">
          <i class="bi bi-table me-2 text-warning"></i>Inventario y Precios
        </h3>
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light text-muted small text-uppercase">
              <tr>
                <th>Código</th>
                <th>Nombre</th>
                <th>NIT Prov.</th>
                <th>P. Compra</th>
                <th>IVA</th>
                <th>P. Venta</th>
                <th class="text-end">Acción</th>
              </tr>
            </thead>
            <tbody>
              @for (p of productsList(); track p.code) {
                <tr (click)="selectProduct(p)" role="button">
                  <td><span class="badge bg-light text-dark border">{{ p.code }}</span></td>
                  <td class="fw-semibold">{{ p.name }}</td>
                  <td class="text-muted small">{{ p.supplierNit }}</td>
                  <td>{{ p.purchasePrice | currency:'USD':'symbol':'1.0-0' }}</td>
                  <td><span class="badge bg-secondary-subtle text-secondary">{{ p.ivaRate }}%</span></td>
                  <td class="text-success fw-bold">{{ p.salePrice | currency:'USD':'symbol':'1.0-0' }}</td>
                  <td class="text-end">
                    <button class="btn btn-sm btn-outline-warning rounded-circle"><i class="bi bi-pencil"></i></button>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductsOrganism implements OnInit {
  private readonly productService = inject(ProductService);

  codigo = '';
  nombre = '';
  nitProveedor = '';
  precioCompra = '';
  ivaCompra = '19';
  precioVenta = '';
  selectedId: number | null = null;
  selectedFile: File | null = null;
  readonly notification = signal<{ text: string; type: string } | null>(null);
  readonly productsList = signal<Product[]>([]);

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.productService.list().subscribe({
      next: (data) => this.productsList.set(data),
      error: () => this.setNotification('Error al cargar catálogo de productos', 'alert-danger')
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files && input.files[0] ? input.files[0] : null;
  }

  importarCsv(): void {
    if (!this.selectedFile) return;
    this.productService.importCsv(this.selectedFile).subscribe({
      next: () => {
        this.setNotification('Catálogo CSV importado y actualizado exitosamente', 'alert-success');
        this.selectedFile = null;
        this.cargarProductos();
      },
      error: (err) => this.handleError(err, 'Error al importar catálogo CSV')
    });
  }

  selectProduct(p: Product): void {
    this.selectedId = p.id || null;
    this.codigo = p.code;
    this.nombre = p.name;
    this.nitProveedor = p.supplierNit;
    this.precioCompra = p.purchasePrice.toString();
    this.ivaCompra = p.ivaRate.toString();
    this.precioVenta = p.salePrice.toString();
    this.setNotification(`Producto ${p.name} seleccionado`, 'alert-info');
  }

  consultar(): void {
    if (!this.codigo.trim()) {
      this.setNotification('Ingrese un código de producto para consultar', 'alert-warning');
      return;
    }
    this.productService.getByCode(this.codigo.trim()).subscribe({
      next: (p) => {
        this.selectProduct(p);
        this.setNotification(`Producto encontrado: ${p.name}`, 'alert-success');
      },
      error: () => this.setNotification('Producto no encontrado con ese código', 'alert-warning')
    });
  }

  crear(): void {
    if (!this.codigo || !this.nombre || !this.nitProveedor) {
      this.setNotification('Complete código, nombre y NIT del proveedor', 'alert-warning');
      return;
    }
    const payload = this.buildPayload();
    this.productService.create(payload).subscribe({
      next: (created) => {
        this.setNotification(`Producto ${created.name} agregado`, 'alert-success');
        this.resetForm();
        this.cargarProductos();
      },
      error: (err) => this.handleError(err, 'Error al crear producto')
    });
  }

  actualizar(): void {
    if (!this.selectedId) {
      this.setNotification('Seleccione un producto para actualizar', 'alert-warning');
      return;
    }
    const payload = this.buildPayload();
    this.productService.update(this.selectedId, payload).subscribe({
      next: (updated) => {
        this.setNotification(`Producto ${updated.name} actualizado`, 'alert-success');
        this.cargarProductos();
      },
      error: (err) => this.handleError(err, 'Error al actualizar producto')
    });
  }

  borrar(): void {
    if (!this.selectedId) {
      this.setNotification('Seleccione un producto para borrar', 'alert-warning');
      return;
    }
    this.productService.delete(this.selectedId).subscribe({
      next: () => {
        this.setNotification('Producto eliminado', 'alert-danger');
        this.resetForm();
        this.cargarProductos();
      },
      error: (err) => this.handleError(err, 'Error al eliminar producto')
    });
  }

  private buildPayload(): Product {
    return {
      code: this.codigo,
      name: this.nombre,
      supplierNit: this.nitProveedor,
      purchasePrice: Number(this.precioCompra) || 0,
      ivaRate: Number(this.ivaCompra) || 0,
      salePrice: Number(this.precioVenta) || 0
    };
  }

  private handleError(err: { error?: { message?: string } }, fallback: string): void {
    this.setNotification(err?.error?.message || fallback, 'alert-danger');
  }

  private resetForm(): void {
    this.selectedId = null;
    this.codigo = '';
    this.nombre = '';
    this.nitProveedor = '';
    this.precioCompra = '';
    this.ivaCompra = '19';
    this.precioVenta = '';
  }

  private setNotification(text: string, type: string): void {
    this.notification.set({ text, type });
  }
}
