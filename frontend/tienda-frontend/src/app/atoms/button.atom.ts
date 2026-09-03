import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'atom-button',
  template: `
    <button
      [type]="type"
      [disabled]="disabled"
      [class]="getButtonClass()"
      (click)="onClick.emit($event)">
      <i *ngIf="icon" [class]="'bi ' + icon + ' me-1'"></i>
      <ng-content></ng-content>
    </button>
  `,
  styles: [`
    button {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 0.4rem;
      cursor: pointer;
      font-family: inherit;
    }
    button:active {
      transform: scale(0.98) translateY(0) !important;
    }
  `]
})
export class ButtonAtom {
  @Input() variant: 'primary' | 'secondary' | 'danger' | 'outline' = 'primary';
  @Input() secondary = false;
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() disabled = false;
  @Input() icon = '';
  @Output() onClick = new EventEmitter<MouseEvent>();

  getButtonClass(): string {
    if (this.secondary || this.variant === 'secondary') {
      return 'btn-secondary-tg';
    }
    if (this.variant === 'danger') {
      return 'btn-danger-tg';
    }
    if (this.variant === 'outline') {
      return 'btn btn-outline-secondary border-secondary-subtle';
    }
    return 'btn-primary-tg';
  }
}
