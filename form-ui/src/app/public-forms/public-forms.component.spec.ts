import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PublicFormsComponent } from './public-forms.component';

describe('PublicFormsComponent', () => {
  let component: PublicFormsComponent;
  let fixture: ComponentFixture<PublicFormsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PublicFormsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PublicFormsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
