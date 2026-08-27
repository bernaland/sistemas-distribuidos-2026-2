import { Component } from '@angular/core';

@Component({
  selector: 'molecule-nav-tabs',
  template: `
    <nav class="tabs">
      <a routerLink="/users" routerLinkActive="active">Usuarios</a>
      <a routerLink="/clients" routerLinkActive="active">Clientes</a>
      <a routerLink="/providers" routerLinkActive="active">Proveedores</a>
      <a routerLink="/products" routerLinkActive="active">Productos</a>
      <a routerLink="/sales" routerLinkActive="active">Ventas</a>
      <a routerLink="/reports" routerLinkActive="active">Reportes</a>
    </nav>
  `,
  styles: [`.tabs{background:#fff;padding:6px 12px;border-bottom:1px solid #ddd}.tabs a{margin-right:12px;color:#777;text-decoration:none;padding:6px 8px;border-radius:4px}.tabs a.active{background:#efefef;color:#000;border:1px solid #ccc}`]
})
export class NavTabsMolecule {}
