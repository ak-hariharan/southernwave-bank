import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-open-account-modal',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './open-account-modal.component.html',
    styleUrls: ['./open-account-modal.component.scss']
})
export class OpenAccountModalComponent {
    @Input() isVisible = false;
    @Input() translation: any;
    @Output() close = new EventEmitter<void>();

    onClose(): void {
        this.close.emit();
    }
}
