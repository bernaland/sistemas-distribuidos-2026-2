import { Component, Input } from '@angular/core';

@Component({
  selector: 'atom-label',
  template: `<label class="atom-label">{{text}}</label>`,
  styles: [`.atom-label{width:140px;color:#777;display:inline-block}`]
})
export class LabelAtom { @Input() text = ''; }
