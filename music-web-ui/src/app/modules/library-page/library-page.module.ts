import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OverviewComponent } from './overview/overview.component';
import { LikesComponent } from './likes/likes.component';
import { PlaylistComponent } from './playlist/playlist.component';
import { AlbumsComponent } from './albums/albums.component';
import { StationsComponent } from './stations/stations.component';
import { FollowingComponent } from './following/following.component';
import { HistoryComponent } from './history/history.component';
import { LibraryRoutingModule } from './library-routing.module';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { LibraryComponent } from './library.component';
import { LibraryTabComponent } from './library-tab/library-tab.component';

@NgModule({
  declarations: [
    OverviewComponent,
    LikesComponent,
    PlaylistComponent,
    AlbumsComponent,
    StationsComponent,
    FollowingComponent,
    HistoryComponent,
    LibraryComponent,
    LibraryTabComponent,
  ],
  imports: [
    CommonModule,
    LibraryRoutingModule,
    NgSelectModule,
    FormsModule,
    SharedModule,
  ],
})
export class LibraryPageModule {}
