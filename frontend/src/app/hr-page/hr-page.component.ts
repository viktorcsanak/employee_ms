import { Component, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, of, throwError } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
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
    adminPrivileges: boolean;
    hrManagementAccess: boolean;
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
    hrUser: UserData | null = null;
    searchTerm: string = '';

    @ViewChild(MatPaginator) paginator!: MatPaginator;

    constructor(
        private http: HttpClient,
        private router: Router) {};

    ngOnInit(): void {
        this.fetchUserData().subscribe({
            next: (data) => {
                console.log('data fetched', data);
                this.dataSource = new MatTableDataSource(data);
                this.loading = false;

                setTimeout (() => {
                    this.dataSource.paginator = this.paginator;
                });
            },
            error: (error: HttpErrorResponse) => {
                console.error('Error fetching user data', error);
                this.errorMessage = 'Failed to load user data. Please try again later.';
                this.loading = false;
            }
        });
        this.fetchCurrentUserData().subscribe({
            next: (data) => {
                console.log('data fetched', data);
                this.hrUser = data;
                setTimeout (() => {
                });
            },
            error: (error: HttpErrorResponse) => {
                console.error('Error fetching user data', error);
                this.errorMessage = 'Failed to load user data. Please try again later.';
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

    fetchCurrentUserData(): Observable<UserData> {
        return this.http.get<UserData>('/api/user/').pipe(
            catchError((error: HttpErrorResponse) => {
                console.error('Error in fetchUserData:', error);
                this.errorMessage = 'Error occurred while fetching user data.';
                return of(null as any); // Return a fallback observable
            })
        );
    }

    applyFilter(): void {
        const filterValue = this.searchTerm.trim().toLowerCase();

        this.dataSource.filterPredicate = (data : UserData) => {
            const matchesSearch = data.email.toLowerCase().includes(filterValue) ||
                                (data.firstName + ' ' + data.middleName + ' ' + data.lastName).toLowerCase().includes(filterValue);
            
            return matchesSearch;
        };

        this.dataSource.filter = filterValue;
    }

    goToHome(): void {
        this.router.navigate(['/home']);
    }

    goToAdmin(): void {
        this.router.navigate(['/admin']);
    }

    logout(): void {
        this.http.post('/api/auth/logout', {}).subscribe(
            (response: any) => {
                console.log(response.msg); // Log out successful
                this.router.navigate(['/home']);
            },
            (error: any) => {
                if ((error.status >= 400) && (error.status < 500)) {
                    this.router.navigate(['/home']);
                }
                console.error('Logout failed', error);
            }
        );
    }
}
