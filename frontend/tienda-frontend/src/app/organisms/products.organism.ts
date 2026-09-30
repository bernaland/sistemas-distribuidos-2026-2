import { Component } from '@angular/core';

interface ProductRecord {
  codigo: string;
  nombre: string;
  nitProveedor: string;
  precioCompra: number;
  ivaCompra: number;
  precioVenta: number;
}

@Component({
  selector: 'organism-products',
  template: `
    <div class="d-flex flex-column gap-4">
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
            <i class="bi bi-boxes me-1"></i> {{ productsList.length }} Referencias
          </span>
        </div>

        <div *ngIf="notification" [class]="'alert py-2 px-3 mb-3 d-flex align-items-center gap-2 ' + notification.type">
          <i class="bi bi-info-circle-fill"></i>
          <span>{{ notification.text }}</span>
        </div>

        <div class="row g-3">
          <div class="col-md-4 col-12">
            <atom-label text="Código del Producto" [required]="true"></atom-label>
            <atom-input icon="bi-upc-scan" placeholder="Ej. PRD-001" [model]="codigo" (modelChange)="codigo = $event.toString()"></atom-input>
          </div>
          <div class="col-md-4 col-12">
            <atom-label text="Nombre del Producto" [required]="true"></atom-label>
            <atom-input icon="bi-tag" placeholder="Ej. Café Gourmet 500g" [model]="nombre" (modelChange)="nombre = $event.toString()"></atom-input>
          </div>
          <div class="col-md-4 col-12">
            <atom-label text="NIT Proveedor" [required]="true"></atom-label>
            <atom-input icon="bi-briefcase" placeholder="Ej. 900112233-1" [model]="nitProveedor" (modelChange)="nitProveedor = $event.toString()"></atom-input>
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
              <tr *ngFor="let p of productsList" (click)="selectProduct(p)" role="button">
                <td><span class="badge bg-light text-dark border">{{ p.codigo }}</span></td>
                <td class="fw-semibold">{{ p.nombre }}</td>
                <td class="text-muted small">{{ p.nitProveedor }}</td>
                <td>{{ p.precioCompra | currency:'USD':'symbol':'1.0-0' }}</td>
                <td><span class="badge bg-secondary-subtle text-secondary">{{ p.ivaCompra }}%</span></td>
                <td class="text-success fw-bold">{{ p.precioVenta | currency:'USD':'symbol':'1.0-0' }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-warning rounded-circle"><i class="bi bi-pencil"></i></button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class ProductsOrganism {
  codigo = '';
  nombre = '';
  nitProveedor = '';
  precioCompra = '';
  ivaCompra = '19';
  precioVenta = '';
  notification: { text: string; type: string } | null = null;

  productsList: ProductRecord[] = [
    { codigo: 'PRD-101', nombre: 'Leche Entera 1000ml', nitProveedor: '900112233-1', precioCompra: 3200, ivaCompra: 0, precioVenta: 4200 },
    { codigo: 'PRD-102', nombre: 'Café Especial Quindío 500g', nitProveedor: '900445566-2', precioCompra: 14000, ivaCompra: 19, precioVenta: 19500 },
    { codigo: 'PRD-103', nombre: 'Arroz Premium 1000g', nitProveedor: '900112233-1', precioCompra: 4000, ivaCompra: 5, precioVenta: 5300 }
  ];

  selectProduct(p: ProductRecord): void {
    this.codigo = p.codigo;
    this.nombre = p.nombre;
    this.nitProveedor = p.nitProveedor;
    this.precioCompra = p.precioCompra.toString();
    this.ivaCompra = p.ivaCompra.toString();
    this.precioVenta = p.precioVenta.toString();
    this.setNotification(`Producto ${p.nombre} seleccionado`, 'alert-info');
  }

  consultar(): void {
    const found = this.productsList.find(p => p.codigo === this.codigo);
    if (found) {
      this.selectProduct(found);
      this.setNotification(`Producto encontrado: ${found.nombre}`, 'alert-success');
    } else {
      this.setNotification('Producto no encontrado con ese código', 'alert-warning');
    }
  }

  crear(): void {
    if (!this.codigo || !this.nombre) {
      this.setNotification('Por favor complete código y nombre', 'alert-warning');
      return;
    }
    this.productsList.push({
      codigo: this.codigo,
      nombre: this.nombre,
      nitProveedor: this.nitProveedor,
      precioCompra: Number(this.precioCompra) || 0,
      ivaCompra: Number(this.ivaCompra) || 0,
      precioVenta: Number(this.precioVenta) || 0
    });
    this.setNotification(`Producto ${this.nombre} agregado`, 'alert-success');
    this.resetForm();
  }

  actualizar(): void {
    const idx = this.productsList.findIndex(p => p.codigo === this.codigo);
    if (idx >= 0) {
      this.productsList[idx] = {
        codigo: this.codigo,
        nombre: this.nombre,
        nitProveedor: this.nitProveedor,
        precioCompra: Number(this.precioCompra) || 0,
        ivaCompra: Number(this.ivaCompra) || 0,
        precioVenta: Number(this.precioVenta) || 0
      };
      this.setNotification(`Producto ${this.nombre} actualizado`, 'alert-success');
    } else {
      this.setNotification('Seleccione un producto para actualizar', 'alert-warning');
    }
  }

  borrar(): void {
    const prev = this.productsList.length;
    this.productsList = this.productsList.filter(p => p.codigo !== this.codigo);
    if (this.productsList.length < prev) {
      this.setNotification('Producto eliminado', 'alert-danger');
      this.resetForm();
    } else {
      this.setNotification('Producto no encontrado para borrar', 'alert-warning');
    }
  }

  private resetForm(): void {
    this.codigo = '';
    this.nombre = '';
    this.nitProveedor = '';
    this.precioCompra = '';
    this.ivaCompra = '19';
    this.precioVenta = '';
  }

  private setNotification(text: string, type: string): void {
    this.notification = { text, type };
  }
}
