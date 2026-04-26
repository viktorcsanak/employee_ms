import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export const adminGuard: CanActivateFn = (route, state): Observable<boolean> => {
    const http = inject(HttpClient);
    const router = inject(Router);

    const url = `/api/auth/verify-admin`;
    return http.get(url, { withCredentials: true }).pipe(
        map((response: any) => {
            if (response.isAuthenticated) {
                return true;
            } else {
                router.navigate(['/admin']);
                return false;
            }
        }),
        catchError(() => {
            router.navigate(['/home']);
            return of(false);
        })
    );
};
