import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, RegisterRequest } from 'shared';

@Component({
    selector: 'app-register-officer',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './register-officer.component.html',
    styleUrls: ['./register-officer.component.scss']
})
export class RegisterOfficerComponent implements OnInit {

    registerForm!: FormGroup;
    isLoading = false;
    errorMessage = '';
    successMessage = '';

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.registerForm = this.fb.group({
            firstName: ['', [Validators.required]],
            lastName: ['', [Validators.required]],
            emailId: ['', [Validators.required, Validators.email]],
            contactNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]]
        });
    }

    onSubmit(): void {
        if (this.registerForm.invalid) {
            this.registerForm.markAllAsTouched();
            return;
        }

        this.isLoading = true;
        this.errorMessage = '';
        this.successMessage = '';

        const formVals = this.registerForm.value;
        const fullName = `${formVals.firstName} ${formVals.lastName}`.trim();

        const req: RegisterRequest = {
            name: fullName,
            emailId: formVals.emailId.trim(),
            password: '', // Passed as empty string since backend handles dummy generation internally
            role: 'OFFICER',
            contactNumber: formVals.contactNumber
        };

        this.authService.registerUser(req).subscribe({
            next: (res) => {
                this.isLoading = false;
                this.successMessage = 'Officer registered successfully! You may now login.';
                this.registerForm.reset();
            },
            error: (err) => {
                this.isLoading = false;
                this.errorMessage = err.error?.message || 'Failed to register officer. Please try again.';
            }
        });
    }

    goToHome(): void {
        this.router.navigate(['/']);
    }
}
