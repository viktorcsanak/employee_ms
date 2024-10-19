import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, catchError, of, throwError } from 'rxjs';
import { FormGroup, FormControl, Validators } from '@angular/forms';

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
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
    constructor(private http: HttpClient, private router: Router) {}

    adminUser: UserData | null = null;
    registerData = this.getDefaultRegisterData();
    emailTakenError: string | null = null;
    successMessage: string | null = null;

    registerForm = new FormGroup({
        email: new FormControl('', { validators: [Validators.required, Validators.email] }),
        password: new FormControl('', { validators: [Validators.required, Validators.minLength(6)] }),
        confirmPassword: new FormControl('', { validators: [Validators.required, Validators.minLength(6)] }),
        firstName: new FormControl('', { validators: [Validators.required] }),
        middleName: new FormControl(''),
        lastName: new FormControl('', { validators: [Validators.required] }),
        dateOfBirth: new FormControl('', { validators: [Validators.required] }),
        placeOfResidence: new FormGroup({
            city: new FormControl('', { validators: [Validators.required] }),
            postalCode: new FormControl('', { validators: [Validators.required] }),
            address: new FormControl('', { validators: [Validators.required] }),
        }),
        position: new FormControl('', { validators: [Validators.required] }),
        gender: new FormControl('', { validators: [Validators.required] }),
        startOfEmployment: new FormControl(''),
        adminPrivileges: new FormControl(false),
        hrManagementAccess: new FormControl(false)
    });

    ngOnInit() {
        this.fetchCurrentUserData().subscribe({
            next: (data) => {
                console.log('data fetched', data);
                this.adminUser = data;
                setTimeout (() => {
                });
            },
            error: (error: HttpErrorResponse) => {
                console.error('Error fetching user data', error);
                this.emailTakenError = 'Failed to load user data. Please try again later.';
            }
        });
    }

    onSubmit() {
        if (!this.registerForm.valid) {
            console.log("Register data is invalid", this.registerForm.value);
            return;
        }

        if (this.registerForm.value.password !== this.registerForm.value.confirmPassword) {
            console.log("Confirm password and password do not match!");
            return;
        }

        console.log(this.registerForm.value);
        console.log(this.registerData);
        
        this.http.post('/api/auth/register', this.registerForm.value).pipe(
            catchError((error: HttpErrorResponse) => {
                this.successMessage = null;
                if (error.status == 400) {
                    return throwError('Employee with email is already registered!');
                }
                return throwError('An unknown error occured');
            })
        ).subscribe({
            next: (response) => {
                this.successMessage = 'Registration successful!';
                this.emailTakenError = null;
                this.registerForm.reset();
                this.registerData = this.getDefaultRegisterData();
                console.log('Register repsonse:', response);
            }, error: (errorMessage) => {
                console.error('Register Error', errorMessage);
                this.successMessage = null;
                this.emailTakenError = errorMessage;
            }
        });   
    }

    fetchCurrentUserData(): Observable<UserData> {
        return this.http.get<UserData>('/api/user/').pipe(
            catchError((error: HttpErrorResponse) => {
                console.error('Error in fetchUserData:', error);
                this.emailTakenError = 'Error occurred while fetching user data.';
                return of(null as any); // Return a fallback observable
            })
        );
    }

    getDefaultRegisterData() {
        return {
            email: '',
            password: '',
            confirmPassword: '',
            firstName: '',
            middleName: '',
            lastName: '',
            dateOfBirth: '',
            placeOfResidence: {
                city: '',
                address: '',
                postalCode: ''
            },
            position: '',
            startOfEmployment: '',
            gender: '',
            adminPrivileges: false,
            hrManagementAccess: false,
        };
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

    goToHome(): void {
        this.router.navigate(['/home']);
    }
}
