import { Component, ChangeDetectionStrategy } from '@angular/core';
import { MainLayoutTemplate } from './templates';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [MainLayoutTemplate],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {}
