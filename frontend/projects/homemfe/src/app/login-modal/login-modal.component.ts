import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService, LoginRequest, ForgetPasswordRequest, ResetPasswordRequest } from 'shared';

type ModalView = 'login' | 'forgot-pass-step1' | 'forgot-pass-step2';

@Component({
    selector: 'app-login-modal',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './login-modal.component.html',
    styleUrls: ['./login-modal.component.scss']
})
export class LoginModalComponent implements OnInit, OnDestroy, OnChanges {

    @Input() isVisible = false;
    @Input() translation: any;
    @Input() initialView: ModalView = 'login';
    @Output() close = new EventEmitter<void>();

    loginForm!: FormGroup;
    forgotPassForm!: FormGroup;
    resetPassForm!: FormGroup;

    isLoading = false;
    errorMessage = '';
    showPassword = false;
    currentView: ModalView = 'login';
    pendingEmail = '';

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

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['initialView'] && changes['initialView'].currentValue) {
            this.currentView = changes['initialView'].currentValue;
        }
        if (changes['isVisible'] && !changes['isVisible'].currentValue) {
            // Reset view when closing
            this.currentView = 'login'; 
        }
    }

    private buildForms(): void {
        this.loginForm = this.fb.group({
            emailId: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });

        this.forgotPassForm = this.fb.group({
            name: ['', [Validators.required]],
            emailId: ['', [Validators.required, Validators.email]]
        });

        this.resetPassForm = this.fb.group({
            otp: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]],
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
                    alert('Login successful! Welcome, Officer. (Redirecting to Officer Dashboard...)');
                } else if (role === 'CONSUMER') {
                    alert('Login successful! Welcome, Customer. (Redirecting to Consumer Dashboard...)');
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
    // Forgot Password Flow
    // --------------------------------------------------------------------------

    goToForgotPass(): void {
        this.currentView = 'forgot-pass-step1';
        this.errorMessage = '';
    }

    goToLogin(): void {
        this.currentView = 'login';
        this.errorMessage = '';
    }

    onForgotPassSubmit(): void {
        if (this.forgotPassForm.invalid) {
            this.forgotPassForm.markAllAsTouched();
            return;
        }

        this.isLoading = true;
        this.errorMessage = '';

        const req: ForgetPasswordRequest = {
            name: this.forgotPassForm.value.name.trim(),
            emailId: this.forgotPassForm.value.emailId.trim()
        };

        this.authService.forgetPassword(req).subscribe({
            next: () => {
                this.isLoading = false;
                this.pendingEmail = req.emailId;
                this.currentView = 'forgot-pass-step2';
            },
            error: (err) => {
                this.isLoading = false;
                this.errorMessage = err.error?.message || 'Failed to send OTP. Please check your details.';
            }
        });
    }

    onResetPassSubmit(): void {
        if (this.resetPassForm.invalid) {
            this.resetPassForm.markAllAsTouched();
            return;
        }

        this.isLoading = true;
        this.errorMessage = '';

        const req: ResetPasswordRequest = {
            otp: Number(this.resetPassForm.value.otp),
            password: this.resetPassForm.value.password
        };

        this.authService.resetPassword(this.pendingEmail, req).subscribe({
            next: () => {
                this.isLoading = false;
                alert('Password reset successfully! You can now log in.');
                this.goToLogin(); // Return to login view
            },
            error: (err) => {
                this.isLoading = false;
                this.errorMessage = err.error?.message || 'Invalid OTP. Please try again.';
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
        this.forgotPassForm.reset();
        this.resetPassForm.reset();
        this.currentView = this.initialView;
        this.pendingEmail = '';
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
    }

    // --------------------------------------------------------------------------
    // Form Convenience Getters
    // --------------------------------------------------------------------------

    get emailCtrl() { return this.loginForm.get('emailId')!; }
    get passwordCtrl() { return this.loginForm.get('password')!; }

    get fpNameCtrl() { return this.forgotPassForm.get('name')!; }
    get fpEmailCtrl() { return this.forgotPassForm.get('emailId')!; }

    get rpOtpCtrl() { return this.resetPassForm.get('otp')!; }
    get rpPasswordCtrl() { return this.resetPassForm.get('password')!; }
}
