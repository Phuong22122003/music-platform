import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RelatedTrackPanelComponent } from './related-track-panel.component';

describe('RelatedTrackPanelComponent', () => {
  let component: RelatedTrackPanelComponent;
  let fixture: ComponentFixture<RelatedTrackPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RelatedTrackPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RelatedTrackPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
