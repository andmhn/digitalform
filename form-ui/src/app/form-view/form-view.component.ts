import { HttpClient } from '@angular/common/http';
import { Component, inject, Input, OnInit } from '@angular/core';
import { baseUrl } from '../user.service';
import { Message } from 'primeng/message';
import { CommonModule } from '@angular/common';
import { Card } from 'primeng/card';
import { PrimeTemplate } from 'primeng/api';
import { Button } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

export interface Question {
  question_id: Number;
  query: string;
  required: boolean;
  type: string;
  choices: string[];
}

export interface FormData {
  form_id: string;
  header: string;
  description: string;
  unlisted: boolean,
  questions: Question[];
}

interface Answer {
  "question_id": Number;
  "answer": string;
}

@Component({
  selector: 'app-form-view',
  standalone: true,
  imports: [Message, Card, CommonModule, PrimeTemplate, Button, InputTextModule, FormsModule, ReactiveFormsModule],
  templateUrl: './form-view.component.html',
  styleUrl: './form-view.component.scss'
})
export class FormViewComponent implements OnInit {
  @Input() id!: string;
  isSubmitted = false;
  error: any;
  http = inject(HttpClient);

  formData: FormData | null = null;
  formInput = new FormGroup({});

  ngOnInit(): void {
    this.http.get<FormData>(baseUrl + "/api/public/forms?form_id=" + this.id)
      .subscribe({
        next: (res) =>{
          this.formData = res;
          this.toFormGroup(this.formData.questions);
        },
        error: (e) => this.error = e.error
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
          answer: this.formInput.get(String(question_id))?.getRawValue()
        });
      }
      console.log(answers);
      
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
  }
}
