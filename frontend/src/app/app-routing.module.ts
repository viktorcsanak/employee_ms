import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomePageComponent } from './home-page/home-page.component';
import { AdminPageComponent } from './admin-page/admin-page.component';
import { HrPageComponent } from './hr-page/hr-page.component';
import { loginGuard } from './login.guard';
import { adminGuard } from './admin.guard';
import { hrGuard } from './hr.guard';
import { alreadyLoggedInGuard } from './already-logged-in.guard';

const routes: Routes = [
    { path: '', redirectTo: '/home', pathMatch: 'full' },
    { path: 'login', component: LoginComponent, canActivate: [alreadyLoggedInGuard] },
    { path: 'register', component: RegisterComponent, canActivate: [adminGuard] },
    { path: 'home', component: HomePageComponent, canActivate: [loginGuard] },
    { path: 'admin', component: AdminPageComponent, canActivate: [adminGuard] },
    { path: 'hr', component: HrPageComponent, canActivate: [hrGuard] },
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }
