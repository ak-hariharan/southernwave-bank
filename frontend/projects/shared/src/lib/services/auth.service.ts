import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

import { LoginRequest, AuthResponse, TOKEN_KEYS, ForgetPasswordRequest, ResetPasswordRequest, ApiResponse, TokenRefreshRequest, RegisterRequest } from '../models/auth.models';

/** Base URL for the API gateway — update this to match your gateway port */
const API_BASE = 'http://localhost:8765';

// ============================================================================
// AuthService — Handles login, token storage and token utilities
// Used by all MFEs that need authentication
// ============================================================================
@Injectable({ providedIn: 'root' })
export class AuthService {

    constructor(private http: HttpClient) { }

    // --------------------------------------------------------------------------
    // HTTP Calls
    // --------------------------------------------------------------------------

    /**
     * Authenticate a user (officer or consumer) against the auth-service.
     * On success automatically stores tokens in localStorage.
     */
    login(req: LoginRequest): Observable<AuthResponse> {
        return this.http
            .post<AuthResponse>(`${API_BASE}/swb/auth/login`, req)
            .pipe(tap(res => this.saveTokens(res.accessToken, res.refreshToken)));
    }

    /** Trigger forgot password flow and get OTP via email */
    forgetPassword(req: ForgetPasswordRequest): Observable<ApiResponse> {
        return this.http.post<ApiResponse>(`${API_BASE}/swb/auth/forget-password`, req);
    }

    /** Reset password using the received OTP */
    resetPassword(emailId: string, req: ResetPasswordRequest): Observable<ApiResponse> {
        return this.http.post<ApiResponse>(`${API_BASE}/swb/auth/reset-password/${emailId}`, req);
    }

    /** Refresh Access Token */
    refreshToken(req: TokenRefreshRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${API_BASE}/swb/auth/refresh`, req).pipe(
            tap(res => this.saveTokens(res.accessToken, res.refreshToken))
        );
    }

    /** Register a generic user (Officer/Consumer) via User Service */
    registerUser(req: RegisterRequest): Observable<ApiResponse> {
        return this.http.post<ApiResponse>(`${API_BASE}/swb/users/register`, req);
    }

    // --------------------------------------------------------------------------
    // Token Utilities
    // --------------------------------------------------------------------------

    /** Persist access and refresh tokens to localStorage */
    saveTokens(accessToken: string, refreshToken: string): void {
        localStorage.setItem(TOKEN_KEYS.ACCESS, accessToken);
        localStorage.setItem(TOKEN_KEYS.REFRESH, refreshToken);
    }

    /** Retrieve the stored access token */
    getAccessToken(): string | null {
        return localStorage.getItem(TOKEN_KEYS.ACCESS);
    }

    /** Retrieve the stored refresh token */
    getRefreshToken(): string | null {
        return localStorage.getItem(TOKEN_KEYS.REFRESH);
    }

    /** Remove both tokens (used on logout) */
    clearTokens(): void {
        localStorage.removeItem(TOKEN_KEYS.ACCESS);
        localStorage.removeItem(TOKEN_KEYS.REFRESH);
    }

    /** Returns true if a valid access token exists in storage */
    isLoggedIn(): boolean {
        return !!this.getAccessToken();
    }

    /**
     * Decode the JWT payload and return the role claim.
     * Returns null if no token found or token is malformed.
     */
    getUserRole(): string | null {
        const token = this.getAccessToken();
        if (!token) return null;
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload?.role ?? payload?.authorities?.[0] ?? null;
        } catch {
            return null;
        }
    }
}
