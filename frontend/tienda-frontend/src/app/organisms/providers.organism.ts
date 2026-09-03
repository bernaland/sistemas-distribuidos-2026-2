import { Component } from '@angular/core';

interface ProviderRecord {
  nit: string;
  nombre: string;
  direccion: string;
  telefono: string;
  ciudad: string;
}

@Component({
  selector: 'organism-providers',
  template: `
    <div class="d-flex flex-column gap-4">
      <div class="tg-card p-4">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
          <div class="d-flex align-items-center gap-3">
            <div class="p-2 bg-success-subtle text-success rounded-3">
              <i class="bi bi-truck fs-4"></i>
            </div>
            <div>
              <h2 class="h5 fw-bold mb-0 text-dark">Gestión de Proveedores</h2>
              <span class="text-muted small">Control de suministros y abastecimiento</span>
            </div>
          </div>
          <span class="badge bg-success-subtle text-success border border-success-subtle rounded-pill px-3 py-2">
            <i class="bi bi-building me-1"></i> {{ providersList.length }} Proveedores Activos
          </span>
        </div>

        <div *ngIf="notification" [class]="'alert py-2 px-3 mb-3 d-flex align-items-center gap-2 ' + notification.type">
          <i class="bi bi-info-circle-fill"></i>
          <span>{{ notification.text }}</span>
        </div>

        <div class="row g-3">
          <div class="col-md-6 col-12">
            <atom-label text="NIT" [required]="true"></atom-label>
            <atom-input icon="bi-briefcase" placeholder="Ej. 900123456-1" [model]="nit" (modelChange)="nit = $event.toString()"></atom-input>
          </div>
          <div class="col-md-6 col-12">
            <atom-label text="Nombre de la Empresa" [required]="true"></atom-label>
            <atom-input icon="bi-building" placeholder="Ej. Lácteos Andinos S.A.S" [model]="nombre" (modelChange)="nombre = $event.toString()"></atom-input>
          </div>
          <div class="col-md-6 col-12">
            <atom-label text="Dirección" [required]="true"></atom-label>
            <atom-input icon="bi-geo-alt" placeholder="Ej. Parque Industrial Bodega 12" [model]="direccion" (modelChange)="direccion = $event.toString()"></atom-input>
          </div>
          <div class="col-md-6 col-12">
            <atom-label text="Teléfono" [required]="true"></atom-label>
            <atom-input icon="bi-telephone" placeholder="Ej. 6017890123" [model]="telefono" (modelChange)="telefono = $event.toString()"></atom-input>
          </div>
          <div class="col-12">
            <atom-label text="Ciudad" [required]="true"></atom-label>
            <atom-input icon="bi-pin-map" placeholder="Ej. Bogotá D.C." [model]="ciudad" (modelChange)="ciudad = $event.toString()"></atom-input>
          </div>
        </div>

        <div class="d-flex flex-wrap justify-content-end gap-2 mt-4 pt-3 border-top">
          <atom-button variant="secondary" (onClick)="consultar()">
            <i class="bi bi-search me-1"></i> Consultar
          </atom-button>
          <atom-button variant="primary" (onClick)="crear()">
            <i class="bi bi-building-add me-1"></i> Crear Proveedor
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
          <i class="bi bi-table me-2 text-success"></i>Directorio de Proveedores
        </h3>
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light text-muted small text-uppercase">
              <tr>
                <th>NIT</th>
                <th>Razón Social</th>
                <th>Teléfono</th>
                <th>Dirección</th>
                <th>Ciudad</th>
                <th class="text-end">Acción</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let p of providersList" (click)="selectProvider(p)" role="button">
                <td class="fw-semibold text-dark">{{ p.nit }}</td>
                <td>{{ p.nombre }}</td>
                <td>{{ p.telefono }}</td>
                <td class="text-muted small">{{ p.direccion }}</td>
                <td><span class="badge bg-light text-dark border">{{ p.ciudad }}</span></td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-success rounded-circle"><i class="bi bi-pencil"></i></button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class ProvidersOrganism {
  nit = '';
  nombre = '';
  direccion = '';
  telefono = '';
  ciudad = '';
  notification: { text: string; type: string } | null = null;

  providersList: ProviderRecord[] = [
    { nit: '900112233-1', nombre: 'Alimentos del Campo S.A.', direccion: 'Zona Franca Km 3', telefono: '6013456789', ciudad: 'Bogotá' },
    { nit: '900445566-2', nombre: 'Empaques e Insumos de Colombia', direccion: 'Calle 80 # 70-15', telefono: '6018901234', ciudad: 'Medellín' }
  ];

  selectProvider(p: ProviderRecord): void {
    this.nit = p.nit;
    this.nombre = p.nombre;
    this.direccion = p.direccion;
    this.telefono = p.telefono;
    this.ciudad = p.ciudad;
    this.setNotification(`Proveedor ${p.nombre} seleccionado`, 'alert-info');
  }

  consultar(): void {
    const found = this.providersList.find(p => p.nit === this.nit);
    if (found) {
      this.selectProvider(found);
      this.setNotification(`Proveedor encontrado: ${found.nombre}`, 'alert-success');
    } else {
      this.setNotification('Proveedor no encontrado con ese NIT', 'alert-warning');
    }
  }

  crear(): void {
    if (!this.nit || !this.nombre) {
      this.setNotification('Por favor complete NIT y nombre', 'alert-warning');
      return;
    }
    this.providersList.push({ nit: this.nit, nombre: this.nombre, direccion: this.direccion, telefono: this.telefono, ciudad: this.ciudad });
    this.setNotification(`Proveedor ${this.nombre} creado`, 'alert-success');
    this.resetForm();
  }

  actualizar(): void {
    const index = this.providersList.findIndex(p => p.nit === this.nit);
    if (index >= 0) {
      this.providersList[index] = { nit: this.nit, nombre: this.nombre, direccion: this.direccion, telefono: this.telefono, ciudad: this.ciudad };
      this.setNotification(`Proveedor ${this.nombre} actualizado`, 'alert-success');
    } else {
      this.setNotification('Seleccione un proveedor para actualizar', 'alert-warning');
    }
  }

  borrar(): void {
    const prev = this.providersList.length;
    this.providersList = this.providersList.filter(p => p.nit !== this.nit);
    if (this.providersList.length < prev) {
      this.setNotification('Proveedor eliminado', 'alert-danger');
      this.resetForm();
    } else {
      this.setNotification('Proveedor no encontrado para borrar', 'alert-warning');
    }
  }

  private resetForm(): void {
    this.nit = '';
    this.nombre = '';
    this.direccion = '';
    this.telefono = '';
    this.ciudad = '';
  }

  private setNotification(text: string, type: string): void {
    this.notification = { text, type };
  }
}
