import { Component } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { catchError, throwError } from 'rxjs';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})
export class LoginComponent {
    constructor(private http: HttpClient, private router: Router) {}

    loginData = {email: '', password: ''};
    errorMessage: string | null = null;

    form: FormGroup = new FormGroup({
        email: new FormControl('', { validators: [Validators.required, Validators.email] }),
        password: new FormControl('', { validators: [Validators.required]}),
    });

    onSubmit() {
        if (!this.form.valid) {
            console.log("Login data is invalid", this.form.value);
            return;
        }
        const { email, password } = this.form.value;
        this.loginData.email = email;
        this.loginData.password = password;
        this.http.post('/api/auth/login', this.loginData).pipe(
            catchError((error: HttpErrorResponse) => {
                if (error.status == 401) {
                    return throwError('Invalid username or password');
                }
                return throwError('An unknown error occured');
            })
        ).subscribe({
            next: (response) => {
                console.log('Login repsonse:', response);
                this.router.navigate(['/home']);
            }, error: (errorMessage) => {
                console.error('Login Error', errorMessage);
                this.errorMessage = errorMessage;
            }
        });
    }
}
