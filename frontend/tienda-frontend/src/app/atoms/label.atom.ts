import { Component, Input } from '@angular/core';

@Component({
  selector: 'atom-label',
  template: `
    <label [attr.for]="forId" class="atom-label form-label mb-1 fw-semibold text-secondary">
      <ng-content></ng-content>{{ text }}
      <span *ngIf="required" class="text-danger ms-1">*</span>
    </label>
  `,
  styles: [`
    .atom-label {
      font-size: 0.85rem;
      letter-spacing: -0.01em;
      display: inline-block;
    }
  `]
})
export class LabelAtom {
  @Input() text = '';
  @Input() forId = '';
  @Input() required = false;
}
