import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export const alreadyLoggedInGuard: CanActivateFn = (route, state): Observable<boolean> => {
    const http = inject(HttpClient);
    const router = inject(Router);

    const url = `/api/auth/verify-token`;

    return http.get(url, { withCredentials: true }).pipe(
        map((response: any) => {
            if (response.isAuthenticated) {
                router.navigate(['/home'])
                return false;
            }
            return true;
        }),
        catchError(() => {
            return of(true);
        })
    );
};
