import { Component, Input, ChangeDetectionStrategy } from '@angular/core';

@Component({
    selector: 'molecule-form-row',
    template: `
    <div class="row align-items-center mb-3 g-2">
      <div [class]="labelColClass">
        <label class="form-label mb-0 fw-semibold text-secondary small">
          {{ label }}
          @if (required) {
            <span class="text-danger">*</span>
          }
        </label>
      </div>
      <div [class]="controlColClass">
        <ng-content></ng-content>
        @if (hint) {
          <div class="form-text small text-muted mt-1">{{ hint }}</div>
        }
      </div>
    </div>
    `,
    styles: [`
    :host {
      display: block;
      width: 100%;
    }
  `],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: true
})
export class FormRowMolecule {
  @Input() label = '';
  @Input() required = false;
  @Input() hint = '';
  @Input() labelColClass = 'col-sm-3 col-12';
  @Input() controlColClass = 'col-sm-9 col-12';
}
