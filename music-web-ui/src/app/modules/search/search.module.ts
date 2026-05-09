import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchOptionBarComponent } from './component/search-option-bar/search-option-bar.component';
import { SearchRoutingModule } from './search-routing.module';
import { SearchComponent } from './search.component';
import { TracksComponent } from './component/tracks/tracks.component';
import { UsersComponent } from './component/users/users.component';
import { PlaylistsComponent } from './component/playlists/playlists.component';
import { AlbumsComponent } from './component/albums/albums.component';
import { UserComponent } from './component/users/user/user.component';
import { SharedModule } from '../../shared/shared.module';
import { PlaylistComponent } from './component/playlists/playlist/playlist.component';
import { AlbumComponent } from './component/albums/album/album.component';

@NgModule({
  declarations: [
    SearchOptionBarComponent,
    SearchComponent,
    TracksComponent,
    UsersComponent,
    PlaylistsComponent,
    AlbumsComponent,
    UserComponent,
    PlaylistComponent,
    AlbumComponent,
  ],
  imports: [CommonModule, SearchRoutingModule, SharedModule],
})
export class SearchModule {}
