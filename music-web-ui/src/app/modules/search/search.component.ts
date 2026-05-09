import { Component, OnDestroy, OnInit } from '@angular/core';
import { SearchService } from '../../core/services/search.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-seach',
  templateUrl: 'search.component.html',
  styleUrl: 'search.component.scss',
  standalone: false,
})
export class SearchComponent implements OnInit, OnDestroy {
  searchQuery = '';
  subQueryChange!: Subscription;
  constructor(private searchService: SearchService) {}
  ngOnInit(): void {
    this.subQueryChange = this.searchService.search$.subscribe(
      (query) => (this.searchQuery = query)
    );
  }
  ngOnDestroy(): void {
    if (this.subQueryChange) {
      this.subQueryChange.unsubscribe();
    }
  }
}
