import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';

// Atoms
import { ButtonAtom } from './atoms/button.atom';
import { InputAtom } from './atoms/input.atom';
import { LabelAtom } from './atoms/label.atom';
import { TopbarAtom } from './atoms/topbar.atom';

// Molecules
import { NavTabsMolecule } from './molecules/nav-tabs.molecule';
import { FormRowMolecule } from './molecules/form-row.molecule';

// Organisms
import { LoginOrganism } from './organisms/login.organism';
import { UsersOrganism } from './organisms/users.organism';
import { ClientsOrganism } from './organisms/clients.organism';
import { ProvidersOrganism } from './organisms/providers.organism';
import { ProductsOrganism } from './organisms/products.organism';
import { SalesOrganism } from './organisms/sales.organism';
import { ReportsOrganism } from './organisms/reports.organism';
import { DashboardOrganism } from './organisms/dashboard.organism';

// Templates
import { MainLayoutTemplate } from './templates/main-layout.template';

// Pages
import { LoginPage } from './pages/login.page';
import { UsersPage } from './pages/users.page';
import { ClientsPage } from './pages/clients.page';
import { ProvidersPage } from './pages/providers.page';
import { ProductsPage } from './pages/products.page';
import { SalesPage } from './pages/sales.page';
import { ReportsPage } from './pages/reports.page';
import { DashboardPage } from './pages/dashboard.page';


@NgModule({
  declarations: [
    AppComponent,
    // atoms
    ButtonAtom,
    InputAtom,
    LabelAtom,
    TopbarAtom,
    // molecules
    NavTabsMolecule,
    FormRowMolecule,
    // organisms
    LoginOrganism,
    UsersOrganism,
    ClientsOrganism,
    ProvidersOrganism,
    ProductsOrganism,
    SalesOrganism,
    ReportsOrganism,
    DashboardOrganism,
    // templates
    MainLayoutTemplate,
    // pages
    LoginPage,
    UsersPage,
    ClientsPage,
    ProvidersPage,
    ProductsPage,
    SalesPage,
    ReportsPage,
    DashboardPage
  ],
  imports: [
    BrowserModule,
    FormsModule,
    RouterModule.forRoot([
      { path: '', component: LoginPage },
      { path: 'dashboard', component: DashboardPage },
      { path: 'users', component: UsersPage },
      { path: 'clients', component: ClientsPage },
      { path: 'providers', component: ProvidersPage },
      { path: 'products', component: ProductsPage },
      { path: 'sales', component: SalesPage },
      { path: 'reports', component: ReportsPage }
    ])
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
