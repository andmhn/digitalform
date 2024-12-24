import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormComposeComponent } from './form-compose.component';

describe('FormComposeComponent', () => {
  let component: FormComposeComponent;
  let fixture: ComponentFixture<FormComposeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormComposeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormComposeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
