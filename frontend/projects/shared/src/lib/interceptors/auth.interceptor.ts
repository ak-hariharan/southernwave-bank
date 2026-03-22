import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { throwError, BehaviorSubject, Observable } from 'rxjs';
import { catchError, filter, take, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

let isRefreshing = false;
let refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> => {
    const authService = inject(AuthService);
    const accessToken = authService.getAccessToken();

    // Bypass interceptor for login, refresh, register, and password-free endpoints
    if (req.url.includes('/auth/login') || req.url.includes('/auth/refresh') || req.url.includes('/auth/register') || req.url.includes('/auth/forget-password') || req.url.includes('/auth/reset-password')) {
        return next(req);
    }

    let authReq = req;
    if (accessToken) {
        authReq = addTokenHeader(req, accessToken);
    }

    return next(authReq).pipe(
        catchError((error) => {
            if (error instanceof HttpErrorResponse && error.status === 401) {
                return handle401Error(authReq, next, authService);
            }
            return throwError(() => error);
        })
    );
};

function addTokenHeader(request: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
    return request.clone({ headers: request.headers.set('Authorization', 'Bearer ' + token) });
}

function handle401Error(request: HttpRequest<unknown>, next: HttpHandlerFn, authService: AuthService): Observable<HttpEvent<unknown>> {
    if (!isRefreshing) {
        isRefreshing = true;
        refreshTokenSubject.next(null);

        const refreshToken = authService.getRefreshToken();
        if (refreshToken) {
            return authService.refreshToken({ refreshToken }).pipe(
                switchMap((res: any) => {
                    isRefreshing = false;
                    refreshTokenSubject.next(res.accessToken);
                    return next(addTokenHeader(request, res.accessToken));
                }),
                catchError((err) => {
                    isRefreshing = false;
                    authService.clearTokens();
                    window.location.hash = '';
                    return throwError(() => err);
                })
            );
        } else {
            isRefreshing = false;
            authService.clearTokens();
            return throwError(() => new Error('Refresh token not available'));
        }
    } else {
        return refreshTokenSubject.pipe(
            filter(token => token !== null),
            take(1),
            switchMap((token) => next(addTokenHeader(request, token as string)))
        );
    }
}
