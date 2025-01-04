import { Component, inject, Input, OnInit } from '@angular/core';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Question, QuestionType } from '../question-view/question-view.component';
import { Select } from 'primeng/select';
import { ToggleSwitch } from 'primeng/toggleswitch';
import { MessageModule } from 'primeng/message';
import { CommonModule } from '@angular/common';
import { DividerModule } from 'primeng/divider';
import { Button } from 'primeng/button';
import { FloatLabel } from 'primeng/floatlabel';
import { Textarea } from 'primeng/textarea';
import { Card } from 'primeng/card';
import { Tooltip } from 'primeng/tooltip';
import { baseUrl } from '../app.config';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-question-compose',
  standalone: true,
  imports: [Select, ToggleSwitch, MessageModule, ReactiveFormsModule, CommonModule, DividerModule, Button, FloatLabel, Textarea, Card, Tooltip],
  templateUrl: './question-compose.component.html',
  styleUrl: './question-compose.component.scss',
})
export class QuestionComposeComponent implements OnInit {
  @Input() formId !: number;
  @Input() questions: Question[] = [];

  error: any;
  router = inject(Router)
  http = inject(HttpClient);
  questionType = Object.values(QuestionType);
  questionEditors = new FormArray<FormGroup>([]);

  ngOnInit(): void {
    this.questions.forEach(
      q => this.toQuestionForm(q)
    );
    if (this.questions.length === 0) {
      this.addQuestion();
    }
  }

  addQuestion() {
    let newQuestion: Question = {
      query: "Untitled Question",
      index: this.questionEditors.length + 1,
      question_id: 0,
      required: false,
      type: QuestionType.short_answer,
      choices: []
    };
    this.http.post<Question>(baseUrl + "/api/users/questions/add?form_id=" + this.formId, newQuestion).subscribe({
      next: (res) => {
        newQuestion = res;
        this.questions.push(newQuestion);
        this.toQuestionForm(newQuestion);
      },
      error: (e) => this.error = e.error
    })
  }

  deleteQuestion(index: number) {
    if (this.questionEditors.length === 1) {
      this.error = { error: "Minimum 1 Question Is Required" };
      return;
    }
    this.http.delete(
      baseUrl + "/api/users/questions/" + this.questionEditors.at(index).get("question_id")?.getRawValue(),
    ).subscribe(() => {
      this.questionEditors.removeAt(index);
    })
  }

  addChoice(questionGroup: FormGroup<any>) {
    let choices: string[] = questionGroup.get("choices")?.getRawValue();
    choices.push("new choice");
    questionGroup.setControl("choices", this.toChoiceArray(choices));
    questionGroup?.markAsDirty();
  }

  removeChoice(questionGroup: FormGroup<any>, index: number) {
    let choices: string[] = questionGroup.get("choices")?.getRawValue();
    if (choices.length === 1) {
      this.error = { error: "Minimum 1 Choice Is Required" };
      return;
    }
    choices.splice(index, 1);
    questionGroup.setControl("choices", this.toChoiceArray(choices));
    questionGroup?.markAsDirty();
  }

  isMultipleType(type: string) {
    if (type === QuestionType.multiple_dropdown ||
      type === QuestionType.checkbox ||
      type === QuestionType.radiobox
    ) {
      return true;
    }
    return false;
  }

  updateQuestionType(questionGroup: FormGroup<any>) {
    this.questions.map(q => {
      if (q.question_id === questionGroup.get("question_id")?.getRawValue()) {
        q.type = questionGroup.get('type')?.getRawValue();

        if (this.isMultipleType(q.type) && q.choices === undefined) {
          q.choices = ['new choice'];
        }
      }
    });
  }

  updateChangedQuestions() {
    for (const formControl of this.questionEditors.controls) {
      if (formControl && formControl.dirty) {
        this.http.patch<Question>(
          baseUrl + "/api/users/questions/" + formControl.get("question_id")?.getRawValue(),
          formControl.getRawValue()
        ).subscribe(() => {
          formControl.reset();
          this.showFormIfNotDirty();
        });
      }
    }
  }

  showFormIfNotDirty() {
    if (!this.questionEditors.dirty) {
      this.router.navigateByUrl("/forms/" + this.formId);
    }
  }

  moveUp(index: number) {
    if(index == 0) return;
    this.swapForm(index, index - 1);
  }
  
  moveDown(index: number) {
    if(index == this.questionEditors.length - 1) return;
    this.swapForm(index, index + 1);
  }

  private swapForm(i : number, j : number){
    let current = this.questionEditors.at(i);
    let other = this.questionEditors.at(j);

    let currentValues = current.value;
    let otherValues = other.value;
    
    // swap question id
    let temp = currentValues.index;
    currentValues.index = other.value.index;
    otherValues.index = temp;

    current.setValue(currentValues);
    other.setValue(otherValues);
    
    current.markAsDirty();
    other.markAsDirty();
    
    // swap form group
    this.questionEditors.setControl(i, other);
    this.questionEditors.setControl(j, current);
  }

  private toQuestionForm(question: Question) {
    this.questionEditors.push(
      new FormGroup({
        "question_id": new FormControl(question.question_id, Validators.required),
        'index': new FormControl(question.index, Validators.required),
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
}
