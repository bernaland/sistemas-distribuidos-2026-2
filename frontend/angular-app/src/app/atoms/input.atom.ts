import { Component, Input } from '@angular/core';

@Component({
  selector: 'atom-input',
  template: `<input class="input" [type]="type" [(ngModel)]="model">`,
  styles: [`.input{padding:6px;border:1px solid #ccc;border-radius:4px;width:220px}`]
})
export class InputAtom {
  @Input() type = 'text';
  @Input() model: any;
}
