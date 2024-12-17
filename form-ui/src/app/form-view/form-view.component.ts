import { HttpClient } from '@angular/common/http';
import { Component, inject, input, Input, OnInit } from '@angular/core';
import { baseUrl } from '../auth.service';
import { Message } from 'primeng/message';

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

@Component({
  selector: 'app-form-view',
  standalone: true,
  imports: [Message],
  templateUrl: './form-view.component.html',
  styleUrl: './form-view.component.scss'
})
export class FormViewComponent implements OnInit{
  @Input() id!: string;
  formData: FormData | null = null;

  http = inject(HttpClient);
  public error: any;

  ngOnInit(): void {
    this.http.get<FormData>(baseUrl + "/api/public/forms?form_id=" + this.id)
    .subscribe({
      next: (res) => this.formData = res,
      error: (e) => this.error = e.error
    });
  }
}
