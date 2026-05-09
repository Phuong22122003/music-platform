import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchOptionBarComponent } from './search-option-bar.component';

describe('SearchOptionBarComponent', () => {
  let component: SearchOptionBarComponent;
  let fixture: ComponentFixture<SearchOptionBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SearchOptionBarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchOptionBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
