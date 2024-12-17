import { inject, Injectable, signal } from '@angular/core';
import { UserInterface } from './user.interface';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

export const baseUrl = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  currentUser = signal<UserInterface | null>(null);
  http = inject(HttpClient);
  router = inject(Router);

  constructor() {
  }

  signup(user: UserInterface) {
    this.http.post<AuthResponse>(baseUrl + '/auth/signup', user)
    .subscribe(
      res => {
        this.updateUser(res, user.password)
        this.router.navigateByUrl('/');
      }
    );
  }

  login(user: {email: string, password: string}) {
    this.http.post<AuthResponse>(baseUrl + "/auth/authenticate", user)
      .subscribe(
        res => {
          this.updateUser(res, user.password)
          this.router.navigateByUrl('/');
        }
      );
  }

  loginSavedUser() {
    let savedEmail = localStorage.getItem('email');
    let savedPassword = localStorage.getItem('password');

    if(!savedEmail || !savedPassword)
      return;

    this.http.post<AuthResponse>(baseUrl + "/auth/authenticate", {email: savedEmail, password: savedPassword})
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

  logout(){
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
  }
}

interface AuthResponse {
  id: string;
  email:string;
  name: string;
}