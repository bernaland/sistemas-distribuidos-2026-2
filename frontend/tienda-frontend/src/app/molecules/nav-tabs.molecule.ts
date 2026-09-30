import { Component } from '@angular/core';

interface NavItem {
  readonly path: string;
  readonly label: string;
  readonly icon: string;
}

@Component({
  selector: 'molecule-nav-tabs',
  template: `
    <nav class="nav-container p-2 px-3 d-flex align-items-center gap-2 overflow-x-auto">
      <a
        *ngFor="let item of navItems"
        [routerLink]="item.path"
        routerLinkActive="active"
        class="nav-tab-pill d-inline-flex align-items-center gap-2 text-decoration-none">
        <i [class]="'bi ' + item.icon"></i>
        <span>{{ item.label }}</span>
      </a>
    </nav>
  `,
  styles: [`
    .nav-container {
      background: rgba(255, 255, 255, 0.82);
      backdrop-filter: blur(12px);
      border-bottom: 1px solid rgba(226, 232, 240, 0.9);
      white-space: nowrap;
      scrollbar-width: thin;
    }
    .nav-tab-pill {
      color: #64748b;
      font-weight: 500;
      font-size: 0.885rem;
      padding: 0.45rem 0.9rem;
      border-radius: 9999px;
      transition: all 0.22s cubic-bezier(0.16, 1, 0.3, 1);
      border: 1px solid transparent;
    }
    .nav-tab-pill:hover {
      color: #334155;
      background: rgba(241, 245, 249, 0.9);
      transform: translateY(-1px);
    }
    .nav-tab-pill.active {
      color: #ffffff;
      background: linear-gradient(135deg, #4f46e5, #6366f1);
      box-shadow: 0 4px 14px rgba(79, 70, 229, 0.32);
      font-weight: 600;
      border-color: rgba(99, 102, 241, 0.4);
    }
  `]
})
export class NavTabsMolecule {
  readonly navItems: readonly NavItem[] = [
    { path: '/dashboard', label: 'Dashboard', icon: 'bi-grid-1x2' },
    { path: '/users', label: 'Usuarios', icon: 'bi-people' },
    { path: '/clients', label: 'Clientes', icon: 'bi-person-badge' },
    { path: '/providers', label: 'Proveedores', icon: 'bi-truck' },
    { path: '/products', label: 'Productos', icon: 'bi-box-seam' },
    { path: '/sales', label: 'Ventas', icon: 'bi-cart-check' },
    { path: '/reports', label: 'Reportes', icon: 'bi-bar-chart-line' }
  ];
}
