import { HttpClient } from '@angular/common/http';
import { Component, computed, effect, inject, Input, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../user.service';
import { FormData } from '../form-view/form-view.component';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
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
  currentUserOwnsForm = computed(() => this.userService.currentUser()?.email === this.formData?.owner_email);
  formData: FormData | null = null;
  formInfoEditor = new FormGroup({});
  questionEditors = new FormGroup({});
  questionType = Object.values(QuestionType);

  ngOnInit(): void {
    this.http.get<FormData>(baseUrl + "/api/users/forms?form_id=" + this.id).subscribe({
      next: (res) => {
        this.formData = res;
        this.formData?.questions.forEach(
          q => this.toQuestionForm(q)
        );
        this.formInfoEditor.setControl("header", new FormControl(this.formData?.header, Validators.required));
        this.formInfoEditor.setControl("description", new FormControl(this.formData?.description));
        this.formInfoEditor.setControl("unlisted", new FormControl(this.formData?.unlisted));
      },
      error: (e) => this.error = e.error
    });
  }

  save() {
    if (this.formInfoEditor.valid) {
      this.http.patch<FormData>(
        baseUrl + "/api/users/forms?form_id=" + this.id,
        this.formInfoEditor.getRawValue()
      ).subscribe({
        next: () => {
          this.updateChangedQuestions();
        },
        error: (e) => this.error = e.error
      });
    } else {
      this.setInvalidFormErr();
    }
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
    if (this.questionEditors.valid && this.formInfoEditor.valid) {
      this.formInfoEditor.setControl("published", new FormControl(true));
      this.save();
      this.showFormIfNotDirty();
    } else {
      this.setInvalidFormErr();
    }
  }

  private setInvalidFormErr() {
    this.error = { error: "Invalid Form", message: "Please make sure forms are not empty!!" }
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
      query: "Untitled Question",
      question_id: 0,
      required: false,
      type: QuestionType.short_answer,
      choices: []
    };
    this.http.post<Question>(baseUrl + "/api/users/questions/add?form_id=" + this.id, newQuestion).subscribe({
      next: (res) => {
        newQuestion = res;
        this.formData?.questions.push(newQuestion);
        this.toQuestionForm(newQuestion);
      },
      error: (e) => this.error = e.error
    })
  }

  private toQuestionForm(question: Question) {
    this.questionEditors.setControl(
      String(question.question_id),
      new FormGroup({
        'query': new FormControl(question.query, Validators.required),
        'required': new FormControl(question.required),
        'type': new FormControl(question.type, Validators.required),
        'choices': this.toChoiceArray(question.choices)
      })
    )
  }

  private toChoiceArray(choices: string[]) {
    if (choices == undefined)
      choices = ['new choice'];

    return new FormArray(
      choices.map((c) => new FormControl(c, Validators.required))
    )
  }

  isMultipleType(question_id: string) {
    const type: string = this.questionEditors.get(question_id)?.get('type')?.getRawValue();
    if (type === QuestionType.multiple_dropdown ||
      type === QuestionType.checkbox ||
      type === QuestionType.radiobox
    ) {
      return true;
    }
    return false;
  }

  removeChoice(question_id: string, index: number) {
    let question = this.formData?.questions.find(q => String(q.question_id) === question_id);
    if (question === undefined)
      return;
    if (index > -1) {
      question?.choices.splice(index, 1);
    }
    this.toQuestionForm(question);
    this.questionEditors.get(question_id)?.markAsDirty();
  }

  addChoice(question_id: string) {
    let question = this.formData?.questions.find(q => String(q.question_id) === question_id);
    if (question === undefined)
      return;
    question.choices = this.questionEditors.get(question_id)?.get('choices')?.getRawValue()
    question.choices.push("new choice");
    this.toQuestionForm(question);
    this.questionEditors.get(question_id)?.markAsDirty();
  }

  updateQuestionType(question_id: string) {
    this.formData?.questions.map(q => {
      if (String(q.question_id) === question_id) {
        q.type = this.questionEditors.get(question_id)?.get('type')?.getRawValue();

        if (this.isMultipleType(question_id) && q.choices === undefined) {
          q.choices = ['new choice'];
        }
      }
    });
  }
}
