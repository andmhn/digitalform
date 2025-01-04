import { inject, Injectable, signal } from '@angular/core';
import { UserInterface } from './user.interface';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { baseUrl } from './app.config';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  currentUser = signal<UserInterface | null | undefined>(undefined); // null means 'not logged in'
  http = inject(HttpClient);
  router = inject(Router);
  error: any;

  constructor() {
  }

  signup(user: UserInterface) {
    this.http.post<AuthResponse>(baseUrl + '/auth/signup', user)
      .subscribe({
        next: res => this.updateUser(res, user.password),
        error: e  => this.error = e.error
      });
  }

  login(user: { email: string, password: string }) {
    this.http.post<AuthResponse>(baseUrl + "/auth/authenticate", user)
      .subscribe({
        next:  res => this.updateUser(res, user.password),
        error: e  => this.error = e.error
      });
  }

  loginSavedUser() {
    let savedEmail = localStorage.getItem('email');
    let savedPassword = localStorage.getItem('password');

    if (!savedEmail || !savedPassword){
      this.currentUser.set(null);
      return;
    }

    this.http.post<AuthResponse>(baseUrl + "/auth/authenticate", { email: savedEmail, password: savedPassword })
      .subscribe(
        res => {
          this.currentUser.set({
            name: res.name,
            email: res.email,
            password: savedPassword
          })
        }
      );
  }

  logout() {
    localStorage.removeItem('email');
    localStorage.removeItem('password');
    this.currentUser.set(null);
    this.router.navigateByUrl('/');
  }

  private updateUser(res: AuthResponse, password: string) {
    localStorage.setItem('email', res.email);
    localStorage.setItem('password', password);

    let newUser: UserInterface = {
      name: res.name,
      email: res.email,
      password: password
    };
    this.currentUser.set(newUser);
    this.error = null;
    this.router.navigateByUrl('/');
  }
}

interface AuthResponse {
  id: number;
  email: string;
  name: string;
}