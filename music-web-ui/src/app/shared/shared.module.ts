import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './components/header/header.component';
import { SoundCardComponent } from './components/sound-card/sound-card.component';
import { RelatedTrackComponent } from './components/related-track/related-track.component';
import { WavesurferComponent } from './components/wavesurfer/wavesurfer.component';
import { TrackCardComponent } from './components/track-card/track-card.component';
import { SideBarComponent } from './components/side-bar/side-bar.component';
import { UserCardComponent } from './components/user-card/user-card.component';
import { AuthModalComponent } from './components/auth-modal/auth-modal.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ArtistCardComponent } from './components/artist-card/artist-card.component';
import { CardComponent } from './components/card/card.component';
import { CardListComponent } from './components/card-list/card-list.component';
import { RecommendedCardComponent } from './components/recommended-card/recommended-card.component';
import { NextPlaylistComponent } from './components/next-playlist/next-playlist.component';
import { NextUpComponent } from './components/next-playlist/next-up/next-up.component';
import { NextUpItemComponent } from './components/next-playlist/next-up/next-up-item/next-up-item.component';
import { DynamicFormComponent } from './components/dynamic-form/dynamic-form.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { TrackInfoAndWaveComponent } from './components/track-info-and-wave/track-info-and-wave.component';
import { RouterModule } from '@angular/router';
import { SocialLoginComponent } from './components/social-login/social-login.component';
import { TimeAgoPipe } from './pipes/time-ago.pipe';
import { TrackListComponent } from './components/track-list/track-list.component';
import { TrackActionsComponent } from './components/track-actions/track-actions.component';
import { ClickOutsideDirective } from './directives/click-outside.directive';
import { PlaylistModalComponent } from './components/playlist-modal/playlist-modal.component';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { TrackActionComponent } from './components/track-action/track-action.component';
import { ListActionComponent } from './components/list-action/list-action.component';
import { EditPlaylistComponent } from './components/list-action/edit-playlist/edit-playlist.component';
import { EditTrackComponent } from './components/track-action/edit-track/edit-track.component';
import { FormatDatePipe } from './pipes/format-date.pipe';
import { FollowingCircleComponent } from './components/following-circle/following-circle.component';
import { NotificationComponent } from './components/notification/notification.component';
import { LoadingComponent } from './components/loading/loading.component';
@NgModule({
  declarations: [
    HeaderComponent,
    SoundCardComponent,
    RelatedTrackComponent,
    WavesurferComponent,
    TrackCardComponent,
    SideBarComponent,
    UserCardComponent,
    AuthModalComponent,
    ArtistCardComponent,
    CardComponent,
    CardListComponent,
    RecommendedCardComponent,
    NextPlaylistComponent,
    NextUpComponent,
    NextUpItemComponent,
    DynamicFormComponent,
    TrackInfoAndWaveComponent,
    SocialLoginComponent,
    TimeAgoPipe,
    TrackListComponent,
    TrackActionsComponent,
    ClickOutsideDirective,
    PlaylistModalComponent,
    ConfirmDialogComponent,
    TrackActionComponent,
    ListActionComponent,
    EditPlaylistComponent,
    EditTrackComponent,
    FormatDatePipe,
    FollowingCircleComponent,
    NotificationComponent,
    LoadingComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgSelectModule,
    DragDropModule,
    RouterModule,
    MatButtonModule,
    MatDialogModule,
  ],
  providers: [],
  exports: [
    HeaderComponent,
    AuthModalComponent,
    SoundCardComponent,
    RelatedTrackComponent,
    TrackCardComponent,
    SideBarComponent,
    WavesurferComponent,
    ArtistCardComponent,
    CardComponent,
    CardListComponent,
    RecommendedCardComponent,
    NextPlaylistComponent,
    NextUpItemComponent,
    TrackInfoAndWaveComponent,
    DynamicFormComponent,
    NgSelectModule,
    TimeAgoPipe,
    TrackActionsComponent,
    ClickOutsideDirective,
    PlaylistModalComponent,
    TrackListComponent,
    ConfirmDialogComponent,
    TrackActionComponent,
    ListActionComponent,
    FormatDatePipe,
    FollowingCircleComponent,
    NotificationComponent,
    LoadingComponent,
  ],
})
export class SharedModule {}
