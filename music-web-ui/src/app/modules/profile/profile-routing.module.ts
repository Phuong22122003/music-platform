import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProfileComponent } from './profile/profile.component';

import { ListTabComponent } from './profile/panel-tab/list-tab/list-tab.component';
import { TrackTabComponent } from './profile/panel-tab/track-tab/track-tab.component';

const routes: Routes = [
  {
    path: '',
    component: ProfileComponent,
    children: [
      { path: 'albums', component: ListTabComponent },
      { path: 'playlists', component: ListTabComponent },
      { path: 'tracks', component: TrackTabComponent },
      {
        path: 'popular-tracks',
        component: TrackTabComponent,
      },
      { path: '**', redirectTo: 'popular-tracks', pathMatch: 'full' },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProfileRoutingModule {}
