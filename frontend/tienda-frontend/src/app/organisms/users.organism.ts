import { Component } from '@angular/core';

interface UserRecord {
  cedula: string;
  nombre: string;
  correo: string;
  usuario: string;
  rol: string;
  activo: boolean;
}

@Component({
  selector: 'organism-users',
  template: `
    <div class="d-flex flex-column gap-4">
      <!-- Form Card -->
      <div class="tg-card p-4">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
          <div class="d-flex align-items-center gap-3">
            <div class="p-2 bg-primary-subtle text-primary rounded-3">
              <i class="bi bi-person-gear fs-4"></i>
            </div>
            <div>
              <h2 class="h5 fw-bold mb-0 text-dark">Gestión de Usuarios</h2>
              <span class="text-muted small">Módulo de administración y control de acceso</span>
            </div>
          </div>
          <span class="badge bg-primary-subtle text-primary border border-primary-subtle rounded-pill px-3 py-2">
            <i class="bi bi-people-fill me-1"></i> {{ usersList.length }} Registrados
          </span>
        </div>

        <div *ngIf="notification" [class]="'alert py-2 px-3 mb-3 d-flex align-items-center gap-2 ' + notification.type">
          <i class="bi bi-info-circle-fill"></i>
          <span>{{ notification.text }}</span>
        </div>

        <div class="row g-3">
          <div class="col-md-6 col-12">
            <atom-label text="Cédula" [required]="true"></atom-label>
            <atom-input icon="bi-card-text" placeholder="Ej. 1018293847" [model]="cedula" (modelChange)="cedula = $event.toString()"></atom-input>
          </div>
          <div class="col-md-6 col-12">
            <atom-label text="Usuario" [required]="true"></atom-label>
            <atom-input icon="bi-person" placeholder="Ej. jdoe" [model]="usuario" (modelChange)="usuario = $event.toString()"></atom-input>
          </div>
          <div class="col-md-6 col-12">
            <atom-label text="Nombre Completo" [required]="true"></atom-label>
            <atom-input icon="bi-file-person" placeholder="Ej. Juan Pérez Gómez" [model]="nombre" (modelChange)="nombre = $event.toString()"></atom-input>
          </div>
          <div class="col-md-6 col-12">
            <atom-label text="Contraseña"></atom-label>
            <atom-input type="password" icon="bi-key" placeholder="••••••••" [model]="contrasena" (modelChange)="contrasena = $event.toString()"></atom-input>
          </div>
          <div class="col-12">
            <atom-label text="Correo Electrónico" [required]="true"></atom-label>
            <atom-input type="email" icon="bi-envelope" placeholder="usuario@tiendagenerica.com" [model]="correo" (modelChange)="correo = $event.toString()"></atom-input>
          </div>
        </div>

        <div class="d-flex flex-wrap justify-content-end gap-2 mt-4 pt-3 border-top">
          <atom-button variant="secondary" (onClick)="consultar()">
            <i class="bi bi-search me-1"></i> Consultar
          </atom-button>
          <atom-button variant="primary" (onClick)="crear()">
            <i class="bi bi-person-plus me-1"></i> Crear Usuario
          </atom-button>
          <atom-button variant="secondary" (onClick)="actualizar()">
            <i class="bi bi-arrow-repeat me-1"></i> Actualizar
          </atom-button>
          <atom-button variant="danger" (onClick)="borrar()">
            <i class="bi bi-trash3 me-1"></i> Borrar
          </atom-button>
        </div>
      </div>

      <!-- Users Data Table Card -->
      <div class="tg-card p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h3 class="h6 fw-bold mb-0 text-secondary">
            <i class="bi bi-table me-2 text-primary"></i>Listado Activo de Usuarios
          </h3>
          <span class="small text-muted">Seleccione una fila para cargar sus datos</span>
        </div>

        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light text-muted small text-uppercase">
              <tr>
                <th>Cédula</th>
                <th>Nombre</th>
                <th>Usuario</th>
                <th>Correo Electrónico</th>
                <th>Rol</th>
                <th>Estado</th>
                <th class="text-end">Acción</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let u of usersList" (click)="selectUser(u)" role="button" class="user-table-row">
                <td class="fw-semibold text-dark">{{ u.cedula }}</td>
                <td>{{ u.nombre }}</td>
                <td><span class="badge bg-light text-dark border">{{ u.usuario }}</span></td>
                <td class="text-muted small">{{ u.correo }}</td>
                <td><span class="badge bg-primary-subtle text-primary">{{ u.rol }}</span></td>
                <td>
                  <span class="badge bg-success-subtle text-success border border-success-subtle">
                    <span class="status-dot-pulse me-1"></span> Activo
                  </span>
                </td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-primary rounded-circle" title="Cargar">
                    <i class="bi bi-pencil-square"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .user-table-row {
      transition: background-color 0.2s ease;
    }
    .user-table-row:hover {
      background-color: rgba(79, 70, 229, 0.04) !important;
    }
  `]
})
export class UsersOrganism {
  cedula = '';
  nombre = '';
  correo = '';
  usuario = '';
  contrasena = '';
  notification: { text: string; type: string } | null = null;

  usersList: UserRecord[] = [
    { cedula: '1001234567', nombre: 'Samuel Admin', correo: 'admin@tiendagenerica.com', usuario: 'admin', rol: 'Administrador', activo: true },
    { cedula: '1009876543', nombre: 'Valeria Gómez', correo: 'valeria.gomez@tiendagenerica.com', usuario: 'vgomez', rol: 'Ventas', activo: true },
    { cedula: '1014567890', nombre: 'Carlos Rodríguez', correo: 'carlos.r@tiendagenerica.com', usuario: 'crodriguez', rol: 'Almacén', activo: true }
  ];

  selectUser(user: UserRecord): void {
    this.cedula = user.cedula;
    this.nombre = user.nombre;
    this.correo = user.correo;
    this.usuario = user.usuario;
    this.contrasena = '••••••••';
    this.setNotification(`Usuario ${user.usuario} seleccionado`, 'alert-info');
  }

  consultar(): void {
    const found = this.usersList.find(u => u.cedula === this.cedula || u.usuario === this.usuario);
    if (found) {
      this.selectUser(found);
      this.setNotification(`Usuario ${found.nombre} encontrado exitosamente`, 'alert-success');
    } else {
      this.setNotification('No se encontró ningún usuario con ese criterio', 'alert-warning');
    }
  }

  crear(): void {
    if (!this.cedula || !this.usuario || !this.nombre) {
      this.setNotification('Por favor complete cédula, usuario y nombre', 'alert-warning');
      return;
    }
    this.usersList.push({
      cedula: this.cedula,
      nombre: this.nombre,
      correo: this.correo || `${this.usuario}@tiendagenerica.com`,
      usuario: this.usuario,
      rol: 'Operador',
      activo: true
    });
    this.setNotification(`Usuario ${this.usuario} registrado correctamente`, 'alert-success');
    this.resetForm();
  }

  actualizar(): void {
    const index = this.usersList.findIndex(u => u.cedula === this.cedula || u.usuario === this.usuario);
    if (index >= 0) {
      this.usersList[index] = { ...this.usersList[index], nombre: this.nombre, correo: this.correo };
      this.setNotification(`Usuario ${this.usuario} actualizado`, 'alert-success');
    } else {
      this.setNotification('Seleccione un usuario existente para actualizar', 'alert-warning');
    }
  }

  borrar(): void {
    const prevLen = this.usersList.length;
    this.usersList = this.usersList.filter(u => u.cedula !== this.cedula && u.usuario !== this.usuario);
    if (this.usersList.length < prevLen) {
      this.setNotification('Usuario eliminado exitosamente', 'alert-danger');
      this.resetForm();
    } else {
      this.setNotification('Usuario no encontrado para borrar', 'alert-warning');
    }
  }

  private resetForm(): void {
    this.cedula = '';
    this.nombre = '';
    this.correo = '';
    this.usuario = '';
    this.contrasena = '';
  }

  private setNotification(text: string, type: string): void {
    this.notification = { text, type };
  }
}
