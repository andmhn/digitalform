import { Component, inject, OnInit } from '@angular/core';
import { FormPreview,  FormPreviewComponent} from '../form-preview/form-preview.component';
import { baseUrl } from '../user.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-public-forms',
  standalone: true,
  imports: [FormPreviewComponent],
  templateUrl: './public-forms.component.html',
  styleUrl: './public-forms.component.scss'
})
export class PublicFormsComponent implements OnInit{
  formPreviews: FormPreview[] | null = null;
  http  = inject(HttpClient)

  ngOnInit(): void {
    this.http.get<FormPreview[]>(baseUrl + "/api/public/forms/info")
          .subscribe(
            res => {
              this.formPreviews = res;
            }
          );
  }
}
