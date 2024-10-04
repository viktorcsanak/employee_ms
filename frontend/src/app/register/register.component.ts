import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
    constructor(private http: HttpClient, private router: Router) {}

    registerData = this.getDefaultRegisterData();
    emailTakenError: string | null = null;
    successMessage: string | null = null;

    onSubmit(registerForm: NgForm) {
        if (!registerForm.valid) {
            console.log("Register data is invalid", registerForm.value);
            return;
        }

        if (this.registerData.password !== this.registerData.confirmPassword) {
            console.log("Confirm password and password do not match!");
            return;
        }

        console.log(registerForm.value);
        console.log(this.registerData);
        
        this.http.post('/api/auth/register', this.registerData).pipe(
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
                registerForm.resetForm();
                this.registerData = this.getDefaultRegisterData();
                console.log('Register repsonse:', response);
            }, error: (errorMessage) => {
                console.error('Register Error', errorMessage);
                this.successMessage = null;
                this.emailTakenError = errorMessage;
            }
        });
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
}
