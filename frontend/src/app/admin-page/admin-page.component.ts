import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, of, throwError } from 'rxjs';

interface UserData {
    __id: any,
    email: string;
    firstName: string;
    middleName: string;
    lastName: string;
    adminPrivileges: boolean;
    hrManagementAccess: boolean;
}

@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrl: './admin-page.component.css'
})
export class AdminPageComponent {
    dataForAdmin: UserData[] = [];
    errorMessage: string | null = null;
    loading: boolean = true;

    constructor(private http: HttpClient, private router: Router) {};

    ngOnInit(): void {
        this.fetchUserData().subscribe({
            next: (data) => {
                console.log('data fetched', data);
                this.dataForAdmin = data;
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
        return this.http.get<UserData>('/api/user/admin/all').pipe(
            catchError((error: HttpErrorResponse) => {
                console.error('Error in fetchUserData:', error);
                this.errorMessage = 'Error occurred while fetching user data.';
                return of([] as any); // Return a fallback observable
            })
        );
    }

    onCheckboxChange(user: UserData): void {
        // Handle checkbox change if needed, or just handle in onSaveChanges
    }
    
    onSaveChanges(user: UserData): void {
        /* this.userService.updateUser(user).subscribe(() => {
            alert('User updated successfully!');
        }); */
    }
    
    onChangePassword(user: UserData): void {
        // Implement password change logic, possibly navigating to a different page or opening a modal
        console.log(`Change password for ${user.email}`);
    }

    onRemoveUser(user: UserData): void {
        console.log('asdasd');
        if (confirm('Are you sure you want to remove this user?')) {
            this.http.delete(`/api/user/admin/${user.__id}`).pipe(
                catchError((error: HttpErrorResponse) => {
                    console.error('Error in fetchUserData:', error);
                    this.errorMessage = 'Error occurred while fetching user data.';
                    return throwError('An unknown error occured');
                })
            ).subscribe({
                next: (data) => {
                    console.log('User removed successfully');
                },
                error: (error: HttpErrorResponse) => {
                    console.error('Error removing user', error);
                }
            });
        }
    }
}
