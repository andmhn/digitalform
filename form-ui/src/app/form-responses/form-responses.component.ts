import { HttpClient } from '@angular/common/http';
import { Component, computed, effect, inject, Input, signal } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../user.service';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Answer, FormData } from '../form-view/form-view.component';
import { Question, QuestionViewComponent, QuestionType } from '../question-view/question-view.component';
import { Message } from 'primeng/message';
import { Tooltip } from 'primeng/tooltip';
import { Select } from 'primeng/select';
import { DividerModule } from 'primeng/divider';
import { toObservable } from '@angular/core/rxjs-interop';
import { baseUrl } from '../app.config';

interface Submission {
  submission_id: Number;
  form_id: number;
  answers: Answer[];
}

@Component({
  selector: 'app-form-responses',
  standalone: true,
  imports: [DividerModule, Message, Tooltip, Select, FormsModule, ReactiveFormsModule, QuestionViewComponent],
  templateUrl: './form-responses.component.html',
  styleUrl: './form-responses.component.scss'
})
export class FormResponsesComponent {
  @Input() id !: number;
  error: any;
  http = inject(HttpClient);
  router = inject(Router)
  userService = inject(UserService);
  currentUserOwnsForm = computed(() =>
     this.userService.currentUser() != null &&
     this.userService.currentUser()?.email === this.formData()?.owner_email);
     
  selectedSubmission: Submission | null = null;
  submissions: Submission[] | undefined = undefined;
  formData = signal<FormData | null>(null);
  formInput = new FormGroup({});

  constructor() {
    toObservable(this.userService.currentUser).subscribe(
      () => this.http.get<FormData>(baseUrl + "/api/public/forms?form_id=" + this.id).subscribe({
        next: (res) => this.formData.set(res),
        error: (e) => this.error = e.error
      })
    );
    effect(() => {
      this.http.get<Submission[]>(baseUrl + "/api/users/forms/submissions?form_id=" + this.id).subscribe(
        {
          next: res => this.submissions = res,
          error: (e) => this.error = e.error
        }
      );
    });
  }

  export() {
    this.http.get(baseUrl + "/api/users/forms/export?form_id=" + this.id, {
      responseType: 'blob'
    }).subscribe(blob => {
      const a = document.createElement('a')
      const objectUrl = URL.createObjectURL(blob)
      a.href = objectUrl
      a.download = this.formData()?.header + ' [' + new Date().toLocaleString() + '].csv';
      a.click();
      URL.revokeObjectURL(objectUrl);
    });
  }

  changeFormInput() {
    this.formInput = new FormGroup({});
    let questions = this.formData()?.questions;
    if (questions === undefined) return;

    for (let index = 0; index < questions.length; index++) {
      let question = questions[index];
      this.formInput.setControl(
        question.question_id.toString(),
        new FormControl({
          value: this.getAnswerOf(question),
          disabled: true
        })
      );
    }
  }

  private getAnswerOf(question: Question): string | string[] {
    let answerString = this.submissions?.find(s => s === this.selectedSubmission)
      ?.answers.find(a => question.question_id === a.question_id)
      ?.answer
    if (answerString === undefined) {
      answerString = ''
    }
    let answerList: null | string[] = null;
    if (question.type === QuestionType.multiple_dropdown || question.type === QuestionType.checkbox) {
      answerList = answerString.split(',')
    }
    return answerList ? answerList : answerString;
  }
}
