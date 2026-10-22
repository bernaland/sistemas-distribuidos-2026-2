import { Component, Input, ChangeDetectionStrategy } from '@angular/core';

@Component({
    selector: 'atom-label',
    template: `
    <label [attr.for]="forId" class="atom-label form-label mb-1 fw-semibold text-secondary">
      <ng-content></ng-content>{{ text }}
      @if (required) {
        <span class="text-danger ms-1">*</span>
      }
    </label>
    `,
    styles: [`
    .atom-label {
      font-size: 0.85rem;
      letter-spacing: -0.01em;
      display: inline-block;
    }
  `],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: true
})
export class LabelAtom {
  @Input() text = '';
  @Input() forId = '';
  @Input() required = false;
}
