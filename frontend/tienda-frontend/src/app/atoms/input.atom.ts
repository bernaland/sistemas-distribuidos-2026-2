import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'atom-input',
  template: `
    <div class="input-wrapper position-relative w-100">
      <i *ngIf="icon" [class]="'bi ' + icon + ' input-icon text-muted'"></i>
      <input
        [id]="id"
        [name]="name"
        [type]="type"
        [placeholder]="placeholder"
        [disabled]="disabled"
        [value]="model || ''"
        (input)="onInputChange($event)"
        class="form-control-tg w-100"
        [class.has-icon]="!!icon" />
    </div>
  `,
  styles: [`
    .input-wrapper {
      display: inline-block;
    }
    .input-icon {
      position: absolute;
      left: 0.85rem;
      top: 50%;
      transform: translateY(-50%);
      pointer-events: none;
      font-size: 0.95rem;
    }
    .has-icon {
      padding-left: 2.4rem !important;
    }
  `]
})
export class InputAtom {
  @Input() type = 'text';
  @Input() placeholder = '';
  @Input() id = '';
  @Input() name = '';
  @Input() icon = '';
  @Input() disabled = false;
  @Input() model: string | number = '';
  @Output() modelChange = new EventEmitter<string | number>();

  onInputChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.model = target.value;
    this.modelChange.emit(this.model);
  }
}
