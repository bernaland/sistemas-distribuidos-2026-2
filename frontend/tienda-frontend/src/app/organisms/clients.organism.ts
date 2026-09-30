import { Component } from '@angular/core';

interface ClientRecord {
  cedula: string;
  nombre: string;
  direccion: string;
  telefono: string;
  correo: string;
}

@Component({
  selector: 'organism-clients',
  template: `
    <div class="d-flex flex-column gap-4">
      <div class="tg-card p-4">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
          <div class="d-flex align-items-center gap-3">
            <div class="p-2 bg-info-subtle text-info rounded-3">
              <i class="bi bi-person-vcard fs-4"></i>
            </div>
            <div>
              <h2 class="h5 fw-bold mb-0 text-dark">Gestión de Clientes</h2>
              <span class="text-muted small">Registro y administración de clientes comerciales</span>
            </div>
          </div>
          <span class="badge bg-info-subtle text-info border border-info-subtle rounded-pill px-3 py-2">
            <i class="bi bi-people me-1"></i> {{ clientsList.length }} Clientes Registrados
          </span>
        </div>

        <div *ngIf="notification" [class]="'alert py-2 px-3 mb-3 d-flex align-items-center gap-2 ' + notification.type">
          <i class="bi bi-info-circle-fill"></i>
          <span>{{ notification.text }}</span>
        </div>

        <div class="row g-3">
          <div class="col-md-6 col-12">
            <atom-label text="Cédula" [required]="true"></atom-label>
            <atom-input icon="bi-card-text" placeholder="Ej. 1029384756" [model]="cedula" (modelChange)="cedula = $event.toString()"></atom-input>
          </div>
          <div class="col-md-6 col-12">
            <atom-label text="Nombre Completo" [required]="true"></atom-label>
            <atom-input icon="bi-person" placeholder="Ej. María Fernanda López" [model]="nombre" (modelChange)="nombre = $event.toString()"></atom-input>
          </div>
          <div class="col-md-6 col-12">
            <atom-label text="Dirección" [required]="true"></atom-label>
            <atom-input icon="bi-geo-alt" placeholder="Ej. Calle 100 # 15-20" [model]="direccion" (modelChange)="direccion = $event.toString()"></atom-input>
          </div>
          <div class="col-md-6 col-12">
            <atom-label text="Teléfono" [required]="true"></atom-label>
            <atom-input icon="bi-telephone" placeholder="Ej. 3101234567" [model]="telefono" (modelChange)="telefono = $event.toString()"></atom-input>
          </div>
          <div class="col-12">
            <atom-label text="Correo Electrónico" [required]="true"></atom-label>
            <atom-input type="email" icon="bi-envelope" placeholder="cliente@correo.com" [model]="correo" (modelChange)="correo = $event.toString()"></atom-input>
          </div>
        </div>

        <div class="d-flex flex-wrap justify-content-end gap-2 mt-4 pt-3 border-top">
          <atom-button variant="secondary" (onClick)="consultar()">
            <i class="bi bi-search me-1"></i> Consultar
          </atom-button>
          <atom-button variant="primary" (onClick)="crear()">
            <i class="bi bi-person-plus me-1"></i> Crear Cliente
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
          <i class="bi bi-table me-2 text-info"></i>Directorio de Clientes
        </h3>
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light text-muted small text-uppercase">
              <tr>
                <th>Cédula</th>
                <th>Nombre</th>
                <th>Teléfono</th>
                <th>Dirección</th>
                <th>Correo</th>
                <th class="text-end">Acción</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let c of clientsList" (click)="selectClient(c)" role="button">
                <td class="fw-semibold text-dark">{{ c.cedula }}</td>
                <td>{{ c.nombre }}</td>
                <td><i class="bi bi-telephone text-muted me-1"></i>{{ c.telefono }}</td>
                <td><span class="text-muted small">{{ c.direccion }}</span></td>
                <td><span class="badge bg-light text-secondary border">{{ c.correo }}</span></td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-info rounded-circle"><i class="bi bi-pencil"></i></button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class ClientsOrganism {
  cedula = '';
  nombre = '';
  direccion = '';
  telefono = '';
  correo = '';
  notification: { text: string; type: string } | null = null;

  clientsList: ClientRecord[] = [
    { cedula: '1020304050', nombre: 'Distribuidora del Norte', direccion: 'Cra 45 # 128-04', telefono: '3128901234', correo: 'contacto@distnorte.com' },
    { cedula: '1098765432', nombre: 'Supermercados La Estrella', direccion: 'Av. El Dorado # 68-90', telefono: '3004567890', correo: 'compras@laestrella.com' }
  ];

  selectClient(c: ClientRecord): void {
    this.cedula = c.cedula;
    this.nombre = c.nombre;
    this.direccion = c.direccion;
    this.telefono = c.telefono;
    this.correo = c.correo;
    this.setNotification(`Cliente ${c.nombre} seleccionado`, 'alert-info');
  }

  consultar(): void {
    const found = this.clientsList.find(c => c.cedula === this.cedula);
    if (found) {
      this.selectClient(found);
      this.setNotification(`Cliente encontrado: ${found.nombre}`, 'alert-success');
    } else {
      this.setNotification('Cliente no encontrado con esa cédula', 'alert-warning');
    }
  }

  crear(): void {
    if (!this.cedula || !this.nombre) {
      this.setNotification('Por favor complete cédula y nombre', 'alert-warning');
      return;
    }
    this.clientsList.push({ cedula: this.cedula, nombre: this.nombre, direccion: this.direccion, telefono: this.telefono, correo: this.correo });
    this.setNotification(`Cliente ${this.nombre} registrado`, 'alert-success');
    this.resetForm();
  }

  actualizar(): void {
    const index = this.clientsList.findIndex(c => c.cedula === this.cedula);
    if (index >= 0) {
      this.clientsList[index] = { cedula: this.cedula, nombre: this.nombre, direccion: this.direccion, telefono: this.telefono, correo: this.correo };
      this.setNotification(`Cliente ${this.nombre} actualizado`, 'alert-success');
    } else {
      this.setNotification('Seleccione un cliente para actualizar', 'alert-warning');
    }
  }

  borrar(): void {
    const prev = this.clientsList.length;
    this.clientsList = this.clientsList.filter(c => c.cedula !== this.cedula);
    if (this.clientsList.length < prev) {
      this.setNotification('Cliente eliminado', 'alert-danger');
      this.resetForm();
    } else {
      this.setNotification('Cliente no encontrado para borrar', 'alert-warning');
    }
  }

  private resetForm(): void {
    this.cedula = '';
    this.nombre = '';
    this.direccion = '';
    this.telefono = '';
    this.correo = '';
  }

  private setNotification(text: string, type: string): void {
    this.notification = { text, type };
  }
}
