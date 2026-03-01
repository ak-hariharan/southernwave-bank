// ============================================================================
// Auth Models - Mirroring SouthernWave Bank auth-service DTOs
// ============================================================================

/** Sent to POST /swb/auth/login */
export interface LoginRequest {
    emailId: string;
    password: string;
}

/** Returned by POST /swb/auth/login and POST /swb/auth/refresh */
export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
}

/** Sent to POST /swb/auth/forget-password */
export interface ForgetPasswordRequest {
    name: string;
    emailId: string;
}

/** Sent to POST /swb/auth/reset-password/{email} */
export interface ResetPasswordRequest {
    otp: string;
    newPassword: string;
}

/** Generic wrapper matching backend Response<T> shape */
export interface ApiResponse<T = unknown> {
    message: string;
    data?: T;
}

/** Token keys used in localStorage */
export const TOKEN_KEYS = {
    ACCESS: 'swb_access_token',
    REFRESH: 'swb_refresh_token',
} as const;
