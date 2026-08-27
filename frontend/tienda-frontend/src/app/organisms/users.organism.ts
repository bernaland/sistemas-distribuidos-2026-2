import { Component } from '@angular/core';

@Component({
  selector: 'organism-users',
  template: `
<div>
  <h3>Usuarios</h3>
  <div class="form-row">
    <label>Cédula</label>
    <input class="input" [(ngModel)]="cedula" />
    <label>Usuario</label>
    <input class="input" [(ngModel)]="usuario" />
  </div>
  <div class="form-row">
    <label>Nombre Completo</label>
    <input class="input" [(ngModel)]="nombre" />
    <label>Contraseña</label>
    <input class="input" type="password" [(ngModel)]="contrasena" />
  </div>
  <div class="form-row">
    <label>Correo Electrónico</label>
    <input class="input" [(ngModel)]="correo" />
  </div>

  <div class="center">
    <button class="button" (click)="consultar()">Consultar</button>
    <button class="button" (click)="crear()">Crear</button>
    <button class="button" (click)="actualizar()">Actualizar</button>
    <button class="button" (click)="borrar()">Borrar</button>
  </div>
</div>
  `,
  styles: [`.form-row{display:flex;gap:24px;margin-bottom:12px;align-items:center}.form-row label{width:160px;color:#777}.input{padding:6px;border:1px solid #ccc;border-radius:4px;width:220px}.center{display:flex;justify-content:center;margin-top:18px}.button{background:#8a8a8a;color:white;padding:10px 20px;border-radius:6px;border:none;margin-right:10px;cursor:pointer}`]
})
export class UsersOrganism {
  cedula = '';
  nombre = '';
  correo = '';
  usuario = '';
  contrasena = '';

  consultar() { console.log('consultar', this.cedula); }
  crear() { console.log('crear', this.usuario); }
  actualizar() { console.log('actualizar', this.usuario); }
  borrar() { console.log('borrar', this.usuario); }
}
