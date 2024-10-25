import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, of } from 'rxjs';
import { FormGroup, FormControl, Validators } from '@angular/forms';

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
    loading: boolean = true;
    errorMessage: string | null = null;
    isEditing = false;

    userProfile = new FormGroup({
        email: new FormControl(''),
        password: new FormControl(''),
        confirmPassword: new FormControl(''),
        firstName: new FormControl(''),
        middleName: new FormControl(''),
        lastName: new FormControl(''),
        dateOfBirth: new FormControl(''),
        placeOfResidence: new FormGroup({
            city: new FormControl(''),
            postalCode: new FormControl(''),
            address: new FormControl(''),
        }),
        position: new FormControl(''),
        gender: new FormControl(''),
        startOfEmployment: new FormControl(''),
        adminPrivileges: new FormControl(false),
        hrManagementAccess: new FormControl(false)
    });
    
    constructor(private http: HttpClient, private router: Router) {};

    ngOnInit(): void {
        this.fetchUserData().subscribe({
            next: (data) => {
                data.dateOfBirth = this.formatDate(data.dateOfBirth);
                data.startOfEmployment = this.formatDate(data.startOfEmployment);
                this.userProfile.patchValue(data);
                this.userProfile.disable();
                this.loading = false;
            },
            error: (error: HttpErrorResponse) => {
                console.error('Error fetching user data', error);
                this.errorMessage = 'Failed to load user data. Please try again later.';
                this.loading = false;
            }
        });
    }

    private formatDate(dateString: string): string {
        const date = new Date(dateString);
        // Extract the date components without using the local time zone offset
        const year = date.getUTCFullYear();
        const month = (date.getUTCMonth() + 1).toString().padStart(2, '0'); // Months are zero-based
        const day = date.getUTCDate().toString().padStart(2, '0');
        return `${year}-${month}-${day}`; // Return in 'yyyy-MM-dd' format
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
                if ((error.status >= 400) && (error.status < 500)) {
                    this.router.navigate(['/login']);
                }
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
