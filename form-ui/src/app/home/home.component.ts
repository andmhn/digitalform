import { Component, effect, inject } from '@angular/core';
import { FormPreview, FormPreviewComponent } from '../form-preview/form-preview.component';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../user.service';
import { baseUrl } from '../app.config';
import { Button } from 'primeng/button';
import { FormData } from '../form-view/form-view.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormPreviewComponent, Button],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  userforms: FormPreview[] | null = null;

  http = inject(HttpClient)
  userService = inject(UserService)
  router = inject(Router);

  constructor(){
    effect(() => { // when user is authenticated
      if (this.userService.currentUser()) {
        this.http.get<FormPreview[]>(baseUrl + "/api/users/forms/info")
          .subscribe(res => this.userforms = res);
      }
    });
  }

  createForm() {
    let newForm: FormData = {
      form_id: 0,
      header: "Untitled Form",
      description: "",
      unlisted: true,
      owner_email: "",
      published: false,
      questions: []
    };
    this.http.post<FormData>(baseUrl + "/api/users/forms", newForm).subscribe(res => {
      newForm = res;
      this.router.navigateByUrl("/forms/" + newForm.form_id + "/compose");
    });
  }
}
