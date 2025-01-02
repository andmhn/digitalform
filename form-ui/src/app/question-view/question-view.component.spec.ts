import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionViewComponent } from './question-view.component';

describe('QuestionInputComponent', () => {
  let component: QuestionViewComponent;
  let fixture: ComponentFixture<QuestionViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuestionViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuestionViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
