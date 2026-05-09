import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FollowingCircleComponent } from './following-circle.component';

describe('FollowingCircleComponent', () => {
  let component: FollowingCircleComponent;
  let fixture: ComponentFixture<FollowingCircleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FollowingCircleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FollowingCircleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
