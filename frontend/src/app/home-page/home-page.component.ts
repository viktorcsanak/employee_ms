import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, of } from 'rxjs';

interface UserData {
    email: string;
    firstName: string;
    middleName: string;
    lastName: string;
    dateOfBirth: string;
    gender: string;
    placeOfResidence: {
        city: string;
        postalCode: string;
        address: string;
    };
    position: string;
    startOfEmployment: string;
    adminPrivileges: boolean;
    hrManagementAccess: boolean;
}

@Component({
    selector: 'app-home-page',
    templateUrl: './home-page.component.html',
    styleUrl: './home-page.component.css'
})
export class HomePageComponent {
    userData: UserData | null = null;
    loading: boolean = true;
    errorMessage: string | null = null;
    
    constructor(private http: HttpClient, private router: Router) {};

    ngOnInit(): void {
        this.fetchUserData().subscribe({
            next: (data) => {
                this.userData = data;
                this.loading = false;
            },
            error: (error: HttpErrorResponse) => {
                console.error('Error fetching user data', error);
                this.errorMessage = 'Failed to load user data. Please try again later.';
                this.loading = false;
            }
        });
    }

    fetchUserData(): Observable<UserData> {
        return this.http.get<UserData>('/api/user/').pipe(
            catchError((error: HttpErrorResponse) => {
                console.error('Error in fetchUserData:', error);
                this.errorMessage = 'Error occurred while fetching user data.';
                return of(null as any); // Return a fallback observable
            })
        );
    }

    logout(): void {
        this.http.post('/api/auth/logout', {}).subscribe(
            (response: any) => {
                console.log(response.msg); // Log out successful
                this.router.navigate(['/login']);
            },
            (error: any) => {
                console.error('Logout failed', error);
            }
        );
    }

    goToAdmin(): void {
        this.router.navigate(['/admin']);
    }

    goToHR(): void {
        this.router.navigate(['/hr']);
    }
}
