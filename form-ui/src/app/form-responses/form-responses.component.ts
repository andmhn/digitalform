import { HttpClient } from '@angular/common/http';
import { Component, computed, effect, inject, Input, input, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Card } from 'primeng/card';
import { baseUrl, UserService } from '../user.service';
import { FormControl, FormGroup } from '@angular/forms';
import { FormData } from '../form-view/form-view.component';
import { Question } from '../question-input/question-input.component';
import { Message } from 'primeng/message';

@Component({
  selector: 'app-form-responses',
  standalone: true,
  imports: [Card, Message],
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

  formData = signal<FormData | null>(null);
  formInput = new FormGroup({});

  constructor(){
    effect(() => {
      // Todo: load responses
    });
  }

  ngOnInit(): void {
    this.http.get<FormData>(baseUrl + "/api/public/forms?form_id=" + this.id)
      .subscribe({
        next: (res) => {
          this.formData.set(res);
          this.toFormGroup(this.formData()?.questions);
        },
        error: (e) => this.error = e.error
      });
  }

  private toFormGroup(questions: Question[] | undefined) {
    if(questions === undefined) return;
    for (let index = 0; index < questions.length; index++) {
      this.formInput.setControl(
        String(questions[index].question_id),
        new FormControl({ value: '', disabled: true })
      );
    }
    this.formInput.reset();
  }
}
