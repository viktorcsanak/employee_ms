import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { environment } from './env';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})
export class LoginComponent {
    constructor(private http: HttpClient, private router: Router) {}

    loginData = {email: '', password: ''};

    onSubmit(loginForm: NgForm) {
        if (!loginForm.valid) {
            console.log("Login data is invalid", loginForm.value);
            return;
        }
        const { email, password } = loginForm.value;
        this.loginData.email = email;
        this.loginData.password = password;
        this.http.post('/api/auth/login', this.loginData)
            .subscribe(response => {
                console.log('Login repsonse:', response);
                this.router.navigate(['/']);
            }, error => {
                console.error('Login Error', error);
            });
    }
}
