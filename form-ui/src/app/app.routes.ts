import { Routes } from '@angular/router';
import { SignupComponent } from './signup/signup.component';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { PublicFormsComponent } from './public-forms/public-forms.component';
import { FormViewComponent } from './form-view/form-view.component';
import { FormResponsesComponent } from './form-responses/form-responses.component';
import { FormComposeComponent } from './form-compose/form-compose.component';

export const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        title: 'Home - Digital Form',
    },
    {
        path: 'signup',
        component: SignupComponent,
        title: 'Signup - Digital Form',
    },
    {
        path: 'login',
        component: LoginComponent,
        title: 'Login - Digital Form',
    },
    {
        path: 'public-forms',
        component: PublicFormsComponent,
        title: 'Public - Digital Form',
    },
    {
        path: 'forms/:id',
        component: FormViewComponent,
        title: 'Details - Digital Form',
    },
    {
        path: 'forms/:id/responses',
        component: FormResponsesComponent,
        title: 'Responses - Digital Form'
    },
    {
        path: 'forms/:id/compose',
        component: FormComposeComponent,
        title: 'Compose - Digital Form'
    }
];
