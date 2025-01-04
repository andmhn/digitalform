import { HttpClient } from '@angular/common/http';
import { Component, computed, effect, inject, Input, signal } from '@angular/core';
import { UserService } from '../user.service';
import { Message } from 'primeng/message';
import { CommonModule } from '@angular/common';
import { Button } from 'primeng/button';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Question, QuestionViewComponent } from '../question-view/question-view.component';
import { DividerModule } from 'primeng/divider';
import { baseUrl } from '../app.config';
import { toObservable } from '@angular/core/rxjs-interop';
import { Tooltip } from 'primeng/tooltip';

export interface FormData {
  form_id: number;
  header: string;
  description: string;
  unlisted: boolean,
  owner_email: string,
  published: boolean,
  questions: Question[];
}

export interface Answer {
  question_id: Number;
  answer: string;
}

@Component({
  selector: 'app-form-view',
  standalone: true,
  imports: [QuestionViewComponent, Message, DividerModule, CommonModule, Button, FormsModule, ReactiveFormsModule, Tooltip],
  templateUrl: './form-view.component.html',
  styleUrl: './form-view.component.scss'
})
export class FormViewComponent {
  @Input() id!: number;
  isSubmitted = false;
  error: any;
  http = inject(HttpClient);
  router = inject(Router)
  userService = inject(UserService);
  currentUserOwnsForm = computed(() => this.userService.currentUser()?.email === this.formData?.owner_email);

  formData: FormData | null | undefined = undefined;
  formInput = new FormGroup({});
  mod = signal("public")

  constructor() {
    toObservable(this.userService.currentUser).subscribe(
      () => this.mod.set(this.userService.currentUser() ? "users" : "public")
    );
    effect(() => {
      this.error = null;
      this.http.get<FormData>(baseUrl + "/api/" + this.mod() + "/forms?form_id=" + this.id).subscribe({
        next: (res) => {
          this.formData = res;
          this.toFormGroup(this.formData.questions);
        },
        error: (e) => {
          this.error = e.error;
          this.formData = null;
        }
      });
    });
  }

  submit() {
    if (this.formData) {
      let answers: Answer[] = [];
      let questions: Question[] = this.formData.questions;

      for (let index = 0; index < questions.length; index++) {
        const question_id = questions[index].question_id;
        answers.push({
          question_id: question_id,
          answer: String(this.formInput.get(String(question_id))?.getRawValue())
        });
      }

      this.http.post(
        baseUrl + "/api/public/forms/submit?form_id=" + this.id,
        answers
      ).subscribe(
        () => {
          this.isSubmitted = true;
          this.formInput.reset();
        }
      )
    }
  }

  showAgain() {
    this.isSubmitted = false;
  }

  goToSubmissions() {
    this.router.navigateByUrl("/forms/" + this.id + "/responses");
  }

  copyLinkToClipboard(){
    navigator.clipboard.writeText(location.host + "/#" + this.router.url);
  }

  private toFormGroup(questions: Question[]) {
    for (let index = 0; index < questions.length; index++) {
      const question = questions[index];
      this.formInput.setControl(
        String(question.question_id),
        question.required
          ? new FormControl('', Validators.required)
          : new FormControl('')
      );
    }
    this.formInput.reset();
  }

  edit() {
    this.router.navigateByUrl("/forms/" + this.formData?.form_id + "/compose");
  }
  
  deleteForm() {
    this.http.delete(baseUrl + "/api/users/forms?form_id=" + this.id).subscribe({
      next: () => {
        this.router.navigateByUrl("/");
      },
      error: (e) => this.error = e.error
    });
  }
}
