import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TrackManagementRoutingModule } from './track-management-routing.module';
import { TrackManagementComponent } from './track-management.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatButton } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatDivider } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { TrackTableComponent } from './track-table/track-table.component';

import { SharedModule } from '../../shared/shared.module';
import { AudioPlayerComponent } from './audio-player/audio-player.component';
import { FormsModule } from '@angular/forms';
@NgModule({
  declarations: [
    TrackManagementComponent,
    TrackTableComponent,
    AudioPlayerComponent,
  ],
  imports: [
    CommonModule,
    TrackManagementRoutingModule,
    MatSidenavModule,
    MatButton,
    MatMenuModule,
    MatDivider,
    MatIconModule,
    MatCheckboxModule,
    MatTableModule,
    SharedModule,
    FormsModule,
  ],
})
export class TrackManagementModule {}
