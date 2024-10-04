import { Component } from '@angular/core';
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

    constructor(
        private http: HttpClient,
        public dialog: MatDialog) {};

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
        return this.http.get<UserData>('/api/user/management/admin').pipe(
            catchError((error: HttpErrorResponse) => {
                console.error('Error occurred while fetching user data.:', error);
                this.errorMessage = 'Error occurred while fetching user data.';
                return of([] as any);
            })
        );
    }
    
    onSaveChanges(user: UserData): void {
        this.http.put(`/api/user/management/${user.__id}`, { adminPrivileges: user.adminPrivileges, hrManagementAccess: user.hrManagementAccess}).pipe(
            catchError((error: HttpErrorResponse) => {
                console.error('Error occurred while updating user:', error);
                this.errorMessage = 'Error occurred while updating user.';
                return throwError('Error occurred while updating user.');
            })
        ).subscribe({
            next: (data) => {
                console.log('User updated successfully');
            },
            error: (error: HttpErrorResponse) => {
                console.error('Error updating user', error);
            }
        });
    }
    
    onChangePassword(user: UserData): void {
        const dialogRef = this.dialog.open(ChangePasswordDialogComponent, {
            width: '400px',
            height: '300px',
            data: { email: user.email }
        });

        dialogRef.afterClosed().subscribe(newPassword => {
            if (!newPassword) {
                return;
            }

            this.http.put(`/api/user/management/${user.__id}`, { password: newPassword}).pipe(
                catchError((error: HttpErrorResponse) => {
                    console.error('Error occurred while updating user:', error);
                    this.errorMessage = 'Error occurred while updating user.';
                    return throwError('Error occurred while updating user.');
                })
            ).subscribe({
                next: (data) => {
                    console.log('User updated successfully');
                },
                error: (error: HttpErrorResponse) => {
                    console.error('Error updating user', error);
                }
            });
        });
    }

    onRemoveUser(user: UserData): void {
        if (confirm('Are you sure you want to remove this user?')) {
            this.http.delete(`/api/user/management/${user.__id}`).pipe(
                catchError((error: HttpErrorResponse) => {
                    console.error('Error occurred while removing user:', error);
                    this.errorMessage = 'Error occurred while removing user.';
                    return throwError('Error occurred while removing user.');
                })
            ).subscribe({
                next: (data) => {
                    console.log('User removed successfully');
                    this.dataForAdmin = this.dataForAdmin.filter(u => u.__id !== user.__id);
                },
                error: (error: HttpErrorResponse) => {
                    console.error('Error removing user', error);
                }
            });
        }
    }
}
