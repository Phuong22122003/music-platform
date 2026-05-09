import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PlayMusicRoutingModule } from './play-music-routing.module';
import { PlayMusicComponent } from './play-music/play-music.component';

import { SharedModule } from '../../shared/shared.module';
import { TrackPlayComponent } from './play-music/track-play/track-play.component';
import { CommentComponent } from './play-music/comment/comment.component';
import { FormsModule } from '@angular/forms';
import { CommentItemComponent } from './play-music/comment/comment-item/comment-item.component';
import { ProfileComponent } from './play-music/profile/profile.component';
import { RelatedTrackPanelComponent } from './play-music/related-track-panel/related-track-panel.component';
import { TrackDescComponent } from './play-music/track-desc/track-desc.component';
import { SendCommentComponent } from './play-music/send-comment/send-comment.component';
import { AllLikeComponent } from './play-music/all-like/all-like.component';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';

@NgModule({
  declarations: [
    PlayMusicComponent,
    TrackPlayComponent,
    CommentComponent,
    CommentItemComponent,
    ProfileComponent,
    RelatedTrackPanelComponent,
    TrackDescComponent,
    SendCommentComponent,
    AllLikeComponent,
  ],
  imports: [
    CommonModule,
    PlayMusicRoutingModule,
    SharedModule,
    FormsModule,
    InfiniteScrollModule,
  ],
})
export class PlayMusicModule {}
