import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, of, throwError } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { animate, state, style, transition, trigger } from '@angular/animations';

interface UserData {
    __id: any,
    email: string;
    firstName: string;
    middleName: string;
    lastName: string;
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

@Component({
    selector: 'app-hr-page',
    templateUrl: './hr-page.component.html',
    styleUrl: './hr-page.component.css',
    animations: [
        trigger('detailExpand', [
            state('collapsed,void', style({height: '0px', minHeight: '0'})),
            state('expanded', style({height: '*'})),
            transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
        ]),
    ],
})

export class HrPageComponent {
    dataSource: MatTableDataSource<UserData> = new MatTableDataSource<UserData>();    errorMessage: string | null = null;
    loading: boolean = true;
    columnsToDisplay: string[] = ['email', 'fullName', 'dateOfBirth'];
    columnsToDisplayWithExpand = [...this.columnsToDisplay, 'expand'];
    expandedElement: UserData | null = null;

    constructor(
        private http: HttpClient,
        private router: Router) {};

    ngOnInit(): void {
        this.fetchUserData().subscribe({
            next: (data) => {
                console.log('data fetched', data);
                this.dataSource = new MatTableDataSource(data);
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
