import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'organism-login',
  template: `
    <div class="login-container">
      <h2 class="login-title">Bienvenidos a la Tienda Genérica</h2>
      <div class="form-row">
        <label>Usuario</label>
        <input class="input" [(ngModel)]="username" />
      </div>
      <div class="form-row">
        <label>Contraseña</label>
        <input class="input" type="password" [(ngModel)]="password" />
      </div>
      <div class="center">
        <button class="button" (click)="accept()">Aceptar</button>
        <button class="button secondary" (click)="cancel()">Cancelar</button>
      </div>
    </div>
  `,
  styles: [`.login-container{background:#fff;padding:24px;border-radius:6px;border:1px solid #eee}.login-title{text-align:center;color:#999;margin-bottom:24px}.input{padding:6px;border:1px solid #ccc;border-radius:4px;width:300px}.center{display:flex;justify-content:center;margin-top:18px}.button{background:#8a8a8a;color:white;padding:10px 20px;border-radius:6px;border:none;margin-right:10px;cursor:pointer}.button.secondary{background:#bdbdbd;color:#333}`]
})
export class LoginOrganism {
  username = '';
  password = '';
  constructor(private router: Router) {}
  accept() { this.router.navigate(['/users']); }
  cancel() { this.username=''; this.password=''; }
}
