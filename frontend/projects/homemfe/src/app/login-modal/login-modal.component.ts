import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService, LoginRequest } from 'shared';

type ModalView = 'login';

@Component({
    selector: 'app-login-modal',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './login-modal.component.html',
    styleUrls: ['./login-modal.component.scss']
})
export class LoginModalComponent implements OnInit, OnDestroy {

    @Input() isVisible = false;
    @Output() close = new EventEmitter<void>();

    loginForm!: FormGroup;
    isLoading = false;
    errorMessage = '';
    showPassword = false;
    currentView: ModalView = 'login';

    constructor(
        private fb: FormBuilder,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.buildForms();
        document.addEventListener('keydown', this.onEscKey);
    }

    ngOnDestroy(): void {
        document.removeEventListener('keydown', this.onEscKey);
    }

    private buildForms(): void {
        this.loginForm = this.fb.group({
            emailId: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });
    }

    // --------------------------------------------------------------------------
    // Login
    // --------------------------------------------------------------------------

    onLoginSubmit(): void {
        if (this.loginForm.invalid) {
            this.loginForm.markAllAsTouched();
            return;
        }

        this.isLoading = true;
        this.errorMessage = '';

        const req: LoginRequest = {
            emailId: this.loginForm.value.emailId.trim(),
            password: this.loginForm.value.password
        };

        this.authService.login(req).subscribe({
            next: () => {
                this.isLoading = false;
                this.closeModal();

                const role = this.authService.getUserRole();
                if (role === 'OFFICER') {
                    // Temporarily using alert or simple window navigation until 
                    // the Officer Dashboard MFE is linked up or routes are defined.
                    alert('Login successful! Welcome, Officer. (Redirecting to Officer Dashboard...)');
                    // e.g. window.location.href = '/officer-dashboard';
                } else if (role === 'CONSUMER') {
                    alert('Login successful! Welcome, Customer. (Redirecting to Consumer Dashboard...)');
                    // e.g. window.location.href = '/consumer-dashboard';
                } else {
                    alert('Login successful, but role could not be determined.');
                }
            },
            error: (err) => {
                this.isLoading = false;
                if (err.status === 404 || err.status === 401) {
                    this.errorMessage = 'Invalid email or password. Please try again.';
                } else if (err.status === 0) {
                    this.errorMessage = 'Unable to connect to the server. Please try later.';
                } else {
                    this.errorMessage = 'Something went wrong. Please try again.';
                }
            }
        });
    }

    // --------------------------------------------------------------------------
    // UI Helpers
    // --------------------------------------------------------------------------

    togglePasswordVisibility(): void {
        this.showPassword = !this.showPassword;
    }

    closeModal(): void {
        this.errorMessage = '';
        this.loginForm.reset();
        this.currentView = 'login';
        this.close.emit();
    }

    onOverlayClick(event: MouseEvent): void {
        if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
            this.closeModal();
        }
    }

    private onEscKey = (event: KeyboardEvent): void => {
        if (event.key === 'Escape' && this.isVisible) {
            this.closeModal();
        }
    };

    // --------------------------------------------------------------------------
    // Form Convenience Getters
    // --------------------------------------------------------------------------

    get emailCtrl() { return this.loginForm.get('emailId')!; }
    get passwordCtrl() { return this.loginForm.get('password')!; }
}
