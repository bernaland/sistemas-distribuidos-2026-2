import { Component, ChangeDetectionStrategy } from '@angular/core';
import { ReportsOrganism } from '../organisms';

@Component({
  selector: 'page-reports',
  standalone: true,
  imports: [ReportsOrganism],
  template: `<organism-reports></organism-reports>`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReportsPage {}
