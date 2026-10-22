import { Component, OnInit, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CustomerService } from '../services';
import { Customer } from '../models';
import { ButtonAtom, InputAtom, LabelAtom } from '../atoms';

@Component({
  selector: 'organism-clients',
  standalone: true,
  imports: [ButtonAtom, InputAtom, LabelAtom],
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
            <i class="bi bi-people me-1"></i> {{ clientsList().length }} Clientes Registrados
          </span>
        </div>

        @if (notification(); as notif) {
          <div [class]="'alert py-2 px-3 mb-3 d-flex align-items-center gap-2 ' + notif.type">
            <i class="bi bi-info-circle-fill"></i>
            <span>{{ notif.text }}</span>
          </div>
        }

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
              @for (c of clientsList(); track c.cedula) {
                <tr (click)="selectClient(c)" role="button">
                  <td class="fw-semibold text-dark">{{ c.cedula }}</td>
                  <td>{{ c.name }}</td>
                  <td><i class="bi bi-telephone text-muted me-1"></i>{{ c.phone }}</td>
                  <td><span class="text-muted small">{{ c.address }}</span></td>
                  <td><span class="badge bg-light text-secondary border">{{ c.email }}</span></td>
                  <td class="text-end">
                    <button class="btn btn-sm btn-outline-info rounded-circle"><i class="bi bi-pencil"></i></button>
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
export class ClientsOrganism implements OnInit {
  private readonly customerService = inject(CustomerService);

  cedula = '';
  nombre = '';
  direccion = '';
  telefono = '';
  correo = '';
  readonly notification = signal<{ text: string; type: string } | null>(null);
  readonly clientsList = signal<Customer[]>([]);

  ngOnInit(): void {
    this.cargarClientes();
  }

  cargarClientes(): void {
    this.customerService.list().subscribe({
      next: (data) => this.clientsList.set(data),
      error: () => this.setNotification('Error al cargar clientes del servidor', 'alert-danger')
    });
  }

  selectClient(c: Customer): void {
    this.cedula = c.cedula;
    this.nombre = c.name;
    this.direccion = c.address;
    this.telefono = c.phone;
    this.correo = c.email;
    this.setNotification(`Cliente ${c.name} seleccionado`, 'alert-info');
  }

  consultar(): void {
    if (!this.cedula.trim()) {
      this.setNotification('Ingrese una cédula para consultar', 'alert-warning');
      return;
    }
    this.customerService.getByCedula(this.cedula.trim()).subscribe({
      next: (c) => {
        this.selectClient(c);
        this.setNotification(`Cliente encontrado: ${c.name}`, 'alert-success');
      },
      error: () => this.setNotification('Cliente no encontrado con esa cédula', 'alert-warning')
    });
  }

  crear(): void {
    if (!this.cedula || !this.nombre) {
      this.setNotification('Por favor complete cédula y nombre', 'alert-warning');
      return;
    }
    const payload = this.buildPayload();
    this.customerService.create(payload).subscribe({
      next: (created) => {
        this.setNotification(`Cliente ${created.name} registrado`, 'alert-success');
        this.resetForm();
        this.cargarClientes();
      },
      error: (err) => this.handleError(err, 'Error al registrar cliente')
    });
  }

  actualizar(): void {
    if (!this.cedula) {
      this.setNotification('Seleccione o ingrese un cliente para actualizar', 'alert-warning');
      return;
    }
    const payload = this.buildPayload();
    this.customerService.update(this.cedula, payload).subscribe({
      next: (updated) => {
        this.setNotification(`Cliente ${updated.name} actualizado`, 'alert-success');
        this.cargarClientes();
      },
      error: (err) => this.handleError(err, 'Error al actualizar cliente')
    });
  }

  borrar(): void {
    if (!this.cedula) {
      this.setNotification('Seleccione un cliente para borrar', 'alert-warning');
      return;
    }
    this.customerService.delete(this.cedula).subscribe({
      next: () => {
        this.setNotification('Cliente eliminado exitosamente', 'alert-danger');
        this.resetForm();
        this.cargarClientes();
      },
      error: (err) => this.handleError(err, 'Error al eliminar cliente')
    });
  }

  private buildPayload(): Customer {
    return {
      cedula: this.cedula,
      name: this.nombre,
      address: this.direccion,
      phone: this.telefono,
      email: this.correo
    };
  }

  private handleError(err: { error?: { message?: string } }, fallback: string): void {
    this.setNotification(err?.error?.message || fallback, 'alert-danger');
  }

  private resetForm(): void {
    this.cedula = '';
    this.nombre = '';
    this.direccion = '';
    this.telefono = '';
    this.correo = '';
  }

  private setNotification(text: string, type: string): void {
    this.notification.set({ text, type });
  }
}
