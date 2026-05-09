import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlaylistComponent } from './playlist/playlist.component';
import { HistoryComponent } from './history/history.component';
import { FollowingComponent } from './following/following.component';
import { AlbumsComponent } from './albums/albums.component';
import { LibraryComponent } from './library.component';
import { LikesComponent } from './likes/likes.component';
import { OverviewComponent } from './overview/overview.component';

const routes: Routes = [
  {
    path: '',
    component: LibraryComponent,
    children: [
      {
        path: 'playlists',
        component: PlaylistComponent,
      },
      {
        path: 'history',
        component: HistoryComponent,
      },
      {
        path: 'following',
        component: FollowingComponent,
      },
      {
        path: 'albums',
        component: AlbumsComponent,
      },
      {
        path: 'likes',
        component: LikesComponent,
      },
      {
        path: 'overview',
        component: OverviewComponent,
      },
      { path: '**', redirectTo: 'overview', pathMatch: 'full' },
    ],
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LibraryRoutingModule {}
