import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionComposeComponent } from './question-compose.component';

describe('QuestionComposeComponent', () => {
  let component: QuestionComposeComponent;
  let fixture: ComponentFixture<QuestionComposeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuestionComposeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuestionComposeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
