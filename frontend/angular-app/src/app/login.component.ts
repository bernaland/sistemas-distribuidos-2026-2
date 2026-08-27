import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  constructor(private router: Router) {}
  accept() {
    // placeholder: in real app validate credentials
    this.router.navigate(['/users']);
  }
  cancel() {
    this.username = '';
    this.password = '';
  }
}
