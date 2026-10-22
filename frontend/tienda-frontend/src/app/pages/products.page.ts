import { Component, ChangeDetectionStrategy } from '@angular/core';
import { ProductsOrganism } from '../organisms';

@Component({
  selector: 'page-products',
  standalone: true,
  imports: [ProductsOrganism],
  template: `<organism-products></organism-products>`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductsPage {}
