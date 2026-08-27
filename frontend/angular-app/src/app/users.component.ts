import { Component } from '@angular/core';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent {
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
