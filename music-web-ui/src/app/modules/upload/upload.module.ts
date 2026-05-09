import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UploadRoutingModule } from './upload-routing.module';
import { UploadComponent } from './upload/upload.component';
import { UploadTrackComponent } from './upload/upload-track/upload-track.component';
import { FormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { AngularPinturaModule } from '@pqina/angular-pintura';
import { UploadAlbumComponent } from './upload/upload-album/upload-album.component';
import { SharedModule } from '../../shared/shared.module';
import { EditTrackComponent } from './upload/edit-track/edit-track.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
@NgModule({
  declarations: [
    UploadComponent,
    UploadTrackComponent,
    UploadAlbumComponent,
    EditTrackComponent,
  ],
  imports: [
    CommonModule,
    UploadRoutingModule,
    FormsModule,
    NgSelectModule,
    AngularPinturaModule,
    SharedModule,
    DragDropModule,
  ],
})
export class UploadModule {}
