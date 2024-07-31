import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
    constructor(private http: HttpClient, private router: Router) {}

    registerData = {email: '', password: '', confirmPassword: ''};

    onSubmit(registerForm: NgForm) {
        if (!registerForm.valid) {
            console.log("Register data is invalid", registerForm.value);
            return;
        }
        const { email, password, confirmPassword } = registerForm.value;
        this.registerData.email = email;
        this.registerData.password = password;
        this.registerData.confirmPassword = confirmPassword;

        if (password !== confirmPassword) {
            console.log("Confirm password and password do not match!");
            return;
        }
        
        this.http.post('/api/auth/register', this.registerData)
            .subscribe(response => {
                console.log('Register repsonse:', response);
                this.router.navigate(['/']);
            }, error => {
                console.error('Register Error', error);
            });
    }
}
