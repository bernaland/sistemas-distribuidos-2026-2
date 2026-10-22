import { Component, ChangeDetectionStrategy } from '@angular/core';
import { ProvidersOrganism } from '../organisms';

@Component({
  selector: 'page-providers',
  standalone: true,
  imports: [ProvidersOrganism],
  template: `<organism-providers></organism-providers>`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProvidersPage {}
