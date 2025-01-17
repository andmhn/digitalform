import { Component, inject, Input } from '@angular/core';
import { Router } from '@angular/router';
import { Card } from 'primeng/card';
import { Ripple } from 'primeng/ripple';

export interface FormPreview {
  form_id :number;
  header: string;
  description :string;
  unlisted: boolean;
}

@Component({
  selector: 'app-form-preview',
  standalone: true,
  imports: [Card, Ripple],
  templateUrl: './form-preview.component.html',
  styleUrl: './form-preview.component.scss'
})
export class FormPreviewComponent {
  @Input({required: true}) formPreview: FormPreview | null = null;
  router  = inject(Router);

  openForm() {
    this.router.navigateByUrl("/forms/" + this.formPreview?.form_id);
  }
}

