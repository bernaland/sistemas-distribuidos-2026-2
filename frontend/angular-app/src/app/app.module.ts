import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { LoginComponent } from './login.component';
import { DashboardComponent } from './dashboard.component';
import { UsersComponent } from './users.component';
import { ClientsComponent } from './clients.component';
import { ProvidersComponent } from './providers.component';
import { ProductsComponent } from './products.component';
import { SalesComponent } from './sales.component';
import { ReportsComponent } from './reports.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    UsersComponent,
    ClientsComponent,
    ProvidersComponent,
    ProductsComponent,
    SalesComponent,
    ReportsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    RouterModule.forRoot([
      { path: '', component: LoginComponent },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'users', component: UsersComponent },
      { path: 'clients', component: ClientsComponent },
      { path: 'providers', component: ProvidersComponent },
      { path: 'products', component: ProductsComponent },
      { path: 'sales', component: SalesComponent },
      { path: 'reports', component: ReportsComponent }
    ])
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
