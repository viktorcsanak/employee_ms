import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, of, throwError } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { ChangePasswordDialogComponent } from '../change-password-dialog/change-password-dialog.component';

interface UserData {
    __id: any,
    email: string;
    firstName: string;
    middleName: string;
    lastName: string;
    additionalData: {
        dateOfBirth: Date,
        placeOfResidence: {
            city: string,
            address: string,
            postalCode: string,
        },
        position: string,
        startOfEmployment: string,
        gender: string,
    }
}

@Component({
    selector: 'app-hr-page',
    templateUrl: './hr-page.component.html',
    styleUrl: './hr-page.component.css'
})
export class HrPageComponent {
    data: UserData[] = [];
    errorMessage: string | null = null;
    loading: boolean = true;
    displayedColumns: string[] = ['email', 'fullName', 'expandedDetail'];

    constructor(
        private http: HttpClient,
        private router: Router,
        public dialog: MatDialog) {};

    ngOnInit(): void {
        this.fetchUserData().subscribe({
            next: (data) => {
                console.log('data fetched', data);
                this.data = data;
                this.loading = false;
            },
            error: (error: HttpErrorResponse) => {
                console.error('Error fetching user data', error);
                this.errorMessage = 'Failed to load user data. Please try again later.';
                this.loading = false;
            }
        });
    }
    
    fetchUserData(): Observable<UserData[]> {
        return this.http.get<UserData>('/api/user/management/hr').pipe(
            catchError((error: HttpErrorResponse) => {
                console.error('Error occurred while fetching user data.:', error);
                this.errorMessage = 'Error occurred while fetching user data.';
                return of([] as any);
            })
        );
    }
}
