import { Component, Input } from '@angular/core';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Card } from 'primeng/card';
import { FloatLabel } from 'primeng/floatlabel';
import { InputText } from 'primeng/inputtext';
import { Textarea } from 'primeng/textarea';
import { InputNumber } from 'primeng/inputnumber';
import { DatePicker } from 'primeng/datepicker';
import { MultiSelect } from 'primeng/multiselect';
import { RadioButton } from 'primeng/radiobutton';
import { Checkbox } from 'primeng/checkbox';
import { Tooltip } from 'primeng/tooltip';

export interface Question {
  question_id: Number;
  query: string;
  required: boolean;
  type: QuestionType;
  choices: string[];
}

export enum QuestionType {
  email = 'email',
  short_answer = 'short_answer',
  long_answer = 'long_answer',
  number = 'number',
  rupees = 'rupees',
  date = 'date',
  multiple_dropdown = 'multiple_dropdown',
  radiobox = 'radiobox',
  checkbox = 'checkbox'
}

@Component({
  selector: 'app-question-view',
  standalone: true,
  imports: [Tooltip, FormsModule, ReactiveFormsModule, Card, FloatLabel,InputText, Textarea, InputNumber, DatePicker, MultiSelect, RadioButton, Checkbox],
  templateUrl: './question-view.component.html',
  styleUrl: './question-view.component.scss'
})
export class QuestionViewComponent {
  @Input() parentFormGroup!: FormGroup;
  @Input() question!: Question;
  readonly QuestionType = QuestionType;
}
