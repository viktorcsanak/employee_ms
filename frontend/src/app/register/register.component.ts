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

    registerData = {
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
    emailTakenError: string | null = null;

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
        
        this.http.post('/api/auth/register', this.registerData).pipe(
            catchError((error: HttpErrorResponse) => {
                if (error.status == 400) {
                    return throwError('Employee with email is already registered!');
                }
                return throwError('An unknown error occured');
            })
        ).subscribe({
            next: (response) => {
                console.log('Register repsonse:', response);
                this.router.navigate(['/login']);
            }, error: (errorMessage) => {
                console.error('Register Error', errorMessage);
                this.emailTakenError = errorMessage;
            }
        });
    }
}
