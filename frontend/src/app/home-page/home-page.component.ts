import { Component } from '@angular/core';
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
    
    constructor(private http: HttpClient) {};

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
        return this.http.get<UserData>('/api/fetch/user-data').pipe(
            catchError((error: HttpErrorResponse) => {
                console.error('Error in fetchUserData:', error);
                this.errorMessage = 'Error occurred while fetching user data.';
                return of(null as any); // Return a fallback observable
            })
        );
    }
}
