import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { SearchService } from '../../../../core/services/search.service';

@Component({
  selector: 'app-search-option-bar',
  standalone: false,
  templateUrl: './search-option-bar.component.html',
  styleUrl: './search-option-bar.component.scss',
})
export class SearchOptionBarComponent implements OnInit, OnDestroy {
  subQueryChange!: Subscription;
  query = '';
  constructor(private searchService: SearchService) {}
  ngOnInit(): void {
    this.subQueryChange = this.searchService.search$.subscribe(
      (query) => (this.query = query)
    );
  }
  ngOnDestroy(): void {
    this.subQueryChange.unsubscribe();
  }
}
