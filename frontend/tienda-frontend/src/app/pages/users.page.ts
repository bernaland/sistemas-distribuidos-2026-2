import { Component, ChangeDetectionStrategy } from '@angular/core';
import { UsersOrganism } from '../organisms';

@Component({
  selector: 'page-users',
  standalone: true,
  imports: [UsersOrganism],
  template: `<organism-users></organism-users>`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsersPage {}
