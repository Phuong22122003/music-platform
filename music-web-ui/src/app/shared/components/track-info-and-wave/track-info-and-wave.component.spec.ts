import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrackInfoAndWaveComponent } from './track-info-and-wave.component';

describe('TrackInfoAndWaveComponent', () => {
  let component: TrackInfoAndWaveComponent;
  let fixture: ComponentFixture<TrackInfoAndWaveComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TrackInfoAndWaveComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrackInfoAndWaveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
