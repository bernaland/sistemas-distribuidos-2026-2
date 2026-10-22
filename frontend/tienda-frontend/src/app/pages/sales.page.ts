import { Component, ChangeDetectionStrategy } from '@angular/core';
import { SalesOrganism } from '../organisms';

@Component({
  selector: 'page-sales',
  standalone: true,
  imports: [SalesOrganism],
  template: `<organism-sales></organism-sales>`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SalesPage {}
