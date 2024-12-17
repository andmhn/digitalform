import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { UserService } from '../auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [Card, Button, InputTextModule, CommonModule, FormsModule, PasswordModule, RouterModule, ReactiveFormsModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss'
})
export class SignupComponent {
  signupForm = new FormGroup({
    name: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', Validators.required),
  });

  userService = inject(UserService);

  signup(){
    this.userService.signup({
      name: String(this.signupForm.value.name),
      email: String(this.signupForm.value.email),
      password: String(this.signupForm.value.password)
    });
  }
}
