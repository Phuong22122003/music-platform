import { ActivatedRoute, Router } from '@angular/router';
import { Component } from '@angular/core';
import { Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-library-tab',
  standalone: false,
  templateUrl: './library-tab.component.html',
  styleUrl: './library-tab.component.scss',
})
export class LibraryTabComponent {
  tabs = [
    { name: 'Overview', route: 'overview', active: true },
    { name: 'Likes', route: 'likes', active: false },
    { name: 'Playlists', route: 'playlists', active: false },
    { name: 'Albums', route: 'albums', active: false },
    { name: 'Following', route: 'following', active: false },
    { name: 'History', route: 'history', active: false },
  ];
  constructor(private router: Router, private route: ActivatedRoute) {}

  // setActiveTab(index: number) {
  //   this.tabs.forEach((tab, i) => tab.active = i === index);
  //   this.router.navigate([this.tabs[index].route]);
  // }
  // @Output() tabSelected = new EventEmitter<string>();
  // selectTab(tab: any) {
  //   this.tabs.forEach((t) => (t.active = false));
  //   tab.active = true;
  //   this.tabSelected.emit(tab.name);

  //   this.router.navigate([tab['route']], { relativeTo: this.route });
  // }
}
