import { HttpClient } from '@angular/common/http';
import { Component, computed, inject, Input, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../user.service';
import { FormData } from '../form-view/form-view.component';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { baseUrl } from '../app.config';
import { MessageModule } from 'primeng/message';
import { DividerModule } from 'primeng/divider';
import { Button } from 'primeng/button';
import { Textarea } from 'primeng/textarea';
import { FloatLabel } from 'primeng/floatlabel';
import { Card } from 'primeng/card';
import { ToggleSwitch } from 'primeng/toggleswitch';
import { Tooltip } from 'primeng/tooltip';
import { QuestionComposeComponent } from "../question-compose/question-compose.component";

@Component({
  selector: 'app-form-compose',
  standalone: true,
  imports: [ToggleSwitch, MessageModule, ReactiveFormsModule, DividerModule, Button, FloatLabel, Textarea, Card, Tooltip, QuestionComposeComponent],
  templateUrl: './form-compose.component.html',
  styleUrl: './form-compose.component.scss'
})
export class FormComposeComponent implements OnInit {
  @Input() id!: string;
  @ViewChild(QuestionComposeComponent) questionComposer!: QuestionComposeComponent;

  error: any;
  http = inject(HttpClient);
  router = inject(Router)
  userService = inject(UserService);
  currentUserOwnsForm = computed(() => this.userService.currentUser()?.email === this.formData?.owner_email);
  formData: FormData | null = null;
  formInfoEditor = new FormGroup({});

  ngOnInit(): void {
    this.http.get<FormData>(baseUrl + "/api/users/forms?form_id=" + this.id).subscribe({
      next: (res) => {
        this.formData = res;
        this.formInfoEditor.setControl("header", new FormControl(this.formData?.header, Validators.required));
        this.formInfoEditor.setControl("description", new FormControl(this.formData?.description));
        this.formInfoEditor.setControl("unlisted", new FormControl(this.formData?.unlisted));
      },
      error: (e) => this.error = e.error
    });
  }

  saveForm() {
    if (this.formInfoEditor.valid) {
      this.http.patch<FormData>(
        baseUrl + "/api/users/forms?form_id=" + this.id,
        this.formInfoEditor.getRawValue()
      ).subscribe({
        next: () => {
          this.questionComposer.updateChangedQuestions();
          this.questionComposer.showFormIfNotDirty();
        },
        error: (e) => this.error = e.error
      });
    } else {
      this.setInvalidFormErr();
    }
  }

  publishForm() {
    if (this.questionComposer.questionEditors.valid && this.formInfoEditor.valid) {
      this.formInfoEditor.setControl("published", new FormControl(true));
      this.saveForm();
      this.questionComposer.showFormIfNotDirty();
    } else {
      this.setInvalidFormErr();
    }
  }

  deleteForm() {
    this.http.delete(baseUrl + "/api/users/forms?form_id=" + this.id).subscribe({
      next: () => {
        this.router.navigateByUrl("/");
      },
      error: (e) => this.error = e.error
    });
  }

  private setInvalidFormErr() {
    this.error = { error: "Invalid Form", message: "Please make sure forms are not empty!!" }
  }
}
