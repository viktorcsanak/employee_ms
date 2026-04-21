import { Component } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { catchError, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent {

    errorMessage: string | null = null;

    form: FormGroup = new FormGroup({
        email: new FormControl('', [Validators.required, Validators.email]),
        password: new FormControl('', [Validators.required]),
    });

    constructor(
        private http: HttpClient,
        private router: Router
    ) { }

    onSubmit() {
    if (this.form.invalid) {
        console.log("Login data is invalid", this.form.value);
        return;
    }

    const body = this.form.value;

    const url =
        `${environment.serverUrl}:${environment.serverPort}/api/auth/login`;

    this.http.post(url, body, { withCredentials: true })
        .pipe(
            catchError((error: HttpErrorResponse) => {

                console.error('Backend error:', error);

                if (error.error?.errorMessage) {
                    this.errorMessage = error.error.errorMessage;
                } 
                else if (error.status === 401) {
                    this.errorMessage = 'Invalid username or password';
                } 
                else if (error.status === 404) {
                    this.errorMessage = 'Backend endpoint not found';
                } 
                else {
                    this.errorMessage = 'An unknown error occurred';
                }

                return throwError(() => error);
            })
        )
        .subscribe({
            next: (response) => {
                console.log('Login response:', response);
                this.router.navigate(['/home']);
            },
            error: (err: HttpErrorResponse) => {
                console.error('Login Error', err);
            }
        });
    }
}