import { Component } from '@angular/core';

@Component({
  selector: 'app-main-layout',
  template: `
    <atom-topbar></atom-topbar>
    <div class="header">Tienda Genérica</div>
    <molecule-nav-tabs></molecule-nav-tabs>
    <section class="content">
      <router-outlet></router-outlet>
    </section>
  `,
  styles: [`.header{background:#bdbdbd;padding:10px 12px;font-weight:600}.content{padding:24px}`]
})
export class MainLayoutTemplate {}
