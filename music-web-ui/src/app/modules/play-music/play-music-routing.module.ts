import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlayMusicComponent } from './play-music/play-music.component';
import { AllLikeComponent } from './play-music/all-like/all-like.component';

const routes: Routes = [
  { path: 'likes', component: AllLikeComponent },
  { path: '', component: PlayMusicComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PlayMusicRoutingModule {}
