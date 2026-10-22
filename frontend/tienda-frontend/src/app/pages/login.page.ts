import { Component, ChangeDetectionStrategy } from '@angular/core';
import { LoginOrganism } from '../organisms';

@Component({
  selector: 'page-login',
  standalone: true,
  imports: [LoginOrganism],
  template: `<organism-login></organism-login>`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginPage {}
