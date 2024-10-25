import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export const alreadyLoggedInGuard: CanActivateFn = (route, state): Observable<boolean> => {
    const http = inject(HttpClient);
    const router = inject(Router);

    return http.get('/api/auth/verify-token').pipe(
        map((response: any) => {
            if (response.isAuthenticated) {
                router.navigate(['/home'])
                return false;
            } else {
                return true;
            }
        }),
        catchError(() => {
            return of(true);
        })
    );
};
