import { Component, effect, inject, OnInit } from '@angular/core';
import { FormPreview, FormPreviewComponent } from '../form-preview/form-preview.component';
import { HttpClient } from '@angular/common/http';
import { UserService, baseUrl } from '../user.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormPreviewComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  publicforms: FormPreview[] = [];
  userforms: FormPreview[] = [];

  http = inject(HttpClient)
  userService = inject(UserService)

  constructor(){
    effect(() => { // when user is authenticated
      if (this.userService.currentUser()) {
        this.http.get<FormPreview[]>(baseUrl + "/api/users/forms/info")
          .subscribe(res => this.userforms = res);
      }
    });
  }

  ngOnInit(): void {
    this.http.get<FormPreview[]>(baseUrl + "/api/public/forms/info")
      .subscribe(res => this.publicforms = res);
  }
}
