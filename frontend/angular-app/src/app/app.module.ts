import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: '<h1>Tienda Genérica - Frontend</h1>'
})
class AppComponent {}

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule],
  bootstrap: [AppComponent]
})
export class AppModule {}
