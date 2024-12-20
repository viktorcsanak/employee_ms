import { Component, ViewChild } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, of, throwError } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { ChangePasswordDialogComponent } from '../change-password-dialog/change-password-dialog.component';
import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';

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
    displayedColumns: string[] = ['select', 'email', 'fullName', 'admin', 'hr', 'actions'];
    dataForAdmin = new MatTableDataSource<UserData>([]);
    adminUser: UserData | null = null;
    selection = new SelectionModel<UserData>(true, []);
    errorMessage: string | null = null;
    searchTerm: string = '';

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    constructor(
        private http: HttpClient,
        public dialog: MatDialog,
        private router: Router) {};

    ngOnInit(): void {
        this.fetchUserData().subscribe({
            next: (data) => {
                console.log('data fetched', data);
                this.dataForAdmin = new MatTableDataSource(data);
                setTimeout (() => {
                    this.dataForAdmin.paginator = this.paginator;
                    this.dataForAdmin.sort = this.sort;
                });
            },
            error: (error: HttpErrorResponse) => {
                console.error('Error fetching user data', error);
                this.errorMessage = 'Failed to load user data. Please try again later.';
            }
        }); 
        this.fetchCurrentUserData().subscribe({
            next: (data) => {
                console.log('data fetched', data);
                this.adminUser = data;
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
        return this.http.get<UserData>('/api/user/management/admin').pipe(
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

    isAllSelected() {
        const numSelected = this.selection.selected.length;
        const numRows = this.dataForAdmin.data.length;
        return numSelected === numRows;
    }
    
    isIndeterminate() {
        const numSelected = this.selection.selected.length;
        const numRows = this.dataForAdmin.data.length;
        return numSelected > 0 && numSelected < numRows;
    }

    masterToggle() {
        this.isAllSelected()
            ? this.selection.clear()
            : this.dataForAdmin.data.forEach(row => this.selection.select(row));
    }
    
    toggleSelection(row: UserData) {
        this.selection.toggle(row);
    }

    onDeleteSelected() {
        const selectedUsers = this.selection.selected;
        if (!confirm('Are you sure you want to remove the selected users?')) {
            return;
        }
        console.log('Deleting users:', selectedUsers);
        selectedUsers.forEach(element => {
            this.removeUser(element);
        });
        this.selection.clear();
    }

    onToggleAdmin() {
        this.selection.selected.forEach(user => {
            user.adminPrivileges = !user.adminPrivileges;
            this.saveChanges(user);
        });
    }
    
    onToggleHR() {
        this.selection.selected.forEach(user => {
            user.hrManagementAccess = !user.hrManagementAccess;
            this.saveChanges(user);
        });
    }
    
    saveChanges(user: UserData): void {
        this.http.put(`/api/user/management/${user.__id}`, { adminPrivileges: user.adminPrivileges, hrManagementAccess: user.hrManagementAccess}).pipe().subscribe({
            next: (data) => {
                console.log('User updated successfully');
            },
            error: (error: HttpErrorResponse) => {
                console.error('Error updating user', error);
                if ((error.status >= 400) && (error.status < 500)) {
                    this.router.navigate(['/home']);
                }
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

            this.http.put(`/api/user/management/${user.__id}`, { password: newPassword}).pipe().subscribe({
                next: (data) => {
                    console.log('User updated successfully');
                },
                error: (error: HttpErrorResponse) => {
                    console.error('Error updating user', error);
                    if ((error.status >= 400) && (error.status < 500)) {
                        this.router.navigate(['/home']);
                    }
                }
            });
        });
    }

    removeUser(user: UserData): void {
        this.http.delete(`/api/user/management/${user.__id}`).pipe().subscribe({
            next: (data) => {
                console.log('User removed successfully');
                this.dataForAdmin.data = this.dataForAdmin.data.filter(u => u.__id !== user.__id);
            },
            error: (error: HttpErrorResponse) => {
                console.error('Error removing user', error);
                if ((error.status >= 400) && (error.status < 500)) {
                    this.router.navigate(['/home']);
                }
            }
        });
    }

    applyFilter(): void {
        const filterValue = this.searchTerm.trim().toLowerCase();

        this.dataForAdmin.filterPredicate = (data : UserData) => {
            const isAdminFilter = filterValue.includes("is:admin");
            const isHRFilter = filterValue.includes("is:hr");
            const matchesSearch = data.email.toLowerCase().includes(filterValue.replace('is:admin', '').replace('is:hr', '').trim()) ||
                                (data.firstName + ' ' + data.middleName + ' ' + data.lastName).toLowerCase().includes(filterValue.replace('is:admin', '').replace('is:hr', '').trim());
            
            const matchesAdmin = isAdminFilter ? data.adminPrivileges : true;
            const matchesHR = isHRFilter ? data.hrManagementAccess : true;
    
            return matchesSearch && matchesAdmin && matchesHR;
        };

        this.dataForAdmin.filter = filterValue;
    }

    goToHome(): void {
        this.router.navigate(['/home']);
    }

    goToHR(): void {
        this.router.navigate(['/hr']);
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
