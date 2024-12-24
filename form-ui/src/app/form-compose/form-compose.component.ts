import { HttpClient } from '@angular/common/http';
import { Component, computed, effect, inject, Input, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../user.service';
import { FormData } from '../form-view/form-view.component';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { baseUrl } from '../app.config';
import { MessageModule } from 'primeng/message';
import { CommonModule } from '@angular/common';
import { DividerModule } from 'primeng/divider';
import { Button } from 'primeng/button';
import { Textarea } from 'primeng/textarea';
import { FloatLabel } from 'primeng/floatlabel';
import { Card } from 'primeng/card';
import { ToggleSwitch } from 'primeng/toggleswitch';
import { Question, QuestionType } from '../question-input/question-input.component';
import { Select } from 'primeng/select';

@Component({
  selector: 'app-form-compose',
  standalone: true,
  imports: [Select, ToggleSwitch, MessageModule, ReactiveFormsModule, CommonModule, DividerModule, Button, FloatLabel, Textarea, Card],
  templateUrl: './form-compose.component.html',
  styleUrl: './form-compose.component.scss'
})
export class FormComposeComponent implements OnInit {
  @Input() id!: string;
  error: any;
  http = inject(HttpClient);
  router = inject(Router)
  userService = inject(UserService);
  currentUserOwnsForm = computed(() => this.userService.currentUser()?.email === this.formData()?.owner_email);
  formData = signal<FormData | null>(null);
  formInfoEditor = new FormGroup({});
  questionEditors = new FormGroup({});
  questionType = Object.values(QuestionType);

  constructor() {
    effect(() => {
      this.formInfoEditor.setControl("header", new FormControl(this.formData()?.header));
      this.formInfoEditor.setControl("description", new FormControl(this.formData()?.description));
      this.formInfoEditor.setControl("unlisted", new FormControl(this.formData()?.unlisted));
    })
  }

  ngOnInit(): void {
    this.http.get<FormData>(baseUrl + "/api/users/forms?form_id=" + this.id).subscribe({
      next: (res) => {
        this.formData.set(res);
        this.formData()?.questions.forEach(
          q => this.toQuestionForm(q)
        );
      },
      error: (e) => this.error = e.error
    });
  }

  save() {
    this.http.patch<FormData>(
      baseUrl + "/api/users/forms?form_id=" + this.id,
      this.formInfoEditor.getRawValue()
    ).subscribe({
      next: () => {
        this.updateChangedQuestions();
      },
      error: (e) => this.error = e.error
    });
  }

  private updateChangedQuestions() {
    for (const formControlName in this.questionEditors.controls) {
      let formControl = this.questionEditors.get(formControlName);

      if (formControl && formControl.dirty) {
        this.http.patch<Question>(
          baseUrl + "/api/users/questions/" + formControlName,
          formControl.getRawValue()
        ).subscribe(() => {
          formControl.reset();
          this.showFormIfNotDirty();
        });
      }
    }
  }

  private showFormIfNotDirty() {
    if (!this.questionEditors.dirty) {
      this.router.navigateByUrl("/forms/" + this.id);
    }
  }

  publish() {
    this.formInfoEditor.setControl("published", new FormControl(true));
    this.save();
    this.showFormIfNotDirty();
  }

  delete() {
    this.http.delete(baseUrl + "/api/users/forms?form_id=" + this.id).subscribe({
      next: () => {
        this.router.navigateByUrl("/");
      },
      error: (e) => this.error = e.error
    });
  }

  addQuestion() {
    let newQuestion: Question = {
      query: "Question",
      question_id: 0,
      required: false,
      type: QuestionType.short_answer,
      choices: []
    };
    this.http.post<Question>(baseUrl + "/api/users/questions/add?form_id=" + this.id, newQuestion).subscribe({
      next: (res) => {
        newQuestion = res;
        this.formData()?.questions.push(newQuestion);
        this.toQuestionForm(newQuestion);
      },
      error: (e) => this.error = e.error
    })
  }

  private toQuestionForm(question: Question) {
    this.questionEditors.setControl(
      String(question.question_id),
      new FormGroup({
        'question_id': new FormControl(question.question_id),
        'query': new FormControl(question.query),
        'required': new FormControl(question.required),
        'type': new FormControl(question.type),
        'choices': new FormControl(question.choices)
      })
    )
  }
}
