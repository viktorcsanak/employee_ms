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
    loginData = {email: '', password: ''};

    constructor(private http: HttpClient, private router: Router) {}

    onSubmit(form: NgForm) {
        this.http.post(`http://${environment.serverUrl}:${environment.serverPort}/api/auth/login`, this.loginData)
            .subscribe(response => {
                console.log('Login repsonse:', response);
                this.router.navigate(['/']);
            }, error => {
                console.error('Login Error', error);
            });
    }
}
