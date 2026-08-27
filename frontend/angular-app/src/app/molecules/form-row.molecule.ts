import { Component, Input } from '@angular/core';

@Component({
  selector: 'molecule-form-row',
  template: `<div class="form-row"><label class="form-label">{{label}}</label><ng-content></ng-content></div>`,
  styles: [`.form-row{display:flex;gap:24px;margin-bottom:12px;align-items:center}.form-label{width:140px;color:#777}`]
})
export class FormRowMolecule { @Input() label = ''; }
