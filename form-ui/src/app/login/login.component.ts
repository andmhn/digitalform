import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from "primeng/password";
import { UserService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [Card, Button, InputTextModule, CommonModule, FormsModule, PasswordModule, RouterModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
    loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
    });
  
  userService = inject(UserService);

  login(){
    this.userService.login({
      email: String(this.loginForm.value.email),
      password: String(this.loginForm.value.password)
    });
  }
}
