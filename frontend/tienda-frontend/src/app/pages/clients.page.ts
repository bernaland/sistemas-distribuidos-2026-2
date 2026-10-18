import { Component, ChangeDetectionStrategy } from '@angular/core';
import { ClientsOrganism } from '../organisms';

@Component({
  selector: 'page-clients',
  standalone: true,
  imports: [ClientsOrganism],
  template: `<organism-clients></organism-clients>`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ClientsPage {}
