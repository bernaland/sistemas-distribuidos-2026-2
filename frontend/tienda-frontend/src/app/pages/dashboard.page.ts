import { Component, ChangeDetectionStrategy } from '@angular/core';
import { DashboardOrganism } from '../organisms';

@Component({
  selector: 'page-dashboard',
  standalone: true,
  imports: [DashboardOrganism],
  template: `<organism-dashboard></organism-dashboard>`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardPage {}
