import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { UserProfile } from '../../../../core/models/user_profile';
import { SearchService } from '../../../../core/services/search.service';
import { ProfileService } from '../../../../core/services/profile.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-users',
  standalone: false,
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})
export class UsersComponent implements OnInit, OnDestroy {
  users: UserProfile[] = [];
  ids!: string[];
  visibleIds!: string[];
  currentId: number = 0;
  itemsPerLoad: number = 10;
  isLoading = false;
  sub!: Subscription;

  constructor(
    private searchService: SearchService,
    private profileService: ProfileService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.sub = this.route.queryParams.subscribe((params) => {
      const query = params['q'];
      this.searchService.emitSearchChange(query);
      this.search(query);
    });
  }

  search(query: string) {
    this.searchService.searchUserIds(query).subscribe((ids) => {
      this.ids = ids;
      console.log(ids);
      this.visibleIds = [];
      this.loadMore();
    });
  }

  loadMore() {
    if (this.isLoading || this.currentId >= this.ids.length) return;
    this.isLoading = true;
    console.log('Load more users...');
    const nextItems = this.ids.slice(
      this.currentId,
      this.currentId + this.itemsPerLoad
    );
    this.visibleIds.push(...nextItems);
    this.currentId += this.itemsPerLoad;
    if (nextItems.length > 0) {
      this.profileService.getProfileByIds(nextItems).subscribe((users) => {
        this.users.push(...users.data);
        this.isLoading = false;
      });
    } else {
      this.isLoading = false;
    }
  }

  @HostListener('window:scroll', [])
  onScroll(): void {
    const threshold = 300;
    const position = window.innerHeight + window.scrollY;
    const height = document.body.offsetHeight;

    if (position > height - threshold) {
      this.loadMore();
    }
  }

  ngOnDestroy(): void {
    if (this.sub) this.sub.unsubscribe();
  }
}
