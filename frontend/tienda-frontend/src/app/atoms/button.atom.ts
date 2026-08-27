import { Component, Input } from '@angular/core';

@Component({
  selector: 'atom-button',
  template: `<button class="button" [class.secondary]="secondary"><ng-content></ng-content></button>`,
  styles: [`.button{background:#8a8a8a;color:white;padding:10px 20px;border-radius:6px;border:none;margin-right:10px;cursor:pointer}.button.secondary{background:#bdbdbd;color:#333}`]
})
export class ButtonAtom {
  @Input() secondary = false;
}
