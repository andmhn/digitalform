import { HttpClient } from '@angular/common/http';
import { Component, computed, effect, inject, Input, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { baseUrl, UserService } from '../user.service';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Answer, FormData } from '../form-view/form-view.component';
import { Question, QuestionInputComponent, QuestionType } from '../question-input/question-input.component';
import { Message } from 'primeng/message';
import { Tooltip } from 'primeng/tooltip';
import { Select } from 'primeng/select';
import { DividerModule } from 'primeng/divider';

interface Submission {
  submission_id: Number;
  form_id: string;
  answers: Answer[];
}

@Component({
  selector: 'app-form-responses',
  standalone: true,
  imports: [DividerModule, Message, Tooltip, Select, FormsModule, ReactiveFormsModule, QuestionInputComponent],
  templateUrl: './form-responses.component.html',
  styleUrl: './form-responses.component.scss'
})
export class FormResponsesComponent implements OnInit {
  @Input() id !: string;
  error: any;
  http = inject(HttpClient);
  router = inject(Router)
  userService = inject(UserService);
  currentUserOwnsForm = computed(() => this.userService.currentUser()?.email === this.formData()?.owner_email);
  selectedSubmission: Submission | null = null;
  submissions: Submission[] | undefined = undefined;
  formData = signal<FormData | null>(null);
  formInput = new FormGroup({});

  constructor() {
    effect(() => {
      this.http.get<Submission[]>(baseUrl + "/api/users/forms/submissions?form_id=" + this.id).subscribe(
        res => this.submissions = res
      );
    });
  }

  ngOnInit(): void {
    this.http.get<FormData>(baseUrl + "/api/public/forms?form_id=" + this.id).subscribe({
      next: (res) => {
        this.formData.set(res);
      },
      error: (e) => this.error = e.error
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
