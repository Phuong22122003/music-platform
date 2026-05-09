import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SearchComponent } from './search.component';
import { TracksComponent } from './component/tracks/tracks.component';
import { UsersComponent } from './component/users/users.component';
import { PlaylistsComponent } from './component/playlists/playlists.component';
import { AlbumsComponent } from './component/albums/albums.component';



const routes: Routes = [
  {
    path:'',
    component: SearchComponent,
    children:[
      {
        path:'tracks',
        component: TracksComponent
      },
      {
        path:'users',
        component: UsersComponent
      },
      {
        path:'playlists',
        component: PlaylistsComponent
      },
      {
        path:'albums',
        component: AlbumsComponent
      },
    ] 
  },
  // { path: '**', redirectTo: 'tracks', pathMatch: 'full' },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SearchRoutingModule { }
