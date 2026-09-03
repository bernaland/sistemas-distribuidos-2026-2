import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';

// Atoms
import { ButtonAtom, InputAtom, LabelAtom, TopbarAtom } from './atoms';

// Molecules
import { NavTabsMolecule, FormRowMolecule } from './molecules';

// Organisms
import {
  LoginOrganism,
  UsersOrganism,
  ClientsOrganism,
  ProvidersOrganism,
  ProductsOrganism,
  SalesOrganism,
  ReportsOrganism,
  DashboardOrganism
} from './organisms';

// Templates
import { MainLayoutTemplate } from './templates';

// Pages
import {
  LoginPage,
  UsersPage,
  ClientsPage,
  ProvidersPage,
  ProductsPage,
  SalesPage,
  ReportsPage,
  DashboardPage
} from './pages';


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
